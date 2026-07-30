import "./SolicitudAdopcion.css";

import { ImageOff } from "lucide-react";
import {
    useEffect,
    useRef,
    useState
} from "react";
import { Link, useSearchParams } from "react-router-dom";

import Loader from "../../components/Loader/Loader";
import api from "../../services/api";

const MAX_COMMENTS_LENGTH = 1000;

const SPECIES_LABELS = {
    GATO: "Gato",
    PERRO: "Perro"
};

function parsePetId(value) {
    if (typeof value !== "string" || !/^[1-9]\d*$/.test(value)) {
        return null;
    }

    const parsedValue = Number(value);

    return Number.isSafeInteger(parsedValue) && parsedValue > 0
        ? parsedValue
        : null;
}

function isValidPetResponse(data, petId) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && Number.isSafeInteger(data.id)
        && data.id > 0
        && data.id === petId
        && typeof data.nombre === "string"
        && data.nombre.trim()
        && typeof data.especie === "string"
        && data.refugio
        && typeof data.refugio === "object"
        && typeof data.refugio.nombre === "string"
        && data.refugio.nombre.trim()
    );
}

function isValidCreationResponse(data, petId) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && Number.isSafeInteger(data.id)
        && data.id > 0
        && data.estado === "PENDIENTE"
        && data.mascotaId === petId
        && typeof data.nombreMascota === "string"
        && data.nombreMascota.trim()
    );
}

function getSafeBackendMessage(error) {
    const message = error.response?.data?.message;

    return typeof message === "string" && message.trim()
        ? message.trim()
        : null;
}

function getPetErrorMessage(error) {
    if (!error.response) {
        return "No fue posible conectar con el backend.";
    }

    const backendMessage = getSafeBackendMessage(error);

    switch (error.response.status) {
        case 400:
            return backendMessage
                || "El identificador de la mascota no es válido.";
        case 401:
            return backendMessage || "La sesión no es válida.";
        case 403:
            return backendMessage
                || "No tienes permiso para consultar este recurso.";
        case 404:
            return "La mascota no está disponible o no fue encontrada.";
        case 409:
            return backendMessage
                || "Existe un conflicto al consultar la mascota.";
        case 500:
            return "Ocurrió un error interno en el servidor.";
        default:
            return backendMessage
                || "No fue posible consultar la mascota seleccionada.";
    }
}

function getSubmissionErrorMessage(error) {
    if (!error.response) {
        return "No fue posible conectar con el backend.";
    }

    const backendMessage = getSafeBackendMessage(error);

    switch (error.response.status) {
        case 400:
            return backendMessage
                || "Los datos de la solicitud no son válidos.";
        case 401:
            return backendMessage || "La sesión no es válida.";
        case 403:
            return backendMessage
                || "El usuario autenticado no tiene permiso para crear solicitudes.";
        case 404:
            return backendMessage
                || "La mascota no está disponible o no fue encontrada.";
        case 409:
            return backendMessage
                || "Ya existe una solicitud activa para esta mascota.";
        case 500:
            return "Ocurrió un error interno en el servidor.";
        default:
            return backendMessage
                || "No fue posible crear la solicitud de adopción.";
    }
}

