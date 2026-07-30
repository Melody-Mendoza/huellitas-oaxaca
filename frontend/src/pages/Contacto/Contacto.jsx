import "./Contacto.css";
import PageHeader from "../../components/PageHeader/PageHeader";

function Contacto() {
    return (
        <>
            <PageHeader
                titulo="Contáctanos"
                descripcion="¿Tienes alguna duda sobre el proceso de adopción? Estamos para ayudarte."
            />
            <section className="contact-container" aria-labelledby="contact-title">
                <div className="contact-info">
                    <h1 id="contact-title">Información de contacto</h1>
                    <p><strong>Correo:</strong> oaxacahuellitas@gmail.com</p>
                    <p><strong>Teléfono:</strong> (951) 1369459</p>
                    <p><strong>Horario:</strong> Lunes a Viernes de 9:00 a 18:00 hrs.</p>
                    <p><strong>Ubicación:</strong> Oaxaca de Juárez, Oaxaca.</p>
                </div>
                <p role="status">El envío de mensajes desde la plataforma estará disponible cuando exista un endpoint de contacto en el backend.</p>
            </section>
        </>
    );
}

export default Contacto;
