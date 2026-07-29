package com.huellitasoaxaca.backend.services;

import org.springframework.web.multipart.MultipartFile;

public interface PerfilFotoStorageService
{
    String guardar(MultipartFile foto);

    void eliminarSiExiste(String fotoPerfil);
}
