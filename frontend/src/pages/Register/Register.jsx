import "./Register.css";
import { Link } from "react-router-dom";
import { User, Mail, Lock, Eye, EyeOff } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

function Register() {

    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);

    const {

        register,

        handleSubmit,

        watch,

        formState: { errors, isSubmitting }

    } = useForm();

    const password = watch("password");

    const onSubmit = async (data) => {

        await new Promise(resolve => setTimeout(resolve, 1500));

        console.log(data);

        toast.success("Cuenta creada correctamente");

        // await api.post("/auth/register", data);

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

                    <label>Nombre completo</label>

                    <div className={`input-group ${errors.name ? "input-error" : ""}`}>

                        <User size={18} />

                        <input

                            type="text"

                            placeholder="Nombre completo"

                            {...register("name", {

                                required: "El nombre es obligatorio.",

                                minLength: {

                                    value: 3,

                                    message: "Debe contener al menos 3 caracteres."

                                }

                            })}

                        />

                    </div>

                    {errors.name &&

                        <p className="error">{errors.name.message}</p>

                    }

                    <label>Correo electrónico</label>

                    <div className={`input-group ${errors.email ? "input-error" : ""}`}>

                        <Mail size={18} />

                        <input

                            type="email"

                            placeholder="correo@ejemplo.com"

                            {...register("email", {

                                required: "El correo es obligatorio.",

                                pattern: {

                                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,

                                    message: "Ingresa un correo válido."

                                }

                            })}

                        />

                    </div>

                    {errors.email &&

                        <p className="error">{errors.email.message}</p>

                    }

                    <label>Contraseña</label>

                    <div className={`input-group ${errors.password ? "input-error" : ""}`}>

                        <Lock size={18} />

                        <input

                            type={showPassword ? "text" : "password"}

                            placeholder="********"

                            {...register("password", {

                                required: "La contraseña es obligatoria.",

                                minLength: {

                                    value: 8,

                                    message: "Debe tener al menos 8 caracteres."

                                }

                            })}

                        />

                        <button

                            type="button"

                            className="eye-btn"

                            onClick={() => setShowPassword(!showPassword)}

                        >

                            {

                                showPassword

                                    ? <EyeOff size={18} />

                                    : <Eye size={18} />

                            }

                        </button>

                    </div>

                    {errors.password &&

                        <p className="error">{errors.password.message}</p>

                    }

                    <label>Confirmar contraseña</label>

                    <div className={`input-group ${errors.confirmPassword ? "input-error" : ""}`}>

                        <Lock size={18} />

                        <input

                            type={showConfirmPassword ? "text" : "password"}

                            placeholder="********"

                            {...register("confirmPassword", {

                                required: "Confirma tu contraseña.",

                                validate: value =>

                                    value === password ||

                                    "Las contraseñas no coinciden."

                            })}

                        />

                        <button

                            type="button"

                            className="eye-btn"

                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}

                        >

                            {

                                showConfirmPassword

                                    ? <EyeOff size={18} />

                                    : <Eye size={18} />

                            }

                        </button>

                    </div>

                    {errors.confirmPassword &&

                        <p className="error">

                            {errors.confirmPassword.message}

                        </p>

                    }

                    <button

                        type="submit"

                        className="register-button"

                        disabled={isSubmitting}

                    >

                        {

                            isSubmitting

                                ? "Creando cuenta..."

                                : "Registrarse"

                        }

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