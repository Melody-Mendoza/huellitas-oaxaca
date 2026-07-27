import "./Login.css";
import { Link } from "react-router-dom";
import { Mail, Lock, Eye, EyeOff } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

function Login() {

    const [showPassword, setShowPassword] = useState(false);

    const {

        register,

        handleSubmit,

        formState: { errors, isSubmitting }

    } = useForm();

    const onSubmit = async (data) => {

        await new Promise(resolve => setTimeout(resolve, 1500));

        console.log(data);

        toast.success("Inicio de sesión correcto");

    };

    // await api.post("/auth/login", data);



    return (

        <section className="login-page">

            <div className="login-card">

                <div className="login-header">

                    <h1>Huellitas Oaxaca</h1>

                    <p>Bienvenido de nuevo</p>

                </div>

                <h2>Iniciar Sesión</h2>

                <form onSubmit={handleSubmit(onSubmit)} noValidate>

                    <label>Correo electrónico</label>

                    <div className="input-group">

                        <Mail size={18} />

                        <input

                            type="email"

                            placeholder="tu@correo.com"

                            {...register("email", {

                                required: "El correo es obligatorio.",

                                pattern: {
                                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                                    message: "Ingresa un correo electrónico válido."
                                }

                            })}

                        />

                    </div>

                    {errors.email &&

                        <p className="error">

                            {errors.email.message}

                        </p>

                    }

                    <label>Contraseña</label>

                    <div className="input-group">

                        <Lock size={18} />

                        <input

                            type={showPassword ? "text" : "password"}

                            placeholder="********"

                            {...register("password", {

                                required: "La contraseña es obligatoria.",

                                minLength: {
                                    value: 8,
                                    message: "La contraseña debe tener al menos 8 caracteres."
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

                        <p className="error">

                            {errors.password.message}

                        </p>

                    }

                    <div className="forgot">

                        <Link to="/recuperar-password">

                            ¿Olvidaste tu contraseña?

                        </Link>

                    </div>

                    <button
                        type="submit"
                        className="login-button"
                        disabled={isSubmitting}
                    >

                        {
                            isSubmitting
                                ? "Ingresando..."
                                : "Iniciar sesión"
                        }

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