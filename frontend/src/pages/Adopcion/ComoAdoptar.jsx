import "./ComoAdoptar.css";
import { Link } from "react-router-dom";
import PageHeader from "../../components/PageHeader/PageHeader";

function ComoAdoptar() {

    return (

        <>

            <PageHeader
                titulo="¿Cómo adoptar?"
                descripcion="Nuestro proceso busca asegurar una adopción responsable y el bienestar de cada mascota."
            />

            <section className="steps">
                <div className="step">
                    <h3>1. Explora</h3>
                    <p>Busca la mascota ideal para ti.</p>
                </div>
                <div className="step">
                    <h3>2. Solicita</h3>
                    <p>Completa el formulario de adopción.</p>
                </div>
                <div className="step">
                    <h3>3. Entrevista</h3>
                    <p>Conoceremos tu hogar y estilo de vida.</p>
                </div>
                <div className="step">
                    <h3>4. Adopta</h3>
                    <p>Si todo está correcto, tu nuevo amigo llegará contigo.</p>
                </div>
                <Link
                    to="/catalogo"
                    className="adopt-btn"
                >
                    Ver mascotas
                </Link>
            </section>
        </>
    );
}

export default ComoAdoptar;