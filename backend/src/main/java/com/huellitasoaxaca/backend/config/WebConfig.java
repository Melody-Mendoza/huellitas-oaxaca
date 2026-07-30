package com.huellitasoaxaca.backend.config;

import java.nio.file.Path;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
    private final Path directorioFotos;
    private final Path directorioImagenesMascotas;

    public WebConfig(
            @Value("${app.storage.perfil-fotos-dir}") String rutaDirectorio,
            @Value("${app.storage.mascota-imagenes-dir:}") String rutaMascotas
    )
    {
        this.directorioFotos = Path.of(rutaDirectorio)
                .toAbsolutePath()
                .normalize();
        this.directorioImagenesMascotas = resolverDirectorioMascotas(
                rutaMascotas
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        String ubicacion = directorioFotos.toUri().toString();

        if (!ubicacion.endsWith("/"))
        {
            ubicacion += "/";
        }

        registry.addResourceHandler("/media/perfiles/**")
                .addResourceLocations(ubicacion)
                .setCacheControl(
                        CacheControl.maxAge(Duration.ofDays(365))
                                .cachePublic()
                                .immutable()
                );

        String ubicacionMascotas = directorioImagenesMascotas
                .toUri()
                .toString();

        if (!ubicacionMascotas.endsWith("/"))
        {
            ubicacionMascotas += "/";
        }

        registry.addResourceHandler("/media/mascotas/**")
                .addResourceLocations(ubicacionMascotas)
                .setCacheControl(
                        CacheControl.maxAge(Duration.ofDays(365))
                                .cachePublic()
                                .immutable()
                );
    }

    private Path resolverDirectorioMascotas(String ruta)
    {
        if (ruta != null && !ruta.isBlank())
        {
            return Path.of(ruta).toAbsolutePath().normalize();
        }

        return Path.of(
                System.getProperty("java.io.tmpdir"),
                "huellitas-oaxaca",
                "mascota-imagenes"
        ).toAbsolutePath().normalize();
    }
}
