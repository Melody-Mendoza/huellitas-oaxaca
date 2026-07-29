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

    public WebConfig(
            @Value("${app.storage.perfil-fotos-dir}") String rutaDirectorio
    )
    {
        this.directorioFotos = Path.of(rutaDirectorio)
                .toAbsolutePath()
                .normalize();
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
    }
}
