import "./Nosotros.css";
import PageHeader from "../../components/PageHeader/PageHeader";

function Nosotros() {

    return (

        <>

            <PageHeader

                titulo="Sobre Huellitas Oaxaca"

                descripcion="Trabajamos para brindar una segunda oportunidad a perros y gatos en situación de abandono, promoviendo la adopción responsable y el bienestar animal."

            />

            <section className="about-container">

                <div className="about-card">

                    <h2>¿Quiénes somos?</h2>

                    <p>

                        Huellitas Oaxaca es una iniciativa dedicada a conectar mascotas
                        rescatadas con familias responsables. Nuestro compromiso es
                        fomentar el respeto, cuidado y protección de los animales mediante
                        procesos de adopción seguros y transparentes.

                    </p>

                </div>

                <div className="about-grid">

                    <div className="info-card">

                        <h3>Misión</h3>

                        <p>

                            Promover la adopción responsable y mejorar la calidad de vida
                            de perros y gatos que buscan un hogar.

                        </p>

                    </div>

                    <div className="info-card">

                        <h3>Visión</h3>

                        <p>

                            Ser una plataforma confiable que impulse una cultura de
                            respeto y bienestar animal en Oaxaca.

                        </p>

                    </div>

                    <div className="info-card">

                        <h3>Valores</h3>

                        <ul>

                            <li>Empatía</li>

                            <li>Compromiso</li>

                            <li>Responsabilidad</li>

                            <li>Respeto por la vida</li>

                        </ul>

                    </div>

                </div>

            </section>

        </>

    );

}

export default Nosotros;