import "./RecuperarPassword.css";

import { Mail } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

import api from "../../services/api";

const GENERIC_SUCCESS_MESSAGE =
    "Si el correo está registrado, recibirás instrucciones para restablecer tu contraseña";

function isValidSuccessResponse(response) {
    return Boolean(
        response.status === 200
        && response.data
        && typeof response.data === "object"
        && typeof response.data.mensaje === "string"
        && response.data.mensaje.trim()
    );
}

function getRecoveryErrorMessage(error) {
    if (!error.response) {
        return "No fue posible conectar con el backend.";
    }

    switch (error.response.status) {
        case 400:
            return "Revisa el correo ingresado.";
        case 401:
            return "La sesión no es válida.";
        case 403:
            return "No tienes permiso para realizar esta operación.";
        case 500:
            return "No fue posible procesar la solicitud en el servidor.";
        default:
            return "No fue posible solicitar la recuperación de contraseña.";
    }
}

function RecuperarPassword() {
    const [correo, setCorreo] = useState("");
    const [correoError, setCorreoError] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [success, setSuccess] = useState(false);
    const [generalError, setGeneralError] = useState("");
    const submissionLockRef = useRef(false);
    const mountedRef = useRef(false);

    useEffect(() => {
        mountedRef.current = true;

        return () => {
            mountedRef.current = false;
        };
    }, []);

    const handleCorreoChange = (event) => {
        setCorreo(event.target.value);
        setCorreoError("");
        setGeneralError("");
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (
            submissionLockRef.current
            || isSubmitting
            || success
        ) {
            return;
        }

        const normalizedCorreo = correo.trim();

        if (!normalizedCorreo) {
            setCorreoError("El correo es obligatorio");
            return;
        }

        if (normalizedCorreo.length > 150) {
            setCorreoError(
                "El correo no puede superar los 150 caracteres"
            );
            return;
        }

        if (
            !/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(
                normalizedCorreo
            )
        ) {
            setCorreoError(
                "El correo no tiene un formato válido"
            );
            return;
        }

        submissionLockRef.current = true;
        setIsSubmitting(true);
        setGeneralError("");
        setCorreoError("");

        try {
            const response = await api.post(
                "/auth/recuperar-password",
                {
                    correo: normalizedCorreo
                }
            );

            if (!isValidSuccessResponse(response)) {
                if (mountedRef.current) {
                    setGeneralError(
                        "El backend devolvió una respuesta de recuperación no compatible."
                    );
                }
                return;
            }

            if (mountedRef.current) {
                setSuccess(true);
            }
        } catch (error) {
            if (!mountedRef.current) {
                return;
            }

            const validationErrors =
                error.response?.data?.validationErrors;

            if (
                error.response?.status === 400
                && typeof validationErrors?.correo === "string"
            ) {
                setCorreoError(validationErrors.correo);
            } else {
                setGeneralError(getRecoveryErrorMessage(error));
            }
        } finally {
            submissionLockRef.current = false;

            if (mountedRef.current) {
                setIsSubmitting(false);
            }
        }
    };

    if (success) {
        return (
            <section
                className="recovery-page"
                aria-labelledby="recovery-success-title"
            >
                <div
                    className="recovery-card recovery-status"
                    role="status"
                >
                    <h1 id="recovery-success-title">
                        Revisa tu correo
                    </h1>

                    <p>{GENERIC_SUCCESS_MESSAGE}.</p>

                    <Link
                        to="/login"
                        className="recovery-link"
                    >
                        Volver al inicio de sesión
                    </Link>
                </div>
            </section>
        );
    }

    return (
        <section
            className="recovery-page"
            aria-labelledby="recovery-title"
        >
            <div className="recovery-card">
                <p className="recovery-eyebrow">
                    Huellitas Oaxaca
                </p>

                <h1 id="recovery-title">
                    Recuperar contraseña
                </h1>

                <p className="recovery-description">
                    Ingresa tu correo electrónico. Si está registrado,
                    recibirás un enlace válido durante 30 minutos.
                </p>

                <form
                    className="recovery-form"
                    onSubmit={handleSubmit}
                    noValidate
                >
                    <label htmlFor="recovery-correo">
                        Correo electrónico
                    </label>

                    <div
                        className={
                            correoError
                                ? "recovery-input-group has-error"
                                : "recovery-input-group"
                        }
                    >
                        <Mail size={18} aria-hidden="true" />

                        <input
                            id="recovery-correo"
                            type="email"
                            autoComplete="email"
                            value={correo}
                            maxLength="150"
                            aria-invalid={Boolean(correoError)}
                            aria-describedby={
                                correoError
                                    ? "recovery-correo-error"
                                    : undefined
                            }
                            disabled={isSubmitting}
                            onChange={handleCorreoChange}
                        />
                    </div>

                    {correoError && (
                        <p
                            id="recovery-correo-error"
                            className="recovery-field-error"
                            role="alert"
                        >
                            {correoError}
                        </p>
                    )}

                    {generalError && (
                        <p
                            className="recovery-general-error"
                            role="alert"
                        >
                            {generalError}
                        </p>
                    )}

                    <button
                        type="submit"
                        className="recovery-submit"
                        disabled={isSubmitting}
                    >
                        {isSubmitting
                            ? "Enviando..."
                            : "Enviar instrucciones"}
                    </button>
                </form>

                <Link to="/login" className="recovery-link">
                    Volver al inicio de sesión
                </Link>
            </div>
        </section>
    );
}

export default RecuperarPassword;
