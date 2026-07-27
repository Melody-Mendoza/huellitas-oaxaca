import "./DetalleMascota.css";

import { Heart, MapPin } from "lucide-react";
import { Link, useParams } from "react-router-dom";
import { useState } from "react";

import GaleriaMascota from "../../components/GaleriaMascota/GaleriaMascota";
import InfoCard from "../../components/InfoCard/InfoCard";

function DetalleMascota() {

    const { id } = useParams();

    const mascotas = [

        {
            id: 1,
            nombre: "Luna",
            raza: "Mestiza",
            edad: "2 años",
            sexo: "Hembra",
            tamano: "Tamaño Mediano",
            vacunas: "Completas",
            esterilizada: "Sí",
            refugio: "Refugio Central, Oaxaca",
            personalidad: ["Juguetona", "Cariñosa", "Sociable"],
            descripcion: "Luna fue rescatada de las calles y actualmente busca una familia responsable. Es una perrita muy cariñosa y le encanta convivir con personas.",
            imagenes: [
                "https://images.unsplash.com/photo-1517849845537-4d257902454a?w=900",
                "https://images.unsplash.com/photo-1517849845537-4d257902454a?w=900",
                "https://images.unsplash.com/photo-1517849845537-4d257902454a?w=900"
            ]
        },

        {
            id: 2,
            nombre: "Pixy",
            raza: "Chihuahua",
            edad: "4 meses",
            sexo: "Hembra",
            tamano: "Tamaño Pequeño",
            vacunas: "Primera vacuna",
            esterilizada: "No",
            refugio: "Hogar Temporal, Centro",
            personalidad: ["Tierna", "Curiosa", "Juguetona"],
            descripcion: "Pixy es una cachorrita llena de energía que disfruta explorar y jugar todo el día.",
            imagenes: [
                "https://images.unsplash.com/photo-1519052537078-e6302a4968d4?w=900",
                "https://images.unsplash.com/photo-1519052537078-e6302a4968d4?w=900",
                "https://images.unsplash.com/photo-1519052537078-e6302a4968d4?w=900"
            ]
        },

        {
            id: 3,
            nombre: "Max",
            raza: "Labrador",
            edad: "5 años",
            sexo: "Macho",
            tamano: "Tamaño Grande",
            vacunas: "Completas",
            esterilizada: "Sí",
            refugio: "Refugio Central, Oaxaca",
            personalidad: ["Leal", "Protector", "Activo"],
            descripcion: "Max disfruta correr, jugar y convivir con niños. Es un perro muy noble y obediente.",
            imagenes: [
                "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=900",
                "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=900",
                "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=900"
            ]
        },

        {
            id: 4,
            nombre: "Mila",
            raza: "Golden Retriever",
            edad: "2 años",
            sexo: "Hembra",
            tamano: "Tamaño Mediano",
            vacunas: "Completas",
            esterilizada: "Sí",
            refugio: "Hogar Temporal, Xoxo",
            personalidad: ["Dócil", "Amorosa", "Paciente"],
            descripcion: "Mila es muy tranquila y disfruta pasar tiempo con personas y otras mascotas.",
            imagenes: [
                "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=900",
                "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=900",
                "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=900"
            ]
        },

        {
            id: 5,
            nombre: "Toby",
            raza: "Beagle",
            edad: "1 año",
            sexo: "Macho",
            tamano: "Tamaño Pequeño",
            vacunas: "Completas",
            esterilizada: "No",
            refugio: "Refugio Central, Oaxaca",
            personalidad: ["Inteligente", "Curioso", "Amigable"],
            descripcion: "Toby es un perro muy sociable que disfruta caminar y convivir con otros perros.",
            imagenes: [
                "https://images.unsplash.com/photo-1591160690555-5debfba289f0?w=900",
                "https://images.unsplash.com/photo-1591160690555-5debfba289f0?w=900",
                "https://images.unsplash.com/photo-1591160690555-5debfba289f0?w=900"
            ]
        }

    ];

    const mascota = mascotas.find((m) => m.id === Number(id));

    const [favorito, setFavorito] = useState(false);

    if (!mascota) {

        return (

            <section className="detalle">

                <h2>Mascota no encontrada.</h2>

            </section>

        );

    }

    return (

        <section className="detalle">

            <Link

                to="/catalogo"

                className="back-link"

            >

                ← Volver al catálogo

            </Link>

            <div className="detalle-grid">

                <GaleriaMascota

                    imagenes={mascota.imagenes}

                />

                <div className="detalle-info">

                    <div className="title-row">

                        <div>

                            <h1>{mascota.nombre}</h1>

                            <p>{mascota.raza}</p>

                        </div>

                        <button

                            className={`favorite ${favorito ? "active" : ""}`}

                            onClick={() => setFavorito(!favorito)}

                        >

                            <Heart

                                size={22}

                                fill={favorito ? "#E63946" : "none"}

                            />

                        </button>

                    </div>

                    <p className="location">

                        <MapPin size={18} />

                        {mascota.refugio}

                    </p>

                    <div className="cards-info">

                        <InfoCard titulo="Edad" valor={mascota.edad} />

                        <InfoCard titulo="Sexo" valor={mascota.sexo} />

                        <InfoCard titulo="Tamaño" valor={mascota.tamano} />

                        <InfoCard titulo="Vacunas" valor={mascota.vacunas} />

                        <InfoCard titulo="Esterilizada" valor={mascota.esterilizada} />

                    </div>

                    <div className="description">

                        <h2>Historia</h2>

                        <p>{mascota.descripcion}</p>

                    </div>

                    <div className="personality">

                        <h2>Personalidad</h2>

                        <div className="tags">

                            {

                                mascota.personalidad.map((tag, index) => (

                                    <span key={index}>

                                        {tag}

                                    </span>

                                ))

                            }

                        </div>

                    </div>

                    <Link

                        to="/solicitud"

                        className="adopt-btn"

                    >

                        Solicitar adopción

                    </Link>

                </div>

            </div>

        </section>

    );

}

export default DetalleMascota;