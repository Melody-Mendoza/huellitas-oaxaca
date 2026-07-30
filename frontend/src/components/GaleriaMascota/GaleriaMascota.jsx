import "./GaleriaMascota.css";
import { resolveMediaUrl } from "../../utils/media";

import { ImageOff } from "lucide-react";
import { useState } from "react";

import fallbackImage from "../../assets/logo/logo.png";

function normalizeImages(
    imagenPrincipal,
    imagenesAdicionales
) {
    const candidates = [
        resolveMediaUrl(imagenPrincipal),
        ...(Array.isArray(imagenesAdicionales)
            ? imagenesAdicionales.map(resolveMediaUrl)
            : [])
    ];

    return [...new Set(
        candidates
            .filter((image) => typeof image === "string")
            .map((image) => image.trim())
            .filter(Boolean)
    )];
}

function GaleriaMascota({
    nombreMascota,
    imagenPrincipal,
    imagenesAdicionales
}) {
    const images = normalizeImages(
        imagenPrincipal,
        imagenesAdicionales
    );
    const [selectedImage, setSelectedImage] = useState(
        images[0] || null
    );
    const currentImage = images.includes(selectedImage)
        ? selectedImage
        : images[0] || null;

    const handleMainImageError = (event) => {
        const image = event.currentTarget;

        if (image.dataset.fallbackApplied === "true") {
            image.hidden = true;
            return;
        }

        image.dataset.fallbackApplied = "true";
        image.src = fallbackImage;
    };

    const handleThumbnailError = (event) => {
        event.currentTarget
            .closest("button")
            ?.setAttribute("hidden", "");
    };

    if (!currentImage) {
        return (
            <div
                className="galeria galeria-empty"
                role="status"
            >
                <ImageOff size={52} aria-hidden="true" />
                <p>
                    {nombreMascota
                        ? `${nombreMascota} no tiene imágenes disponibles.`
                        : "Esta mascota no tiene imágenes disponibles."}
                </p>
            </div>
        );
    }

    return (
        <div
            className="galeria"
            aria-label={`Galería de ${nombreMascota}`}
        >
            <div className="imagen-principal-container">
                <img
                    key={currentImage}
                    src={currentImage}
                    alt={`Fotografía seleccionada de ${nombreMascota}`}
                    className="imagen-principal"
                    onError={handleMainImageError}
                />
            </div>

            {images.length > 1 && (
                <div
                    className="miniaturas"
                    aria-label="Seleccionar fotografía"
                >
                    {images.map((image, index) => {
                        const isSelected = image === currentImage;

                        return (
                            <button
                                key={image}
                                type="button"
                                className={
                                    isSelected
                                        ? "miniatura active"
                                        : "miniatura"
                                }
                                aria-label={
                                    `Mostrar fotografía ${index + 1} de ${nombreMascota}`
                                }
                                aria-pressed={isSelected}
                                onClick={() => {
                                    setSelectedImage(image);
                                }}
                            >
                                <img
                                    src={image}
                                    alt=""
                                    onError={handleThumbnailError}
                                />
                            </button>
                        );
                    })}
                </div>
            )}
        </div>
    );
}

export default GaleriaMascota;
