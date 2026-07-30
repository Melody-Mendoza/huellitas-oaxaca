package com.huellitasoaxaca.backend.services;

import org.springframework.web.multipart.MultipartFile;

public interface MascotaImagenStorageService
{
    String guardar(MultipartFile imagen);

    void eliminarSiExiste(String url);
}
