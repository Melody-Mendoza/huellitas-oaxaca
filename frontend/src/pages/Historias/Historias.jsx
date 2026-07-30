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
                <p>Aún no hay historias de adopción publicadas. Muy pronto conocerás a las familias y mascotas que encontraron un nuevo hogar.</p>
            </section>

        </>

    );

}

export default Historias;
