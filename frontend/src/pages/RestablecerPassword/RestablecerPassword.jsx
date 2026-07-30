import "./RestablecerPassword.css";

import { Eye, EyeOff, Lock } from "lucide-react";
import {
    useEffect,
    useRef,
    useState
} from "react";
import { Link, useSearchParams } from "react-router-dom";

import api from "../../services/api";

const PASSWORD_PATTERN =
    /^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/;

function isValidSuccessResponse(response) {
    return Boolean(
        response.status === 200
        && response.data
        && typeof response.data === "object"
        && typeof response.data.mensaje === "string"
        && response.data.mensaje.trim()
    );
}

function getSafeBackendMessage(error) {
    const message = error.response?.data?.message;

    return typeof message === "string" && message.trim()
        ? message.trim()
        : null;
}

function getResetErrorMessage(error) {
    if (!error.response) {
        return "No fue posible conectar con el backend.";
    }

    const backendMessage = getSafeBackendMessage(error);

    switch (error.response.status) {
        case 400:
            return backendMessage
                || "Revisa los campos del formulario.";
        case 401:
            return "La sesión no es válida.";
        case 403:
            return "No tienes permiso para realizar esta operación.";
        case 422:
            return backendMessage
                || "El enlace de recuperación no es válido.";
        case 500:
            return "Ocurrió un error interno en el servidor.";
        default:
            return backendMessage
                || "No fue posible restablecer la contraseña.";
    }
}

function validatePassword(value) {
    if (!value) {
        return "La nueva contraseña es obligatoria";
    }

    if (value.length < 8 || value.length > 14) {
        return "La contraseña debe tener entre 8 y 14 caracteres";
    }

    if (!PASSWORD_PATTERN.test(value)) {
        return "La contraseña debe contener una mayúscula, un número y un carácter especial";
    }

    return "";
}

