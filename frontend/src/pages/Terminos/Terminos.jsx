import "./Terminos.css";
import PageHeader from "../../components/PageHeader/PageHeader";

function Terminos() {

    return (

        <>

            <PageHeader
                titulo="Términos y Condiciones"
                descripcion="Al utilizar Huellitas Oaxaca aceptas las siguientes condiciones para garantizar una experiencia segura y responsable."
            />

            <section className="policy-container">

                <article className="policy-card">

                    <h2>1. Uso de la plataforma</h2>

                    <p>
                        Huellitas Oaxaca es una plataforma destinada a promover la
                        adopción responsable de mascotas. Los usuarios se comprometen
                        a utilizar el sitio de manera ética y respetuosa.
                    </p>

                    <h2>2. Registro de usuarios</h2>

                    <p>
                        La información proporcionada durante el registro deberá ser
                        verídica y mantenerse actualizada para facilitar el proceso de
                        adopción.
                    </p>

                    <h2>3. Solicitudes de adopción</h2>

                    <p>
                        El envío de una solicitud no garantiza la adopción de una
                        mascota. Cada caso será evaluado conforme a los criterios
                        establecidos por el refugio.
                    </p>

                    <h2>4. Responsabilidades del adoptante</h2>

                    <p>
                        Las personas que adopten una mascota se comprometen a brindarle
                        alimentación, atención veterinaria, cuidados y un ambiente
                        adecuado para su bienestar.
                    </p>

                    <h2>5. Modificaciones del servicio</h2>

                    <p>
                        Huellitas Oaxaca podrá actualizar el contenido, las funciones
                        o estos términos cuando sea necesario para mejorar el servicio.
                    </p>

                    <h2>6. Aceptación</h2>

                    <p>
                        Al continuar utilizando la plataforma, aceptas estos términos
                        y condiciones, así como la política de privacidad vigente.
                    </p>

                </article>

            </section>

        </>

    );

}

export default Terminos;