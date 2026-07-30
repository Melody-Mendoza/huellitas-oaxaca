import "./Register.css";
import { Link, useNavigate } from "react-router-dom";
import { User, Mail, Phone, Lock, Eye, EyeOff } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import api from "../../services/api";

function Register() {
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const navigate = useNavigate();

    const {
        register,
        handleSubmit,
        watch,
        setError,
        formState: { errors, isSubmitting }
    } = useForm({
        defaultValues: {
            nombre: "",
            apellidoPaterno: "",
            apellidoMaterno: "",
            correo: "",
            telefono: "",
            password: "",
            confirmPassword: ""
        }
    });

    // React Hook Form's watch is needed to validate the confirmation field.
    // eslint-disable-next-line react-hooks/incompatible-library
    const password = watch("password");

    const onSubmit = async (data) => {
        const payload = {
            nombre: data.nombre.trim(),
            apellidoPaterno: data.apellidoPaterno.trim(),
            apellidoMaterno: data.apellidoMaterno.trim(),
            correo: data.correo.trim(),
            password: data.password,
            telefono: data.telefono.trim()
        };

        try {
            const response = await api.post("/auth/registro", payload);

            if (response.status === 201) {
                toast.success("Cuenta creada correctamente");
                navigate("/login", { replace: true });
            }
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

            if (status === 422) {
                setError("correo", {
                    type: "server",
                    message: responseData?.message
                        ?? "Ya existe un usuario registrado con ese correo"
                });
            }

            toast.error(
                responseData?.message
                ?? "No fue posible crear la cuenta"
            );
        }
    };

    return (
        <section className="register-page">
            <div className="register-card">
                <div className="register-header">
                    <h1>Huellitas Oaxaca</h1>
                    <p>Crea tu cuenta</p>
                </div>

                <h2>Registro</h2>

                <form onSubmit={handleSubmit(onSubmit)} noValidate>
                    <label htmlFor="nombre">Nombre</label>
                    <div className={`input-group ${errors.nombre ? "input-error" : ""}`}>
                        <User size={18} />
                        <input
                            id="nombre"
                            type="text"
                            placeholder="Nombre"
                            autoComplete="given-name"
                            {...register("nombre", {
                                required: "El nombre es obligatorio.",
                                maxLength: {
                                    value: 30,
                                    message: "El nombre no puede superar los 30 caracteres."
                                }
                            })}
                        />
                    </div>
                    {errors.nombre && (
                        <p className="error">{errors.nombre.message}</p>
                    )}

                    <label htmlFor="apellidoPaterno">Apellido paterno</label>
                    <div className={`input-group ${errors.apellidoPaterno ? "input-error" : ""}`}>
                        <User size={18} />
                        <input
                            id="apellidoPaterno"
                            type="text"
                            placeholder="Apellido paterno"
                            autoComplete="family-name"
                            {...register("apellidoPaterno", {
                                required: "El apellido paterno es obligatorio.",
                                maxLength: {
                                    value: 20,
                                    message: "El apellido paterno no puede superar los 20 caracteres."
                                }
                            })}
                        />
                    </div>
                    {errors.apellidoPaterno && (
                        <p className="error">
                            {errors.apellidoPaterno.message}
                        </p>
                    )}

                    <label htmlFor="apellidoMaterno">
                        Apellido materno (opcional)
                    </label>
                    <div className={`input-group ${errors.apellidoMaterno ? "input-error" : ""}`}>
                        <User size={18} />
                        <input
                            id="apellidoMaterno"
                            type="text"
                            placeholder="Apellido materno"
                            {...register("apellidoMaterno", {
                                maxLength: {
                                    value: 20,
                                    message: "El apellido materno no puede superar los 20 caracteres."
                                }
                            })}
                        />
                    </div>
                    {errors.apellidoMaterno && (
                        <p className="error">
                            {errors.apellidoMaterno.message}
                        </p>
                    )}

                    <label htmlFor="correo">Correo electrónico</label>
                    <div className={`input-group ${errors.correo ? "input-error" : ""}`}>
                        <Mail size={18} />
                        <input
                            id="correo"
                            type="email"
                            autoComplete="email"
                            placeholder="correo@ejemplo.com"
                            {...register("correo", {
                                required: "El correo es obligatorio.",
                                maxLength: {
                                    value: 100,
                                    message: "El correo no puede superar los 100 caracteres."
                                },
                                pattern: {
                                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                                    message: "El correo no tiene un formato válido."
                                }
                            })}
                        />
                    </div>
                    {errors.correo && (
                        <p className="error">{errors.correo.message}</p>
                    )}

                    <label htmlFor="telefono">Teléfono (opcional)</label>
                    <div className={`input-group ${errors.telefono ? "input-error" : ""}`}>
                        <Phone size={18} />
                        <input
                            id="telefono"
                            type="tel"
                            autoComplete="tel"
                            placeholder="9511234567"
                            {...register("telefono", {
                                pattern: {
                                    value: /^$|^[0-9]{10}$/,
                                    message: "El teléfono debe contener 10 dígitos."
                                }
                            })}
                        />
                    </div>
                    {errors.telefono && (
                        <p className="error">{errors.telefono.message}</p>
                    )}

                    <label htmlFor="password">Contraseña</label>
                    <div className={`input-group ${errors.password ? "input-error" : ""}`}>
                        <Lock size={18} />
                        <input
                            id="password"
                            type={showPassword ? "text" : "password"}
                            placeholder="********"
                            autoComplete="new-password"
                            {...register("password", {
                                required: "La contraseña es obligatoria.",
                                minLength: {
                                    value: 8,
                                    message: "La contraseña debe tener entre 8 y 14 caracteres."
                                },
                                maxLength: {
                                    value: 14,
                                    message: "La contraseña debe tener entre 8 y 14 caracteres."
                                },
                                pattern: {
                                    value: /^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/,
                                    message: "La contraseña debe contener una mayúscula, un número y un carácter especial."
                                }
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
                        <p className="error">{errors.password.message}</p>
                    )}

                    <label htmlFor="confirmPassword">
                        Confirmar contraseña
                    </label>
                    <div className={`input-group ${errors.confirmPassword ? "input-error" : ""}`}>
                        <Lock size={18} />
                        <input
                            id="confirmPassword"
                            type={showConfirmPassword ? "text" : "password"}
                            placeholder="********"
                            autoComplete="new-password"
                            {...register("confirmPassword", {
                                required: "Confirma tu contraseña.",
                                validate: (value) => (
                                    value === password
                                    || "Las contraseñas no coinciden."
                                )
                            })}
                        />

                        <button
                            type="button"
                            className="eye-btn"
                            onClick={() => (
                                setShowConfirmPassword((visible) => !visible)
                            )}
                            aria-label={
                                showConfirmPassword
                                    ? "Ocultar confirmación"
                                    : "Mostrar confirmación"
                            }
                        >
                            {showConfirmPassword
                                ? <EyeOff size={18} />
                                : <Eye size={18} />}
                        </button>
                    </div>
                    {errors.confirmPassword && (
                        <p className="error">
                            {errors.confirmPassword.message}
                        </p>
                    )}

                    <button
                        type="submit"
                        className="register-button"
                        disabled={isSubmitting}
                    >
                        {isSubmitting
                            ? "Creando cuenta..."
                            : "Registrarse"}
                    </button>
                </form>

                <p className="login-link">
                    ¿Ya tienes una cuenta?
                    <Link to="/login">
                        Inicia sesión
                    </Link>
                </p>
            </div>
        </section>
    );
}

export default Register;