function RestablecerPassword() {
    const [searchParams] = useSearchParams();
    const tokenValue = searchParams.get("token");
    const token = typeof tokenValue === "string"
        ? tokenValue.trim()
        : "";
    const validTokenParameter = Boolean(
        token
        && token.length <= 200
    );

    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [fieldErrors, setFieldErrors] = useState({});
    const [generalError, setGeneralError] = useState("");
    const [tokenRejected, setTokenRejected] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [success, setSuccess] = useState(false);

    const submissionLockRef = useRef(false);
    const mountedRef = useRef(false);

    useEffect(() => {
        mountedRef.current = true;

        return () => {
            mountedRef.current = false;
        };
    }, []);

    const handleNewPasswordChange = (event) => {
        setNewPassword(event.target.value);
        setFieldErrors((currentErrors) => ({
            ...currentErrors,
            nuevaPassword: ""
        }));
        setGeneralError("");
    };

    const handleConfirmPasswordChange = (event) => {
        setConfirmPassword(event.target.value);
        setFieldErrors((currentErrors) => ({
            ...currentErrors,
            confirmarPassword: ""
        }));
        setGeneralError("");
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (
            !validTokenParameter
            || submissionLockRef.current
            || isSubmitting
            || success
        ) {
            return;
        }

        const nextErrors = {
            nuevaPassword: validatePassword(newPassword),
            confirmarPassword: !confirmPassword
                ? "La confirmación de contraseña es obligatoria"
                : confirmPassword !== newPassword
                    ? "Las contraseñas no coinciden"
                    : ""
        };

        setFieldErrors(nextErrors);

        if (
            nextErrors.nuevaPassword
            || nextErrors.confirmarPassword
        ) {
            return;
        }

        submissionLockRef.current = true;
        setIsSubmitting(true);
        setGeneralError("");
        setTokenRejected(false);

        try {
            const response = await api.post(
                "/auth/restablecer-password",
                {
                    token,
                    nuevaPassword: newPassword,
                    confirmarPassword: confirmPassword
                }
            );

            if (!isValidSuccessResponse(response)) {
                if (mountedRef.current) {
                    setGeneralError(
                        "El backend devolvió una respuesta de restablecimiento no compatible."
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
                && validationErrors
                && typeof validationErrors === "object"
            ) {
                setFieldErrors((currentErrors) => ({
                    ...currentErrors,
                    nuevaPassword:
                        validationErrors.nuevaPassword || "",
                    confirmarPassword:
                        validationErrors.confirmarPassword || ""
                }));

                if (typeof validationErrors.token === "string") {
                    setGeneralError(validationErrors.token);
                    setTokenRejected(true);
                }
            } else {
                const message = getResetErrorMessage(error);

                setGeneralError(message);
                setTokenRejected(
                    error.response?.status === 422
                    && message.toLowerCase().includes("token")
                );
            }
        } finally {
            submissionLockRef.current = false;

            if (mountedRef.current) {
                setIsSubmitting(false);
            }
        }
    };

    if (!validTokenParameter) {
        return (
            <section
                className="reset-page"
                aria-labelledby="reset-missing-title"
            >
                <div
                    className="reset-card reset-status"
                    role="alert"
                >
                    <h1 id="reset-missing-title">
                        Enlace no válido
                    </h1>

                    <p>
                        Solicita un nuevo enlace para restablecer tu
                        contraseña.
                    </p>

                    <div className="reset-links">
                        <Link to="/recuperar-password">
                            Solicitar otro enlace
                        </Link>

                        <Link to="/login">
                            Volver al inicio de sesión
                        </Link>
                    </div>
                </div>
            </section>
        );
    }

    if (success) {
        return (
            <section
                className="reset-page"
                aria-labelledby="reset-success-title"
            >
                <div
                    className="reset-card reset-status reset-success"
                    role="status"
                >
                    <h1 id="reset-success-title">
                        Contraseña restablecida
                    </h1>

                    <p>
                        La contraseña se restableció correctamente.
                        Ya puedes iniciar sesión con la nueva contraseña.
                    </p>

                    <Link
                        to="/login"
                        className="reset-primary-link"
                    >
                        Ir al inicio de sesión
                    </Link>
                </div>
            </section>
        );
    }

    return (
        <section
            className="reset-page"
            aria-labelledby="reset-title"
        >
            <div className="reset-card">
                <p className="reset-eyebrow">
                    Huellitas Oaxaca
                </p>

                <h1 id="reset-title">
                    Restablecer contraseña
                </h1>

                <p className="reset-description">
                    Crea una contraseña de 8 a 14 caracteres con una
                    mayúscula, un número y un carácter especial.
                </p>

                <form
                    className="reset-form"
                    onSubmit={handleSubmit}
                    noValidate
                >
                    <label htmlFor="reset-new-password">
                        Nueva contraseña
                    </label>

                    <div
                        className={
                            fieldErrors.nuevaPassword
                                ? "reset-input-group has-error"
                                : "reset-input-group"
                        }
                    >
                        <Lock size={18} aria-hidden="true" />

                        <input
                            id="reset-new-password"
                            type={showNewPassword ? "text" : "password"}
                            autoComplete="new-password"
                            value={newPassword}
                            minLength="8"
                            maxLength="14"
                            aria-invalid={
                                Boolean(fieldErrors.nuevaPassword)
                            }
                            aria-describedby={
                                fieldErrors.nuevaPassword
                                    ? "reset-new-password-error reset-password-help"
                                    : "reset-password-help"
                            }
                            disabled={isSubmitting}
                            onChange={handleNewPasswordChange}
                        />

                        <button
                            type="button"
                            className="reset-visibility-button"
                            aria-label={
                                showNewPassword
                                    ? "Ocultar nueva contraseña"
                                    : "Mostrar nueva contraseña"
                            }
                            aria-pressed={showNewPassword}
                            disabled={isSubmitting}
                            onClick={() => {
                                setShowNewPassword(
                                    (visible) => !visible
                                );
                            }}
                        >
                            {showNewPassword
                                ? <EyeOff size={19} aria-hidden="true" />
                                : <Eye size={19} aria-hidden="true" />}
                        </button>
                    </div>

                    <p
                        id="reset-password-help"
                        className="reset-help"
                    >
                        Entre 8 y 14 caracteres, con mayúscula,
                        número y carácter especial.
                    </p>

                    {fieldErrors.nuevaPassword && (
                        <p
                            id="reset-new-password-error"
                            className="reset-field-error"
                            role="alert"
                        >
                            {fieldErrors.nuevaPassword}
                        </p>
                    )}

                    <label htmlFor="reset-confirm-password">
                        Confirmar contraseña
                    </label>

                    <div
                        className={
                            fieldErrors.confirmarPassword
                                ? "reset-input-group has-error"
                                : "reset-input-group"
                        }
                    >
                        <Lock size={18} aria-hidden="true" />

                        <input
                            id="reset-confirm-password"
                            type={
                                showConfirmPassword
                                    ? "text"
                                    : "password"
                            }
                            autoComplete="new-password"
                            value={confirmPassword}
                            aria-invalid={
                                Boolean(fieldErrors.confirmarPassword)
                            }
                            aria-describedby={
                                fieldErrors.confirmarPassword
                                    ? "reset-confirm-password-error"
                                    : undefined
                            }
                            disabled={isSubmitting}
                            onChange={handleConfirmPasswordChange}
                        />

                        <button
                            type="button"
                            className="reset-visibility-button"
                            aria-label={
                                showConfirmPassword
                                    ? "Ocultar confirmación de contraseña"
                                    : "Mostrar confirmación de contraseña"
                            }
                            aria-pressed={showConfirmPassword}
                            disabled={isSubmitting}
                            onClick={() => {
                                setShowConfirmPassword(
                                    (visible) => !visible
                                );
                            }}
                        >
                            {showConfirmPassword
                                ? <EyeOff size={19} aria-hidden="true" />
                                : <Eye size={19} aria-hidden="true" />}
                        </button>
                    </div>

                    {fieldErrors.confirmarPassword && (
                        <p
                            id="reset-confirm-password-error"
                            className="reset-field-error"
                            role="alert"
                        >
                            {fieldErrors.confirmarPassword}
                        </p>
                    )}

                    {generalError && (
                        <div
                            className="reset-general-error"
                            role="alert"
                        >
                            <p>{generalError}</p>

                            {tokenRejected && (
                                <Link to="/recuperar-password">
                                    Solicitar un nuevo enlace
                                </Link>
                            )}
                        </div>
                    )}

                    <button
                        type="submit"
                        className="reset-submit"
                        disabled={isSubmitting}
                    >
                        {isSubmitting
                            ? "Restableciendo..."
                            : "Restablecer contraseña"}
                    </button>
                </form>

                <Link to="/login" className="reset-login-link">
                    Volver al inicio de sesión
                </Link>
            </div>
        </section>
    );
}

export default RestablecerPassword;
