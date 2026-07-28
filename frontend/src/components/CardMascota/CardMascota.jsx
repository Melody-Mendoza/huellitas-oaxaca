import "./CardMascota.css";
import { Link } from "react-router-dom";
import { Heart, MapPin, Ruler } from "lucide-react";

function CardMascota({ mascota }) {

    return (

        <article className="card-mascota">

            <div className="card-image">

                <img
                    src={mascota.imagen}
                    alt={mascota.nombre}
                />

                <span
                    className={`status-badge ${mascota.estado === "Urgente" ? "urgent" : ""}`}
                >
                    {mascota.estado}
                </span>

                <button type="button" className="favorite-btn">

                    <Heart size={18} />

                </button>

            </div>

            <div className="card-body">

                <div className="card-header">

                    <h3>{mascota.nombre}</h3>

                    <span className="age-badge">

                        {mascota.edad}

                    </span>

                </div>

                <p className="info">

                    <Ruler size={15} />

                    {mascota.tamano}

                </p>

                <p className="info">

                    <MapPin size={15} />

                    {mascota.ubicacion}

                </p>

                <Link

                    className="details-btn"

                    to={`/mascota/${mascota.id}`}

                >

                    Ver detalles

                </Link>

            </div>

        </article>

    );

}

export default CardMascota;