function SolicitudAdopcion() {
    const [searchParams] = useSearchParams();
    const rawPetId = searchParams.get("mascotaId");
    const petId = parsePetId(rawPetId);

    const [pet, setPet] = useState(null);
    const [loadingPet, setLoadingPet] = useState(petId !== null);
    const [petErrorMessage, setPetErrorMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);
    const [comments, setComments] = useState("");
    const [commentsError, setCommentsError] = useState("");
    const [submissionError, setSubmissionError] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [createdRequest, setCreatedRequest] = useState(null);
    const [imageFailed, setImageFailed] = useState(false);

    const mountedRef = useRef(false);
    const submissionLockRef = useRef(false);

    useEffect(() => {
        mountedRef.current = true;

        return () => {
            mountedRef.current = false;
        };
    }, []);

    useEffect(() => {
        if (petId === null) {
            return undefined;
        }

        const controller = new AbortController();

        const loadPet = async () => {
            setLoadingPet(true);
            setPetErrorMessage("");
            setPet(null);
            setImageFailed(false);
            setCommentsError("");
            setSubmissionError("");
            setCreatedRequest(null);

            try {
                const response = await api.get(
                    `/mascotas/${petId}`,
                    {
                        signal: controller.signal
                    }
                );

                if (!isValidPetResponse(response.data, petId)) {
                    setPetErrorMessage(
                        "El backend devolvió una estructura de mascota no compatible."
                    );
                    return;
                }

                setPet(response.data);
            } catch (error) {
                if (
                    error.code === "ERR_CANCELED"
                    || controller.signal.aborted
                ) {
                    return;
                }

                setPetErrorMessage(getPetErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) {
                    setLoadingPet(false);
                }
            }
        };

        loadPet();

        return () => {
            controller.abort();
        };
    }, [petId, retryVersion]);

    const handleCommentsChange = (event) => {
        const nextComments = event.target.value;

        setComments(nextComments);
        setSubmissionError("");

        if (nextComments.length > MAX_COMMENTS_LENGTH) {
            setCommentsError(
                "Los comentarios no pueden superar los 1000 caracteres."
            );
        } else {
            setCommentsError("");
        }
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (
            submissionLockRef.current
            || submitting
            || createdRequest
            || !pet
            || petId === null
        ) {
            return;
        }

        if (comments.length > MAX_COMMENTS_LENGTH) {
            setCommentsError(
                "Los comentarios no pueden superar los 1000 caracteres."
            );
            return;
        }

        submissionLockRef.current = true;
        setSubmitting(true);
        setSubmissionError("");
        setCommentsError("");

        const trimmedComments = comments.trim();
        const payload = {
            mascotaId: petId
        };

        if (trimmedComments) {
            payload.comentarios = trimmedComments;
        }

        try {
            const response = await api.post(
                "/solicitudes",
                payload
            );

            if (
                response.status !== 201
                || !isValidCreationResponse(response.data, petId)
            ) {
                if (mountedRef.current) {
                    setSubmissionError(
                        "El backend devolvió una respuesta de creación no compatible."
                    );
                }
                return;
            }

            if (mountedRef.current) {
                setCreatedRequest(response.data);
            }
        } catch (error) {
            if (!mountedRef.current) {
                return;
            }

            const validationErrors =
                error.response?.data?.validationErrors;

            if (
                error.response?.status === 400
                && typeof validationErrors?.comentarios === "string"
            ) {
                setCommentsError(validationErrors.comentarios);
            } else {
                setSubmissionError(
                    getSubmissionErrorMessage(error)
                );
            }
        } finally {
            submissionLockRef.current = false;

            if (mountedRef.current) {
                setSubmitting(false);
            }
        }
    };

    if (petId === null) {
        return (
            <main
                className="solicitud"
                aria-labelledby="solicitud-invalid-title"
            >
                <section
                    className="contenedor-solicitud solicitud-feedback"
                    role="alert"
                >
                    <h1 id="solicitud-invalid-title">
                        Mascota no válida
                    </h1>

                    <p>
                        Selecciona una mascota válida desde el catálogo
                        para iniciar una solicitud de adopción.
                    </p>

                    <Link
                        to="/catalogo"
                        className="solicitud-secondary-link"
                    >
                        Volver al catálogo
                    </Link>
                </section>
            </main>
        );
    }

    if (loadingPet) {
        return (
            <main
                className="solicitud solicitud-loading"
                aria-labelledby="solicitud-loading-title"
            >
                <h1
                    id="solicitud-loading-title"
                    className="solicitud-visually-hidden"
                >
                    Solicitud de adopción
                </h1>

                <Loader />
            </main>
        );
    }

    if (petErrorMessage || !pet) {
        return (
            <main
                className="solicitud"
                aria-labelledby="solicitud-error-title"
            >
                <section
                    className="contenedor-solicitud solicitud-feedback"
                    role="alert"
                >
                    <h1 id="solicitud-error-title">
                        No fue posible cargar la mascota
                    </h1>

                    <p>
                        {petErrorMessage
                            || "No fue posible consultar la mascota seleccionada."}
                    </p>

                    <div className="solicitud-feedback-actions">
                        <button
                            type="button"
                            className="solicitud-primary-button"
                            onClick={() => {
                                setRetryVersion(
                                    (currentVersion) =>
                                        currentVersion + 1
                                );
                            }}
                        >
                            Reintentar
                        </button>

                        <Link
                            to="/catalogo"
                            className="solicitud-secondary-link"
                        >
                            Volver al catálogo
                        </Link>
                    </div>
                </section>
            </main>
        );
    }

    if (createdRequest) {
        return (
            <main
                className="solicitud"
                aria-labelledby="solicitud-success-title"
            >
                <section
                    className="contenedor-solicitud solicitud-success"
                    role="status"
                >
                    <p className="solicitud-eyebrow">
                        Solicitud #{createdRequest.id}
                    </p>

                    <h1 id="solicitud-success-title">
                        Solicitud enviada correctamente
                    </h1>

                    <p>
                        Tu solicitud para adoptar a{
                            " "
                        }<strong>{createdRequest.nombreMascota}</strong>{
                            " "
                        }fue registrada con estado pendiente.
                    </p>

                    <div className="solicitud-feedback-actions">
                        <Link
                            to={`/mascota/${createdRequest.mascotaId}`}
                            className="solicitud-primary-link"
                        >
                            Volver al detalle
                        </Link>

                        <Link
                            to="/catalogo"
                            className="solicitud-secondary-link"
                        >
                            Volver al catálogo
                        </Link>
                    </div>
                </section>
            </main>
        );
    }

    const speciesLabel = SPECIES_LABELS[pet.especie]
        || pet.especie;
    const showPetImage = Boolean(
        typeof pet.imagenPrincipal === "string"
        && pet.imagenPrincipal.trim()
        && !imageFailed
    );

    return (
        <main
            className="solicitud"
            aria-labelledby="solicitud-title"
        >
            <section className="contenedor-solicitud">
                <p className="solicitud-eyebrow">
                    Adopción responsable
                </p>

                <h1 id="solicitud-title">
                    Solicitud de adopción
                </h1>

                <p className="solicitud-intro">
                    Confirma la mascota seleccionada y agrega un
                    comentario opcional para el refugio.
                </p>

                <article
                    className="solicitud-pet-context"
                    aria-labelledby="solicitud-pet-name"
                >
                    <div className="solicitud-pet-media">
                        {showPetImage ? (
                            <img
                                src={pet.imagenPrincipal}
                                alt={`Fotografía de ${pet.nombre}`}
                                onError={() => {
                                    setImageFailed(true);
                                }}
                            />
                        ) : (
                            <div
                                className="solicitud-pet-no-image"
                                role="img"
                                aria-label={
                                    `${pet.nombre} no tiene imagen disponible`
                                }
                            >
                                <ImageOff
                                    size={36}
                                    aria-hidden="true"
                                />
                            </div>
                        )}
                    </div>

                    <div className="solicitud-pet-info">
                        <p>{speciesLabel}</p>

                        <h2 id="solicitud-pet-name">
                            {pet.nombre}
                        </h2>

                        <p>
                            Refugio: {pet.refugio.nombre}
                        </p>

                        <Link to={`/mascota/${pet.id}`}>
                            Revisar detalle de la mascota
                        </Link>
                    </div>
                </article>

                <form
                    className="solicitud-form"
                    onSubmit={handleSubmit}
                    noValidate
                >
                    <label htmlFor="solicitud-comentarios">
                        Comentarios para el refugio
                        <span>Opcional</span>
                    </label>

                    <textarea
                        id="solicitud-comentarios"
                        name="comentarios"
                        rows="7"
                        value={comments}
                        maxLength={MAX_COMMENTS_LENGTH + 1}
                        onChange={handleCommentsChange}
                        aria-invalid={Boolean(commentsError)}
                        aria-describedby={
                            commentsError
                                ? "solicitud-comentarios-error solicitud-comentarios-help"
                                : "solicitud-comentarios-help"
                        }
                        disabled={submitting}
                    />

                    <div className="solicitud-field-meta">
                        <p id="solicitud-comentarios-help">
                            Máximo 1000 caracteres.
                        </p>

                        <p aria-live="polite">
                            {comments.length} / {MAX_COMMENTS_LENGTH}
                        </p>
                    </div>

                    {commentsError && (
                        <p
                            id="solicitud-comentarios-error"
                            className="solicitud-field-error"
                            role="alert"
                        >
                            {commentsError}
                        </p>
                    )}

                    {submissionError && (
                        <p
                            className="solicitud-general-error"
                            role="alert"
                        >
                            {submissionError}
                        </p>
                    )}

                    <button
                        type="submit"
                        className="solicitud-submit-button"
                        disabled={
                            submitting
                            || Boolean(commentsError)
                        }
                    >
                        {submitting
                            ? "Enviando solicitud..."
                            : "Enviar solicitud"}
                    </button>
                </form>
            </section>
        </main>
    );
}

export default SolicitudAdopcion;
