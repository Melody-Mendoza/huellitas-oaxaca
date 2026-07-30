import "./DetalleMascota.css";

import { CalendarDays, Mail, MapPin, Phone } from "lucide-react";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";

import GaleriaMascota from "../../components/GaleriaMascota/GaleriaMascota";
import InfoCard from "../../components/InfoCard/InfoCard";
import Loader from "../../components/Loader/Loader";
import api from "../../services/api";

const MAX_JAVA_LONG = 9223372036854775807n;

const ENUM_LABELS = {
    ADOPTADO: "Adoptado",
    DISPONIBLE: "Disponible",
    EN_PROCESO: "En proceso",
    GATO: "Gato",
    GRANDE: "Grande",
    HEMBRA: "Hembra",
    MACHO: "Macho",
    MEDIANO: "Mediano",
    PEQUENO: "Pequeño",
    PERRO: "Perro"
};

const dateFormatter = new Intl.DateTimeFormat("es-MX", {
    dateStyle: "long",
    timeZone: "UTC"
});

function isValidRouteId(value) {
    if (typeof value !== "string" || !/^[1-9]\d*$/.test(value)) {
        return false;
    }

    try {
        return BigInt(value) <= MAX_JAVA_LONG;
    } catch {
        return false;
    }
}

function isValidDetailResponse(data) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && Number.isInteger(data.id)
        && data.id > 0
        && typeof data.nombre === "string"
        && data.nombre.trim()
        && Array.isArray(data.imagenesAdicionales)
        && data.refugio
        && typeof data.refugio === "object"
        && Number.isInteger(data.refugio.id)
        && data.refugio.id > 0
        && typeof data.refugio.nombre === "string"
        && data.refugio.nombre.trim()
    );
}

function getRequestErrorMessage(error) {
    if (!error.response) {
        return "No fue posible conectar con el backend.";
    }

    switch (error.response.status) {
        case 400:
            return "El identificador de la mascota no es válido.";
        case 401:
            return "La sesión no es válida.";
        case 403:
            return "No tienes permiso para consultar este recurso.";
        case 404:
            return "La mascota no está disponible o no fue encontrada.";
        case 409:
            return "Existe un conflicto al consultar la mascota.";
        case 422:
            return "No fue posible procesar la información de la mascota.";
        case 500:
            return "Ocurrió un error interno en el servidor.";
        default:
            return "No fue posible consultar la información de la mascota.";
    }
}

function formatEnum(value) {
    return ENUM_LABELS[value] || null;
}

function formatAge(value) {
    if (!Number.isInteger(value)) {
        return null;
    }

    return `${value} ${value === 1 ? "año" : "años"}`;
}

function formatWeight(value) {
    return typeof value === "number"
        ? `${value} kg`
        : null;
}

function formatDate(value) {
    if (typeof value !== "string" || !value) {
        return null;
    }

    const date = new Date(`${value}T00:00:00Z`);

    return Number.isNaN(date.getTime())
        ? null
        : dateFormatter.format(date);
}

