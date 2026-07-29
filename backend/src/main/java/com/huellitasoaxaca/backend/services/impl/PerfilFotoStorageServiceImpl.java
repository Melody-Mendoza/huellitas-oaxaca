package com.huellitasoaxaca.backend.services.impl;

import java.awt.image.BufferedImage;
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

import com.huellitasoaxaca.backend.exception.FotoPerfilException;
import com.huellitasoaxaca.backend.services.PerfilFotoStorageService;

@Service
public class PerfilFotoStorageServiceImpl
        implements PerfilFotoStorageService
{
    private static final long TAMANO_MAXIMO = 5L * 1024 * 1024;
    private static final long PIXELES_MAXIMOS = 16_000_000L;
    private static final String URL_PUBLICA = "/media/perfiles/";
    private static final Pattern NOMBRE_ADMINISTRADO = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png)$"
    );

    private final Path directorio;

    public PerfilFotoStorageServiceImpl(
            @Value("${app.storage.perfil-fotos-dir}") String rutaDirectorio
    )
    {
        this.directorio = prepararDirectorio(rutaDirectorio);
    }

    @Override
    public String guardar(MultipartFile foto)
    {
        validarArchivoPresente(foto);

        byte[] contenido = leerContenido(foto);
        FormatoImagen formato = validarImagen(
                contenido,
                foto.getContentType()
        );
        String nombre = UUID.randomUUID() + "." + formato.extension();
        Path destino = directorio.resolve(nombre).normalize();
        Path temporal = null;

        validarDentroDelDirectorio(destino);

        try
        {
            temporal = Files.createTempFile(
                    directorio,
                    ".perfil-",
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
            throw new FotoPerfilException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible almacenar la foto de perfil",
                    exception
            );
        }
        finally
        {
            eliminarTemporal(temporal);
        }
    }

    @Override
    public void eliminarSiExiste(String fotoPerfil)
    {
        if (fotoPerfil == null)
        {
            return;
        }

        Path archivo = resolverArchivoAdministrado(fotoPerfil);

        try
        {
            Files.deleteIfExists(archivo);
        }
        catch (IOException | SecurityException exception)
        {
            throw new FotoPerfilException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible eliminar la foto de perfil",
                    exception
            );
        }
    }

    private Path prepararDirectorio(String rutaDirectorio)
    {
        if (rutaDirectorio == null || rutaDirectorio.isBlank())
        {
            throw new FotoPerfilException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible preparar el almacenamiento de fotos"
            );
        }

        try
        {
            Path ruta = Path.of(rutaDirectorio)
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(ruta);

            if (!Files.isDirectory(ruta)
                    || !Files.isReadable(ruta)
                    || !Files.isWritable(ruta))
            {
                throw new FotoPerfilException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "No fue posible preparar el almacenamiento de fotos"
                );
            }

            return ruta;
        }
        catch (InvalidPathException | IOException | SecurityException exception)
        {
            throw new FotoPerfilException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No fue posible preparar el almacenamiento de fotos",
                    exception
            );
        }
    }

    private void validarArchivoPresente(MultipartFile foto)
    {
        if (foto == null || foto.isEmpty())
        {
            throw new FotoPerfilException(
                    HttpStatus.BAD_REQUEST,
                    "La foto no puede estar vacía"
            );
        }

        if (foto.getSize() > TAMANO_MAXIMO)
        {
            throw crearErrorTamano();
        }
    }

    private byte[] leerContenido(MultipartFile foto)
    {
        try (InputStream entrada = foto.getInputStream())
        {
            byte[] contenido = entrada.readNBytes(
                    Math.toIntExact(TAMANO_MAXIMO + 1)
            );

            if (contenido.length == 0)
            {
                throw new FotoPerfilException(
                        HttpStatus.BAD_REQUEST,
                        "La foto no puede estar vacía"
                );
            }

            if (contenido.length > TAMANO_MAXIMO)
            {
                throw crearErrorTamano();
            }

            return contenido;
        }
        catch (IOException exception)
        {
            throw new FotoPerfilException(
                    HttpStatus.BAD_REQUEST,
                    "No fue posible leer la foto",
                    exception
            );
        }
    }

    private FormatoImagen validarImagen(
            byte[] contenido,
            String mimeDeclarado
    )
    {
        FormatoImagen formatoDeclarado = FormatoImagen.desdeMime(
                mimeDeclarado
        );

        if (formatoDeclarado == null)
        {
            throw new FotoPerfilException(
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "El tipo de archivo no está permitido. Usa JPEG o PNG"
            );
        }

        try (ImageInputStream entrada = ImageIO.createImageInputStream(
                new java.io.ByteArrayInputStream(contenido)
        ))
        {
            if (entrada == null)
            {
                throw crearErrorContenido();
            }

            Iterator<ImageReader> lectores = ImageIO.getImageReaders(entrada);

            if (!lectores.hasNext())
            {
                throw crearErrorContenido();
            }

            ImageReader lector = lectores.next();

            try
            {
                lector.setInput(entrada, false, true);

                FormatoImagen formatoReal = FormatoImagen.desdeFormato(
                        lector.getFormatName()
                );

                if (formatoReal == null)
                {
                    throw new FotoPerfilException(
                            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                            "El tipo de archivo no está permitido. Usa JPEG o PNG"
                    );
                }

                if (formatoReal != formatoDeclarado)
                {
                    throw new FotoPerfilException(
                            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                            "El tipo declarado no corresponde al contenido de la foto"
                    );
                }

                int ancho = lector.getWidth(0);
                int alto = lector.getHeight(0);

                if (ancho <= 0
                        || alto <= 0
                        || (long) ancho * alto > PIXELES_MAXIMOS)
                {
                    throw new FotoPerfilException(
                            HttpStatus.BAD_REQUEST,
                            "Las dimensiones de la foto no están permitidas"
                    );
                }

                BufferedImage imagen = lector.read(0);

                if (imagen == null)
                {
                    throw crearErrorContenido();
                }

                return formatoReal;
            }
            finally
            {
                lector.dispose();
            }
        }
        catch (FotoPerfilException exception)
        {
            throw exception;
        }
        catch (IOException | RuntimeException exception)
        {
            throw new FotoPerfilException(
                    HttpStatus.BAD_REQUEST,
                    "La foto no contiene una imagen válida",
                    exception
            );
        }
    }

    private Path resolverArchivoAdministrado(String fotoPerfil)
    {
        if (!fotoPerfil.startsWith(URL_PUBLICA))
        {
            throw crearErrorRuta();
        }

        String nombre = fotoPerfil.substring(URL_PUBLICA.length());

        if (!NOMBRE_ADMINISTRADO.matcher(nombre).matches())
        {
            throw crearErrorRuta();
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
            throw crearErrorRuta();
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

    private FotoPerfilException crearErrorTamano()
    {
        return new FotoPerfilException(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "La foto no debe superar 5 MiB"
        );
    }

    private FotoPerfilException crearErrorContenido()
    {
        return new FotoPerfilException(
                HttpStatus.BAD_REQUEST,
                "La foto no contiene una imagen válida"
        );
    }

    private FotoPerfilException crearErrorRuta()
    {
        return new FotoPerfilException(
                HttpStatus.BAD_REQUEST,
                "La ruta de la foto de perfil no es válida"
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

        private String extension()
        {
            return extension;
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
