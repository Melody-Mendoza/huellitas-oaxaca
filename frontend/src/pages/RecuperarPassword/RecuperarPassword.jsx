import "./RecuperarPassword.css";
import { Link } from "react-router-dom";
import { Mail } from "lucide-react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

function RecuperarPassword() {

    const {

        register,

        handleSubmit,

        formState: { errors, isSubmitting }

    } = useForm();

    const onSubmit = async (data) => {

        await new Promise(resolve => setTimeout(resolve, 1500));

        console.log(data);

        toast.success("Si el correo existe, recibirás instrucciones para recuperar tu contraseña.");


        // await api.post("/auth/forgot-password", data);

    };

    return (

        <section className="forgot-page">

            <div className="forgot-card">

                <div className="forgot-header">

                    <h1>Huellitas Oaxaca</h1>

                    <p>Recuperar contraseña</p>

                </div>

                <h2>¿Olvidaste tu contraseña?</h2>

                <p className="forgot-description">

                    Ingresa tu correo electrónico y te enviaremos las instrucciones para restablecer tu contraseña.

                </p>

                <form onSubmit={handleSubmit(onSubmit)} noValidate>

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
                                    message: "Ingresa un correo electrónico válido."
                                }

                            })}

                        />

                    </div>

                    {

                        errors.email &&

                        <p className="error">

                            {errors.email.message}

                        </p>

                    }

                    <button

                        type="submit"

                        className="forgot-button"

                        disabled={isSubmitting}

                    >

                        {

                            isSubmitting

                                ? "Enviando..."

                                : "Enviar instrucciones"

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

export default RecuperarPassword;