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
                <p role="status">¿Necesitas ayuda? Puedes comunicarte con nosotros por correo electrónico o por teléfono. Próximamente también podrás enviarnos mensajes desde esta sección.</p>
            </section>
        </>
    );
}

export default Contacto;
