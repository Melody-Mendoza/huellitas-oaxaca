import "./Catalogo.css";

import { useState } from "react";

import SearchBar from "../../components/SearchBar/SearchBar";
import Sidebar from "../../components/Sidebar/Sidebar";
import CardMascota from "../../components/CardMascota/CardMascota";
import Pagination from "../../components/Pagination/Pagination";

function Catalogo() {

    const [busqueda, setBusqueda] = useState("");

    const mascotas = [

        {
            id: 1,
            nombre: "Luna",
            edad: "2 años",
            sexo: "Hembra",
            tamano: "Tamaño Mediano",
            ubicacion: "Refugio Central, Oaxaca",
            estado: "Disponible",
            imagen: "https://images.unsplash.com/photo-1517849845537-4d257902454a?w=600"
        },

        {
            id: 2,
            nombre: "Pixy",
            edad: "4 meses",
            sexo: "Hembra",
            tamano: "Tamaño Pequeño",
            ubicacion: "Hogar Temporal, Centro",
            estado: "Urgente",
            imagen: "https://images.unsplash.com/photo-1519052537078-e6302a4968d4?w=600"
        },

        {
            id: 3,
            nombre: "Max",
            edad: "5 años",
            sexo: "Macho",
            tamano: "Tamaño Grande",
            ubicacion: "Refugio Central, Oaxaca",
            estado: "Disponible",
            imagen: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?w=600"
        },

        {
            id: 4,
            nombre: "Mila",
            edad: "2 años",
            sexo: "Hembra",
            tamano: "Tamaño Mediano",
            ubicacion: "Hogar Temporal, Xoxo",
            estado: "Disponible",
            imagen: "https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=600"
        },

        {
            id: 5,
            nombre: "Toby",
            edad: "1 año",
            sexo: "Macho",
            tamano: "Tamaño Pequeño",
            ubicacion: "Refugio Central, Oaxaca",
            estado: "Disponible",
            imagen: "https://images.unsplash.com/photo-1591160690555-5debfba289f0?w=600"
        }

    ];

    const mascotasFiltradas = mascotas.filter((mascota) =>
        mascota.nombre.toLowerCase().includes(busqueda.toLowerCase())
    );

    return (

        <section className="catalogo">

            <div className="catalogo-layout">

                <Sidebar />

                <div className="catalogo-content">

                    <div className="catalogo-top">

                        <div>

                            <h1>63</h1>

                            <p>Mascotas esperando un hogar</p>

                        </div>

                        <SearchBar

                            value={busqueda}

                            onChange={(e) => setBusqueda(e.target.value)}

                        />

                    </div>

                    <div className="catalogo-grid">

                        {

                            mascotasFiltradas.map((mascota) => (

                                <CardMascota

                                    key={mascota.id}

                                    mascota={mascota}

                                />

                            ))

                        }

                    </div>

                    <Pagination />

                </div>

            </div>

        </section>

    );

}

export default Catalogo;