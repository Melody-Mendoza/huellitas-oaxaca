package com.huellitasoaxaca.backend.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.huellitasoaxaca.backend.dto.response.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler 
{
        @ExceptionHandler(ParametroInvalidoException.class)
        public ResponseEntity<ErrorResponse> manejarParametroInvalido(
                ParametroInvalidoException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.BAD_REQUEST,
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity.badRequest().body(respuesta);
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponse> manejarTipoParametroInvalido(
                MethodArgumentTypeMismatchException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.BAD_REQUEST,
                        "El parámetro '" + exception.getName()
                                + "' no tiene un valor válido",
                        request.getRequestURI()
                );

                return ResponseEntity.badRequest().body(respuesta);
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponse> manejarCuerpoNoLegible(
                HttpMessageNotReadableException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.BAD_REQUEST,
                        "El cuerpo de la solicitud no es válido",
                        request.getRequestURI()
                );

                return ResponseEntity.badRequest().body(respuesta);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> manejarValidaciones(
                MethodArgumentNotValidException exception,
                HttpServletRequest request
        ) 
        {
                Map<String, String> errores = new LinkedHashMap<>();

                exception.getBindingResult()
                        .getFieldErrors()
                        .forEach(error ->
                                errores.putIfAbsent(
                                        error.getField(),
                                        error.getDefaultMessage()
                                )
                        );

                ErrorResponse respuesta = new ErrorResponse(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        "Existen campos inválidos",
                        request.getRequestURI(),
                        errores
                );

                return ResponseEntity.badRequest().body(respuesta);
        }

        @ExceptionHandler(RecursoNoEncontradoException.class)
        public ResponseEntity<ErrorResponse> manejarRecursoNoEncontrado(
                RecursoNoEncontradoException exception,
                HttpServletRequest request
        ) {
                ErrorResponse respuesta = crearError(
                        HttpStatus.NOT_FOUND,
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }

        @ExceptionHandler(RecursoDuplicadoException.class)
        public ResponseEntity<ErrorResponse> manejarRecursoDuplicado(
                RecursoDuplicadoException exception,
                HttpServletRequest request
        ) 
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(respuesta);
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> manejarAccesoDenegado(
                AccessDeniedException exception,
                HttpServletRequest request
        ) 
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.FORBIDDEN,
                        "No tienes permiso para acceder a este recurso",
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(respuesta);
        }

        @ExceptionHandler(TokenRecuperacionInvalidoException.class)
        public ResponseEntity<ErrorResponse> manejarTokenRecuperacionInvalido(
                TokenRecuperacionInvalidoException exception,
                HttpServletRequest request
        ) 
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(respuesta);
        }

        @ExceptionHandler(ReglaNegocioException.class)
        public ResponseEntity<ErrorResponse> manejarReglaNegocio(
                ReglaNegocioException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(HttpStatus.UNPROCESSABLE_ENTITY)
                        .body(respuesta);
        }

        @ExceptionHandler(GoogleAuthenticationException.class)
        public ResponseEntity<ErrorResponse> manejarAutenticacionGoogle(
                GoogleAuthenticationException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        exception.getStatus(),
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(exception.getStatus())
                        .body(respuesta);
        }

        @ExceptionHandler(FotoPerfilException.class)
        public ResponseEntity<ErrorResponse> manejarFotoPerfil(
                FotoPerfilException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        exception.getStatus(),
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(exception.getStatus())
                        .body(respuesta);
        }

        @ExceptionHandler(ConflictoAdministrativoException.class)
        public ResponseEntity<ErrorResponse> manejarConflictoAdministrativo(
                ConflictoAdministrativoException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.CONFLICT,
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(respuesta);
        }

        @ExceptionHandler(ImagenMascotaException.class)
        public ResponseEntity<ErrorResponse> manejarImagenMascota(
                ImagenMascotaException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        exception.getStatus(),
                        exception.getMessage(),
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(exception.getStatus())
                        .body(respuesta);
        }

        @ExceptionHandler(MissingServletRequestPartException.class)
        public ResponseEntity<ErrorResponse> manejarParteMultipartAusente(
                MissingServletRequestPartException exception,
                HttpServletRequest request
        )
        {
                String nombreParte = exception.getRequestPartName();
                ErrorResponse respuesta = crearError(
                        HttpStatus.BAD_REQUEST,
                        "Debe enviar el archivo en el campo '"
                                + nombreParte + "'",
                        request.getRequestURI()
                );

                return ResponseEntity.badRequest().body(respuesta);
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ErrorResponse> manejarTamanoExcedido(
                MaxUploadSizeExceededException exception,
                HttpServletRequest request
        )
        {
                boolean imagenMascota = request.getRequestURI()
                        .contains("/imagenes");
                ErrorResponse respuesta = crearError(
                        HttpStatus.PAYLOAD_TOO_LARGE,
                        imagenMascota
                                ? "La imagen no debe superar 5 MiB"
                                : "La foto no debe superar 5 MiB",
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(respuesta);
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponse> manejarRecursoInexistente(
                NoResourceFoundException exception,
                HttpServletRequest request
        )
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.NOT_FOUND,
                        "No se encontró el recurso solicitado",
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(respuesta);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> manejarErrorGeneral(
                Exception exception,
                HttpServletRequest request
        ) 
        {
                ErrorResponse respuesta = crearError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ocurrió un error interno en el servidor",
                        request.getRequestURI()
                );

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(respuesta);
        }

        private ErrorResponse crearError(
                HttpStatus estado,
                String mensaje,
                String ruta
        ) 
        {
                return new ErrorResponse(
                        LocalDateTime.now(),
                        estado.value(),
                        estado.getReasonPhrase(),
                        mensaje,
                        ruta,
                        Map.of()
                );
        }

        @ExceptionHandler({
                BadCredentialsException.class,
                UsernameNotFoundException.class
        })
        public ResponseEntity<ErrorResponse> manejarCredencialesIncorrectas(
                RuntimeException exception,
                HttpServletRequest request
        ) {
        ErrorResponse respuesta = crearError(
                HttpStatus.UNAUTHORIZED,
                "Correo o contraseña incorrectos",
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(respuesta);
        }
}
