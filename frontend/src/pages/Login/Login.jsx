import "./Login.css";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { Mail, Lock, Eye, EyeOff } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { useAuth } from "../../context/AuthContext";

function Login() {
    const [showPassword, setShowPassword] = useState(false);
    const { login } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        setError,
        formState: { errors, isSubmitting }
    } = useForm({
        defaultValues: {
            correo: "",
            password: ""
        }
    });

    const onSubmit = async (data) => {
        try {
            await login({
                correo: data.correo.trim(),
                password: data.password
            });

            const requestedLocation = location.state?.from;
            const destination = requestedLocation
                ? `${requestedLocation.pathname}${requestedLocation.search}${requestedLocation.hash}`
                : "/";

            toast.success("Inicio de sesión correcto");
            navigate(destination, { replace: true });
        } catch (error) {
            const status = error.response?.status;
            const responseData = error.response?.data;

            if (status === 400 && responseData?.validationErrors) {
                Object.entries(responseData.validationErrors).forEach(
                    ([field, message]) => {
                        setError(field, {
                            type: "server",
                            message
                        });
                    }
                );
            }

            if (status === 401) {
                setError("password", {
                    type: "server",
                    message: responseData?.message
                        ?? "Correo o contraseña incorrectos"
                });
            }

            toast.error(
                responseData?.message
                ?? "No fue posible iniciar sesión"
            );
        }
    };

    return (
        <section className="login-page">
            <div className="login-card">
                <div className="login-header">
                    <h1>Huellitas Oaxaca</h1>
                    <p>Bienvenido de nuevo</p>
                </div>

                <h2>Iniciar Sesión</h2>

                <form onSubmit={handleSubmit(onSubmit)} noValidate>
                    <label htmlFor="correo">Correo electrónico</label>

                    <div className="input-group">
                        <Mail size={18} />
                        <input
                            id="correo"
                            type="email"
                            placeholder="tu@correo.com"
                            autoComplete="email"
                            {...register("correo", {
                                required: "El correo es obligatorio.",
                                pattern: {
                                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                                    message: "El correo no tiene un formato válido."
                                }
                            })}
                        />
                    </div>

                    {errors.correo && (
                        <p className="error">
                            {errors.correo.message}
                        </p>
                    )}

                    <label htmlFor="password">Contraseña</label>

                    <div className="input-group">
                        <Lock size={18} />
                        <input
                            id="password"
                            type={showPassword ? "text" : "password"}
                            placeholder="********"
                            autoComplete="current-password"
                            {...register("password", {
                                required: "La contraseña es obligatoria."
                            })}
                        />

                        <button
                            type="button"
                            className="eye-btn"
                            onClick={() => setShowPassword((visible) => !visible)}
                            aria-label={
                                showPassword
                                    ? "Ocultar contraseña"
                                    : "Mostrar contraseña"
                            }
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

                    <button
                        type="submit"
                        className="login-button"
                        disabled={isSubmitting}
                    >
                        {isSubmitting
                            ? "Ingresando..."
                            : "Iniciar sesión"}
                    </button>
                </form>

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