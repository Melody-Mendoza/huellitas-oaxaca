import "./Historias.css";
import PageHeader from "../../components/PageHeader/PageHeader";

const historias = [

    {
        id: 1,
        nombre: "Luna",
        historia: "Después de varios meses en un refugio, Luna encontró una familia que hoy la llena de amor y aventuras.",
        imagen: "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=600"
    },

    {
        id: 2,
        nombre: "Max",
        historia: "Max pasó de vivir en las calles a convertirse en el mejor amigo de una familia que lo esperaba.",
        imagen: "https://images.unsplash.com/photo-1517849845537-4d257902454a?w=600"
    },

    {
        id: 3,
        nombre: "Milo",
        historia: "Milo encontró un hogar lleno de cariño y ahora disfruta cada día junto a sus nuevos dueños.",
        imagen: "https://images.unsplash.com/photo-1519052537078-e6302a4968d4?w=600"
    }

];

function Historias() {

    return (

        <>

            <PageHeader

                titulo="Historias que inspiran"

                descripcion="Cada adopción cambia dos vidas: la de la mascota y la de su nueva familia."

            />

            <section className="stories-container">

                {

                    historias.map((historia) => (

                        <article
                            className="story-card"
                            key={historia.id}
                        >

                            <img
                                src={historia.imagen}
                                alt={historia.nombre}
                            />

                            <div className="story-content">

                                <h2>{historia.nombre}</h2>

                                <p>{historia.historia}</p>

                            </div>

                        </article>

                    ))

                }

            </section>

        </>

    );

}

export default Historias;