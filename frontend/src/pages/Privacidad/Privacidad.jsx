import "./Privacidad.css";
import PageHeader from "../../components/PageHeader/PageHeader";

function Privacidad() {

    return (

        <>

            <PageHeader
                titulo="Política de Privacidad"
                descripcion="Conoce cómo protegemos y utilizamos la información que compartes con Huellitas Oaxaca."
            />

            <section className="policy-container">

                <article className="policy-card">

                    <h2>1. Información que recopilamos</h2>

                    <p>
                        Recopilamos únicamente la información necesaria para gestionar
                        solicitudes de adopción, crear cuentas de usuario y mejorar la
                        experiencia dentro de la plataforma.
                    </p>

                    <h2>2. Uso de la información</h2>

                    <p>
                        Los datos proporcionados se utilizan exclusivamente para fines
                        relacionados con el proceso de adopción, comunicación con los
                        usuarios y funcionamiento del sistema.
                    </p>

                    <h2>3. Protección de datos</h2>

                    <p>
                        Implementamos medidas de seguridad para proteger la información
                        personal contra accesos no autorizados, pérdida o modificación.
                    </p>

                    <h2>4. Compartición de información</h2>

                    <p>
                        No vendemos ni compartimos información personal con terceros,
                        salvo cuando sea necesario para cumplir con obligaciones legales
                        o con el proceso de adopción.
                    </p>

                    <h2>5. Derechos del usuario</h2>

                    <p>
                        Puedes solicitar la actualización, corrección o eliminación de
                        tu información personal en cualquier momento.
                    </p>

                    <h2>6. Contacto</h2>

                    <p>
                        Si tienes dudas sobre esta política puedes comunicarte con
                        nuestro equipo mediante la sección de contacto.
                    </p>

                </article>

            </section>

        </>

    );

}

export default Privacidad;