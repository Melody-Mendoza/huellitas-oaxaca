import "./Contacto.css";
import PageHeader from "../../components/PageHeader/PageHeader";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";

function Contacto() {

    const {

        register,

        handleSubmit,

        reset,

        formState: { errors, isSubmitting }

    } = useForm();

    const onSubmit = async (data) => {

        await new Promise(resolve => setTimeout(resolve, 1000));

        console.log(data);

        toast.success("Mensaje enviado correctamente.");

        reset();

    };

    return (

        <>

            <PageHeader

                titulo="Contáctanos"

                descripcion="¿Tienes alguna duda sobre el proceso de adopción? Estamos para ayudarte."

            />

            <section className="contact-container">

                <div className="contact-info">

                    <h2>Información</h2>

                    <p><strong>Correo:</strong> oaxacahuellitas@gmail.com</p>

                    <p><strong>Teléfono:</strong> (951) 1369459</p>

                    <p><strong>Horario:</strong> Lunes a Viernes de 9:00 a 18:00 hrs.</p>

                    <p><strong>Ubicación:</strong> Oaxaca de Juárez, Oaxaca.</p>

                </div>

                <form
                    className="contact-form"
                    onSubmit={handleSubmit(onSubmit)}
                >

                    <h2>Envíanos un mensaje</h2>

                    <input
                        placeholder="Nombre"
                        {...register("nombre", {
                            required: "El nombre es obligatorio."
                        })}
                    />

                    {errors.nombre && <p className="error">{errors.nombre.message}</p>}

                    <input
                        placeholder="Correo electrónico"
                        {...register("correo", {
                            required: "El correo es obligatorio."
                        })}
                    />

                    {errors.correo && <p className="error">{errors.correo.message}</p>}

                    <textarea
                        rows="6"
                        placeholder="Escribe tu mensaje..."
                        {...register("mensaje", {
                            required: "El mensaje es obligatorio."
                        })}
                    />

                    {errors.mensaje && <p className="error">{errors.mensaje.message}</p>}

                    <button
                        type="submit"
                        disabled={isSubmitting}
                    >

                        {

                            isSubmitting

                                ? "Enviando..."

                                : "Enviar mensaje"

                        }

                    </button>

                </form>

            </section>

        </>

    );

}

export default Contacto;