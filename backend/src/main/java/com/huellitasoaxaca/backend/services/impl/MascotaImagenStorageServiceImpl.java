package com.huellitasoaxaca.backend.services.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.huellitasoaxaca.backend.exception.ImagenMascotaException;
import com.huellitasoaxaca.backend.services.MascotaImagenStorageService;

@Service
public class MascotaImagenStorageServiceImpl
        implements MascotaImagenStorageService
{
    private static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
    private static final long PIXELES_MAXIMOS = 16_000_000L;
    private static final String URL_PUBLICA = "/media/mascotas/";
    private static final Pattern NOMBRE_ADMINISTRADO = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png)$"
    );

    private final Path directorio;

    public MascotaImagenStorageServiceImpl(
            @Value("${app.storage.mascota-imagenes-dir:}") String ruta
    )
    {
        String configurada = ruta == null ? "" : ruta.trim();
        Path predeterminada = Path.of(
                System.getProperty("java.io.tmpdir"),
                "huellitas-oaxaca",
                "mascota-imagenes"
        );
        this.directorio = prepararDirectorio(
                configurada.isEmpty() ? predeterminada : Path.of(configurada)
        );
    }

    @Override
    public String guardar(MultipartFile imagen)
    {
        validarArchivoPresente(imagen);
        byte[] contenido = leerContenido(imagen);
        FormatoImagen formato = validarImagen(
                contenido,
                imagen.getContentType()
        );
        String nombre = UUID.randomUUID() + "." + formato.extension;
        Path destino = directorio.resolve(nombre).normalize();
        Path temporal = null;

        validarDentroDelDirectorio(destino);

        try
        {
            temporal = Files.createTempFile(
                    directorio,
                    ".mascota-",
                    ".tmp"
            );
            Files.write(
                    temporal,
                    contenido,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            moverSinSobrescribir(temporal, destino);
            temporal = null;

            return URL_PUBLICA + nombre;
        }
        catch (IOException | SecurityException exception)
        {
            throw new ImagenMascotaException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible almacenar la imagen de la mascota",
                    exception
            );
        }
        finally
        {
            eliminarTemporal(temporal);
        }
    }

    @Override
    public void eliminarSiExiste(String url)
    {
        if (url == null || !url.startsWith(URL_PUBLICA))
        {
            return;
        }

        Path archivo = resolverArchivoAdministrado(url);

        try
        {
            Files.deleteIfExists(archivo);
        }
        catch (IOException | SecurityException exception)
        {
            throw new ImagenMascotaException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible eliminar la imagen de la mascota",
                    exception
            );
        }
    }

    private Path prepararDirectorio(Path ruta)
    {
        try
        {
            Path normalizada = ruta.toAbsolutePath().normalize();
            Files.createDirectories(normalizada);

            if (!Files.isDirectory(normalizada)
                    || !Files.isReadable(normalizada)
                    || !Files.isWritable(normalizada))
            {
                throw errorAlmacenamiento();
            }

            return normalizada;
        }
        catch (InvalidPathException | IOException | SecurityException exception)
        {
            throw new ImagenMascotaException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible preparar el almacenamiento de imágenes",
                    exception
            );
        }
    }

    private void validarArchivoPresente(MultipartFile imagen)
    {
        if (imagen == null || imagen.isEmpty())
        {
            throw new ImagenMascotaException(
                    HttpStatus.BAD_REQUEST,
                    "La imagen no puede estar vacía"
            );
        }
        if (imagen.getSize() > TAMANO_MAXIMO)
        {
            throw errorTamano();
        }
    }

    private byte[] leerContenido(MultipartFile imagen)
    {
        try (InputStream entrada = imagen.getInputStream())
        {
            byte[] contenido = entrada.readNBytes(
                    Math.toIntExact(TAMANO_MAXIMO + 1)
            );

            if (contenido.length == 0)
            {
                throw new ImagenMascotaException(
                        HttpStatus.BAD_REQUEST,
                        "La imagen no puede estar vacía"
                );
            }
            if (contenido.length > TAMANO_MAXIMO)
            {
                throw errorTamano();
            }

            return contenido;
        }
        catch (IOException exception)
        {
            throw new ImagenMascotaException(
                    HttpStatus.BAD_REQUEST,
                    "No fue posible leer la imagen",
                    exception
            );
        }
    }

    private FormatoImagen validarImagen(byte[] contenido, String mimeDeclarado)
    {
        FormatoImagen declarado = FormatoImagen.desdeMime(mimeDeclarado);

        if (declarado == null)
        {
            throw errorTipo();
        }

        try (ImageInputStream entrada = ImageIO.createImageInputStream(
                new ByteArrayInputStream(contenido)
        ))
        {
            if (entrada == null)
            {
                throw errorContenido();
            }

            Iterator<ImageReader> lectores = ImageIO.getImageReaders(entrada);
            if (!lectores.hasNext())
            {
                throw errorContenido();
            }

            ImageReader lector = lectores.next();
            try
            {
                lector.setInput(entrada, false, true);
                FormatoImagen real = FormatoImagen.desdeFormato(
                        lector.getFormatName()
                );

                if (real == null)
                {
                    throw errorTipo();
                }
                if (real != declarado)
                {
                    throw new ImagenMascotaException(
                            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                            "El tipo declarado no corresponde al contenido de la imagen"
                    );
                }

                int ancho = lector.getWidth(0);
                int alto = lector.getHeight(0);
                if (ancho <= 0
                        || alto <= 0
                        || (long) ancho * alto > PIXELES_MAXIMOS)
                {
                    throw new ImagenMascotaException(
                            HttpStatus.BAD_REQUEST,
                            "Las dimensiones de la imagen no están permitidas"
                    );
                }

                BufferedImage decodificada = lector.read(0);
                if (decodificada == null)
                {
                    throw errorContenido();
                }

                return real;
            }
            finally
            {
                lector.dispose();
            }
        }
        catch (ImagenMascotaException exception)
        {
            throw exception;
        }
        catch (IOException | RuntimeException exception)
        {
            throw new ImagenMascotaException(
                    HttpStatus.BAD_REQUEST,
                    "El archivo no contiene una imagen válida",
                    exception
            );
        }
    }

    private Path resolverArchivoAdministrado(String url)
    {
        String nombre = url.substring(URL_PUBLICA.length());
        if (!NOMBRE_ADMINISTRADO.matcher(nombre).matches())
        {
            throw errorRuta();
        }

        Path archivo = directorio.resolve(nombre).normalize();
        validarDentroDelDirectorio(archivo);

        return archivo;
    }

    private void validarDentroDelDirectorio(Path archivo)
    {
        if (!archivo.startsWith(directorio)
                || !directorio.equals(archivo.getParent()))
        {
            throw errorRuta();
        }
    }

    private void moverSinSobrescribir(Path origen, Path destino)
            throws IOException
    {
        if (Files.exists(destino))
        {
            throw new IOException("El archivo de destino ya existe");
        }

        try
        {
            Files.move(origen, destino, StandardCopyOption.ATOMIC_MOVE);
        }
        catch (AtomicMoveNotSupportedException exception)
        {
            Files.move(origen, destino);
        }
    }

    private void eliminarTemporal(Path temporal)
    {
        if (temporal == null)
        {
            return;
        }

        try
        {
            Files.deleteIfExists(temporal);
        }
        catch (IOException | SecurityException ignored)
        {
            // El error original de almacenamiento tiene prioridad.
        }
    }

    private ImagenMascotaException errorAlmacenamiento()
    {
        return new ImagenMascotaException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "No fue posible preparar el almacenamiento de imágenes"
        );
    }

    private ImagenMascotaException errorTamano()
    {
        return new ImagenMascotaException(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "La imagen no debe superar 5 MiB"
        );
    }

    private ImagenMascotaException errorTipo()
    {
        return new ImagenMascotaException(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El tipo de archivo no está permitido. Usa JPEG o PNG"
        );
    }

    private ImagenMascotaException errorContenido()
    {
        return new ImagenMascotaException(
                HttpStatus.BAD_REQUEST,
                "El archivo no contiene una imagen válida"
        );
    }

    private ImagenMascotaException errorRuta()
    {
        return new ImagenMascotaException(
                HttpStatus.BAD_REQUEST,
                "La ruta de la imagen no es válida"
        );
    }

    private enum FormatoImagen
    {
        JPEG("image/jpeg", "jpg"),
        PNG("image/png", "png");

        private final String mime;
        private final String extension;

        FormatoImagen(String mime, String extension)
        {
            this.mime = mime;
            this.extension = extension;
        }

        private static FormatoImagen desdeMime(String mime)
        {
            if (mime == null)
            {
                return null;
            }

            for (FormatoImagen formato : values())
            {
                if (formato.mime.equals(mime.toLowerCase(Locale.ROOT)))
                {
                    return formato;
                }
            }

            return null;
        }

        private static FormatoImagen desdeFormato(String formato)
        {
            if (formato == null)
            {
                return null;
            }

            return switch (formato.toUpperCase(Locale.ROOT))
            {
                case "JPEG", "JPG" -> JPEG;
                case "PNG" -> PNG;
                default -> null;
            };
        }
    }
}
