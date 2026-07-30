package com.huellitasoaxaca.backend.services.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.huellitasoaxaca.backend.entity.ImagenMascota;
import com.huellitasoaxaca.backend.entity.Mascota;
import com.huellitasoaxaca.backend.entity.Refugio;
import com.huellitasoaxaca.backend.entity.Rol;
import com.huellitasoaxaca.backend.entity.Usuario;
import com.huellitasoaxaca.backend.mapper.ImagenMascotaMapper;
import com.huellitasoaxaca.backend.repository.ImagenMascotaRepository;
import com.huellitasoaxaca.backend.repository.MascotaRepository;
import com.huellitasoaxaca.backend.repository.RefugioRepository;
import com.huellitasoaxaca.backend.repository.UsuarioRepository;
import com.huellitasoaxaca.backend.services.MascotaImagenStorageService;

@ExtendWith(MockitoExtension.class)
class ImagenMascotaServiceImplTest
{
    private static final String CORREO = "refugio@example.com";
    private static final String URL = "/media/mascotas/11111111-1111-1111-1111-111111111111.jpg";

    @Mock
    private ImagenMascotaRepository imagenRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private RefugioRepository refugioRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ImagenMascotaMapper imagenMapper;
    @Mock
    private MascotaImagenStorageService storageService;
    @Mock
    private MultipartFile archivo;

    @InjectMocks
    private ImagenMascotaServiceImpl service;

    @BeforeEach
    void prepararSincronizacion()
    {
        TransactionSynchronizationManager.initSynchronization();
    }

    @AfterEach
    void limpiarSincronizacion()
    {
        if (TransactionSynchronizationManager.isSynchronizationActive())
        {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void eliminaArchivoNuevoCuandoFallaPersistencia()
    {
        prepararPropiedad();
        when(imagenRepository.countByMascotaId(20L)).thenReturn(0L);
        when(storageService.guardar(archivo)).thenReturn(URL);
        when(imagenRepository.saveAndFlush(any(ImagenMascota.class)))
                .thenThrow(new IllegalStateException("fallo controlado"));

        assertThrows(
                IllegalStateException.class,
                () -> service.guardarPropia(10L, 20L, archivo, CORREO)
        );

        completarTransaccion(TransactionSynchronization.STATUS_ROLLED_BACK);

        verify(storageService).eliminarSiExiste(URL);
    }

    @Test
    void conservaArchivoNuevoCuandoConfirmaPersistencia()
    {
        prepararPropiedad();
        when(imagenRepository.countByMascotaId(20L)).thenReturn(0L);
        when(storageService.guardar(archivo)).thenReturn(URL);
        when(imagenRepository.saveAndFlush(any(ImagenMascota.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        service.guardarPropia(10L, 20L, archivo, CORREO);
        completarTransaccion(TransactionSynchronization.STATUS_COMMITTED);

        verify(storageService, never()).eliminarSiExiste(URL);
    }

    @Test
    void archivoFisicoInexistenteNoImpideEliminarRegistro()
    {
        prepararPropiedad();
        ImagenMascota imagen = ImagenMascota.builder()
                .id(30L)
                .url(URL)
                .principal(false)
                .mascota(crearMascota())
                .build();
        when(imagenRepository.findByIdAndMascotaId(30L, 20L))
                .thenReturn(Optional.of(imagen));

        service.eliminarPropia(10L, 20L, 30L, CORREO);

        assertDoesNotThrow(() -> completarTransaccion(
                TransactionSynchronization.STATUS_COMMITTED
        ));
        verify(imagenRepository).delete(imagen);
        verify(storageService).eliminarSiExiste(URL);
    }

    @Test
    void noEliminaRutaFueraDelDirectorioAdministrado(
            @TempDir Path temporal
    ) throws Exception
    {
        Path administrado = temporal.resolve("administrado");
        Path externo = temporal.resolve("fuera.jpg");
        Files.write(externo, new byte[] { 1, 2, 3 });
        MascotaImagenStorageServiceImpl storage =
                new MascotaImagenStorageServiceImpl(
                        administrado.toString()
                );

        storage.eliminarSiExiste(externo.toAbsolutePath().toString());

        assertTrue(Files.exists(externo));
    }

    private void prepararPropiedad()
    {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .correo(CORREO)
                .activo(true)
                .rol(Rol.builder().nombre("REFUGIO").build())
                .build();
        Refugio refugio = Refugio.builder()
                .id(10L)
                .activo(true)
                .usuario(usuario)
                .build();
        Mascota mascota = crearMascota();

        when(usuarioRepository.findByCorreoAndActivoTrue(CORREO))
                .thenReturn(Optional.of(usuario));
        when(refugioRepository.findByIdAndUsuarioId(10L, 1L))
                .thenReturn(Optional.of(refugio));
        when(mascotaRepository.findPropiaParaActualizar(20L, 10L))
                .thenReturn(Optional.of(mascota));
    }

    private Mascota crearMascota()
    {
        return Mascota.builder()
                .id(20L)
                .refugio(Refugio.builder().id(10L).activo(true).build())
                .build();
    }

    private void completarTransaccion(int estado)
    {
        TransactionSynchronizationManager.getSynchronizations()
                .forEach(sincronizacion ->
                        sincronizacion.afterCompletion(estado)
                );
    }
}
