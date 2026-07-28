import "../RecuperarPassword/RecuperarPassword.css";

import { Link, useNavigate } from "react-router-dom";
import { Lock } from "lucide-react";
import { useForm } from "react-hook-form";
import { useEffect } from "react";
import toast from "react-hot-toast";

function RestablecerPassword() {

    const navigate = useNavigate();

    const {

        register,

        handleSubmit,

        watch,

        formState: { errors, isSubmitting }

    } = useForm();

    const password = watch("password");

    useEffect(() => {

        // Aquí más adelante se validará el token enviado por correo.
        // Ejemplo:
        // GET /auth/reset-password?token=...

    }, []);

    const onSubmit = async (data) => {

        await new Promise(resolve => setTimeout(resolve, 1500));

        console.log(data);

        toast.success("Contraseña actualizada correctamente.");

        // Backend
        // await api.post("/auth/reset-password", data);

        setTimeout(() => {

            navigate("/login");

        }, 1500);

    };

    return (

        <section className="forgot-page">

            <div className="forgot-card">

                <div className="forgot-header">

                    <h1>Huellitas Oaxaca</h1>

                    <p>Restablecer contraseña</p>

                </div>

                <h2>Nueva contraseña</h2>

                <p className="forgot-description">

                    Crea una nueva contraseña segura para tu cuenta.
                    Evita usar contraseñas anteriores.

                </p>

                <form onSubmit={handleSubmit(onSubmit)} noValidate>

                    <label>Nueva contraseña</label>

                    <div className={`input-group ${errors.password ? "input-error" : ""}`}>

                        <Lock size={18} />

                        <input

                            type="password"

                            placeholder="********"

                            autoComplete="new-password"

                            {...register("password", {

                                required: "La contraseña es obligatoria.",

                                minLength: {

                                    value: 8,

                                    message: "Debe tener al menos 8 caracteres."

                                }

                            })}

                        />

                    </div>

                    {

                        errors.password &&

                        <p className="error">

                            {errors.password.message}

                        </p>

                    }

                    <label>Confirmar contraseña</label>

                    <div className={`input-group ${errors.confirmPassword ? "input-error" : ""}`}>

                        <Lock size={18} />

                        <input

                            type="password"

                            placeholder="********"

                            autoComplete="new-password"

                            {...register("confirmPassword", {

                                required: "Confirma tu contraseña.",

                                validate: value =>

                                    value === password ||

                                    "Las contraseñas no coinciden."

                            })}

                        />

                    </div>

                    {

                        errors.confirmPassword &&

                        <p className="error">

                            {errors.confirmPassword.message}

                        </p>

                    }

                    <button

                        type="submit"

                        className="forgot-button"

                        disabled={isSubmitting}

                    >

                        {

                            isSubmitting

                                ? "Actualizando..."

                                : "Restablecer contraseña"

                        }

                    </button>

                </form>

                <p className="back-login">

                    <Link to="/login">

                        ← Volver al inicio de sesión

                    </Link>

                </p>

            </div>

        </section>

    );

}

export default RestablecerPassword;