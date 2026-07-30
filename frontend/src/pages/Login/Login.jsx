import "./Login.css";

import {
    Link,
    useLocation,
    useNavigate
} from "react-router-dom";

import {
    Eye,
    EyeOff,
    Lock,
    Mail
} from "lucide-react";

import { FcGoogle } from "react-icons/fc";
import { useRef, useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

import { useAuth } from "../../context/AuthContext";
import {
    getUserRole,
    USER_ROLES
} from "../../utils/constants";

function Login() {
    const [showPassword, setShowPassword] =
        useState(false);

    const [
        isGoogleSubmitting,
        setIsGoogleSubmitting
    ] = useState(false);

    const isLoginProcessing = useRef(false);

    const {
        login,
        loginWithGoogle
    } = useAuth();

    const location = useLocation();
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        setError,
        formState: {
            errors,
            isSubmitting
        }
    } = useForm({
        defaultValues: {
            correo: "",
            password: ""
        }
    });

    const redirectAfterLogin = (authResponse) => {
        const requestedLocation =
            location.state?.from;

        let destination;

        if (requestedLocation?.pathname) {
            destination =
                `${requestedLocation.pathname}`
                + `${requestedLocation.search ?? ""}`
                + `${requestedLocation.hash ?? ""}`;
        } else {
            const authenticatedUser =
                authResponse?.usuario;

            const userRole =
                getUserRole(authenticatedUser);

            destination =
                userRole === USER_ROLES.ADMIN
                    ? "/admin"
                    : userRole === USER_ROLES.REFUGIO
                        ? "/refugio"
                        : "/perfil";
        }

        toast.success(
            "Inicio de sesión correcto"
        );

        navigate(destination, {
            replace: true
        });
    };

    const onSubmit = async (data) => {
        if (isLoginProcessing.current) {
            return;
        }

        isLoginProcessing.current = true;

        try {
            const authResponse = await login({
                correo: data.correo.trim(),
                password: data.password
            });

            redirectAfterLogin(authResponse);
        } catch (error) {
            const status =
                error.response?.status;

            const responseData =
                error.response?.data;

            if (
                status === 400
                && responseData?.validationErrors
            ) {
                Object.entries(
                    responseData.validationErrors
                ).forEach(([field, message]) => {
                    setError(field, {
                        type: "server",
                        message
                    });
                });
            }

            if (status === 401) {
                setError("password", {
                    type: "server",
                    message:
                        responseData?.message
                        ?? "Correo o contraseña incorrectos"
                });
            }

            toast.error(
                responseData?.message
                ?? (
                    error.response
                        ? "No fue posible iniciar sesión"
                        : "No fue posible conectar con el servidor"
                )
            );
        } finally {
            isLoginProcessing.current = false;
        }
    };

    const handleGoogleLogin = async () => {
        if (isLoginProcessing.current) {
            return;
        }

        isLoginProcessing.current = true;
        setIsGoogleSubmitting(true);

        try {
            const authResponse =
                await loginWithGoogle();

            if (authResponse) {
                redirectAfterLogin(authResponse);
            }
        } catch (error) {
            const quietErrors = [
                "auth/popup-closed-by-user",
                "auth/cancelled-popup-request"
            ];

            if (
                quietErrors.includes(error.code)
            ) {
                return;
            }

            const firebaseMessages = {
                "auth/popup-blocked":
                    "El navegador bloqueó la ventana de Google. Habilita las ventanas emergentes e inténtalo de nuevo.",

                "auth/unauthorized-domain":
                    "Este dominio no está autorizado para iniciar sesión con Google.",

                "auth/configuration-not-found":
                    "La configuración de Google no corresponde al proyecto Firebase.",

                "auth/invalid-api-key":
                    "La API key configurada para Firebase no es válida.",

                "auth/operation-not-allowed":
                    "El inicio de sesión con Google no está habilitado.",

                "auth/network-request-failed":
                    "No se pudo conectar con Google. Revisa tu conexión e inténtalo de nuevo."
            };

            const firebaseError =
                error.code?.startsWith("auth/")
                    ? `${
                        error.message
                        ?? "Error de Firebase"
                    } (${error.code})`
                    : null;

            toast.error(
                firebaseMessages[error.code]
                ?? error.response?.data?.message
                ?? firebaseError
                ?? "No fue posible iniciar sesión con Google"
            );
        } finally {
            isLoginProcessing.current = false;
            setIsGoogleSubmitting(false);
        }
    };

    return (
        <section className="login-page">
            <div className="login-card">
                <div className="login-header">
                    <h1>
                        Huellitas Oaxaca
                    </h1>

                    <p>
                        Bienvenidos de nuevo
                    </p>
                </div>

                <h2>
                    Iniciar Sesión
                </h2>

                <form
                    onSubmit={handleSubmit(onSubmit)}
                    noValidate
                >
                    <label htmlFor="correo">
                        Correo electrónico
                    </label>

                    <div className="input-group">
                        <Mail
                            size={18}
                            aria-hidden="true"
                        />

                        <input
                            id="correo"
                            type="email"
                            placeholder="tu@correo.com"
                            autoComplete="email"
                            {...register("correo", {
                                required:
                                    "El correo es obligatorio.",

                                pattern: {
                                    value:
                                        /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,

                                    message:
                                        "El correo no tiene un formato válido."
                                }
                            })}
                        />
                    </div>

                    {errors.correo && (
                        <p className="error">
                            {errors.correo.message}
                        </p>
                    )}

                    <label htmlFor="password">
                        Contraseña
                    </label>

                    <div className="input-group">
                        <Lock
                            size={18}
                            aria-hidden="true"
                        />

                        <input
                            id="password"
                            type={
                                showPassword
                                    ? "text"
                                    : "password"
                            }
                            placeholder="********"
                            autoComplete="current-password"
                            {...register("password", {
                                required:
                                    "La contraseña es obligatoria."
                            })}
                        />

                        <button
                            type="button"
                            className="eye-btn"
                            onClick={() => {
                                setShowPassword(
                                    (visible) => !visible
                                );
                            }}
                            aria-label={
                                showPassword
                                    ? "Ocultar contraseña"
                                    : "Mostrar contraseña"
                            }
                            aria-pressed={showPassword}
                        >
                            {showPassword
                                ? <EyeOff size={18} />
                                : <Eye size={18} />}
                        </button>
                    </div>

                    {errors.password && (
                        <p className="error">
                            {errors.password.message}
                        </p>
                    )}

                    <p className="forgot">
                        <Link to="/recuperar-password">
                            ¿Olvidaste tu contraseña?
                        </Link>
                    </p>

                    <button
                        type="submit"
                        className="login-button"
                        disabled={
                            isSubmitting
                            || isGoogleSubmitting
                        }
                    >
                        {isSubmitting
                            ? "Ingresando..."
                            : "Iniciar sesión"}
                    </button>
                </form>

                <div className="divider">
                    <span>o</span>
                </div>

                <button
                    type="button"
                    className="google-button"
                    onClick={handleGoogleLogin}
                    disabled={
                        isSubmitting
                        || isGoogleSubmitting
                    }
                >
                    <FcGoogle
                        size={20}
                        aria-hidden="true"
                    />

                    {isGoogleSubmitting
                        ? "Conectando con Google..."
                        : "Continuar con Google"}
                </button>

                <p className="register">
                    ¿No tienes cuenta?

                    <Link to="/register">
                        Crear una cuenta
                    </Link>
                </p>
            </div>
        </section>
    );
}

export default Login;
