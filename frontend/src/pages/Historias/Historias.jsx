import "./Historias.css";
import PageHeader from "../../components/PageHeader/PageHeader";

function Historias() {

    return (

        <>

            <PageHeader

                titulo="Historias que inspiran"

                descripcion="Cada adopción cambia dos vidas: la de la mascota y la de su nueva familia."

            />

            <section className="stories-container" role="status">
                <h1>Historias de adopción</h1>
                <p>Las historias publicadas por refugios estarán disponibles cuando exista un endpoint HTTP para este módulo.</p>
            </section>

        </>

    );

}

export default Historias;
