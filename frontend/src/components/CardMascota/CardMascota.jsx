import "./CardMascota.css";

import { MapPin, Ruler } from "lucide-react";
import { Link } from "react-router-dom";

import fallbackImage from "../../assets/logo/logo.png";

const LABELS = {
    DISPONIBLE: "Disponible",
    GATO: "Gato",
    GATO_HEMBRA: "Gata",
    GATO_MACHO: "Gato",
    GRANDE: "Grande",
    HEMBRA: "Hembra",
    MACHO: "Macho",
    MEDIANO: "Mediano",
    PEQUENO: "Pequeño",
    PERRO: "Perro"
};

function formatAge(age) {
    if (!Number.isInteger(age)) {
        return "Edad no disponible";
    }

    return `${age} ${age === 1 ? "año" : "años"}`;
}

function CardMascota({ mascota }) {
    const speciesLabel = LABELS[mascota.especie]
        || "Especie no disponible";
    const sexLabel = LABELS[mascota.sexo]
        || "Sexo no disponible";
    const sizeLabel = LABELS[mascota.tamano]
        || "Tamaño no disponible";
    const statusLabel = LABELS[mascota.estado]
        || "Estado no disponible";
    const refugeLocation = [
        mascota.refugio?.nombre,
        mascota.refugio?.direccion
    ]
        .filter(Boolean)
        .join(", ")
        || "Ubicación no disponible";

    const handleImageError = (event) => {
        const image = event.currentTarget;

        if (image.dataset.fallbackApplied === "true") {
            image.hidden = true;
            return;
        }

        image.dataset.fallbackApplied = "true";
        image.src = fallbackImage;
    };

    return (
        <article className="card-mascota">
            <div className="card-image">
                <img
                    src={mascota.imagenPrincipal || fallbackImage}
                    alt={`Fotografía de ${mascota.nombre}`}
                    onError={handleImageError}
                />

                <span className="status-badge">
                    {statusLabel}
                </span>
            </div>

            <div className="card-body">
                <div className="card-header">
                    <div>
                        <h2>{mascota.nombre}</h2>
                        <p className="card-species">
                            {speciesLabel} · {sexLabel}
                        </p>
                    </div>

                    <span className="age-badge">
                        {formatAge(mascota.edad)}
                    </span>
                </div>

                {mascota.raza && (
                    <p className="card-breed">
                        {mascota.raza}
                    </p>
                )}

                <p className="info">
                    <Ruler size={15} aria-hidden="true" />
                    {sizeLabel}
                </p>

                <p className="info">
                    <MapPin size={15} aria-hidden="true" />
                    {refugeLocation}
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