function DetalleMascota() {
    const { id } = useParams();
    const validId = isValidRouteId(id);

    const [mascota, setMascota] = useState(null);
    const [loading, setLoading] = useState(validId);
    const [errorMessage, setErrorMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);

    useEffect(() => {
        if (!validId) {
            return undefined;
        }

        const controller = new AbortController();

        const loadPet = async () => {
            setLoading(true);
            setErrorMessage("");
            setMascota(null);

            try {
                const response = await api.get(
                    `/mascotas/${id}`,
                    {
                        signal: controller.signal
                    }
                );

                if (!isValidDetailResponse(response.data)) {
                    setErrorMessage(
                        "El backend devolvió una estructura de detalle no compatible."
                    );
                    return;
                }

                setMascota(response.data);
            } catch (error) {
                if (
                    error.code === "ERR_CANCELED"
                    || controller.signal.aborted
                ) {
                    return;
                }

                setErrorMessage(getRequestErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            }
        };

        loadPet();

        return () => {
            controller.abort();
        };
    }, [id, retryVersion, validId]);

    if (!validId) {
        return (
            <section
                className="detalle detalle-feedback"
                aria-labelledby="detalle-error-title"
                role="alert"
            >
                <h1 id="detalle-error-title">
                    Identificador no válido
                </h1>

                <p>
                    El identificador de la mascota no es válido.
                </p>

                <Link to="/catalogo" className="back-link">
                    Volver al catálogo
                </Link>
            </section>
        );
    }

    if (loading) {
        return (
            <section
                className="detalle detalle-loading"
                aria-labelledby="detalle-loading-title"
            >
                <h1
                    id="detalle-loading-title"
                    className="detalle-visually-hidden"
                >
                    Detalle de mascota
                </h1>

                <Loader />
            </section>
        );
    }

    if (errorMessage || !mascota) {
        return (
            <section
                className="detalle detalle-feedback"
                aria-labelledby="detalle-error-title"
                role="alert"
            >
                <h1 id="detalle-error-title">
                    No fue posible cargar la mascota
                </h1>

                <p>
                    {errorMessage
                        || "No fue posible consultar la información de la mascota."}
                </p>

                <div className="detalle-feedback-actions">
                    <button
                        type="button"
                        className="detalle-retry-button"
                        onClick={() => {
                            setRetryVersion(
                                (currentVersion) =>
                                    currentVersion + 1
                            );
                        }}
                    >
                        Reintentar
                    </button>

                    <Link to="/catalogo" className="back-link">
                        Volver al catálogo
                    </Link>
                </div>
            </section>
        );
    }

    const age = formatAge(mascota.edad);
    const weight = formatWeight(mascota.peso);
    const entryDate = formatDate(mascota.fechaIngreso);
    const species = formatEnum(mascota.especie);
    const sex = formatEnum(mascota.sexo);
    const size = formatEnum(mascota.tamano);
    const status = formatEnum(mascota.estado);

    return (
        <section
            className="detalle"
            aria-labelledby="detalle-title"
        >
            <Link to="/catalogo" className="back-link">
                Volver al catálogo
            </Link>

            <div className="detalle-grid">
                <GaleriaMascota
                    key={mascota.id}
                    nombreMascota={mascota.nombre}
                    imagenPrincipal={mascota.imagenPrincipal}
                    imagenesAdicionales={
                        mascota.imagenesAdicionales
                    }
                />

                <div className="detalle-info">
                    <div className="detalle-heading">
                        <div>
                            <p className="detalle-eyebrow">
                                {species || "Mascota"}
                            </p>

                            <h1 id="detalle-title">
                                {mascota.nombre}
                            </h1>

                            {mascota.raza && (
                                <p className="detalle-raza">
                                    {mascota.raza}
                                </p>
                            )}
                        </div>

                        {status && (
                            <span className="detalle-status">
                                {status}
                            </span>
                        )}
                    </div>

                    <div className="cards-info">
                        {age && (
                            <InfoCard titulo="Edad" valor={age} />
                        )}

                        {sex && (
                            <InfoCard titulo="Sexo" valor={sex} />
                        )}

                        {size && (
                            <InfoCard titulo="Tamaño" valor={size} />
                        )}

                        {weight && (
                            <InfoCard titulo="Peso" valor={weight} />
                        )}
                    </div>

                    {mascota.descripcion && (
                        <div className="description">
                            <h2>Descripción</h2>
                            <p>{mascota.descripcion}</p>
                        </div>
                    )}

                    {entryDate && (
                        <p className="detalle-entry-date">
                            <CalendarDays
                                size={18}
                                aria-hidden="true"
                            />
                            Fecha de ingreso: {entryDate}
                        </p>
                    )}

                    <section
                        className="detalle-refugio"
                        aria-labelledby="refugio-title"
                    >
                        <h2 id="refugio-title">
                            Refugio responsable
                        </h2>

                        <h3>{mascota.refugio.nombre}</h3>

                        <div className="detalle-refugio-datos">
                            {mascota.refugio.direccion && (
                                <p>
                                    <MapPin
                                        size={18}
                                        aria-hidden="true"
                                    />
                                    {mascota.refugio.direccion}
                                </p>
                            )}

                            {mascota.refugio.telefono && (
                                <p>
                                    <Phone
                                        size={18}
                                        aria-hidden="true"
                                    />
                                    {mascota.refugio.telefono}
                                </p>
                            )}

                            {mascota.refugio.correo && (
                                <p>
                                    <Mail
                                        size={18}
                                        aria-hidden="true"
                                    />
                                    {mascota.refugio.correo}
                                </p>
                            )}
                        </div>
                    </section>

                    <Link
                        to={`/solicitud?mascotaId=${mascota.id}`}
                        className="adopt-btn"
                    >
                        Iniciar solicitud de adopción
                    </Link>
                </div>
            </div>
        </section>
    );
}

export default DetalleMascota;
