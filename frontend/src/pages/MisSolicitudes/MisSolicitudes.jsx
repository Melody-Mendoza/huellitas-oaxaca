import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Loader from "../../components/Loader/Loader";
import Pagination from "../../components/Pagination/Pagination";
import api from "../../services/api";
import "./MisSolicitudes.css";

const PAGE_SIZE = 10;
const VALID_STATUSES = new Set(["PENDIENTE", "APROBADA", "RECHAZADA"]);
const STATUS_LABELS = { PENDIENTE: "Pendiente", APROBADA: "Aprobada", RECHAZADA: "Rechazada" };
const EMPTY_PAGE = { content: [], number: 0, size: PAGE_SIZE, totalElements: 0, totalPages: 0, first: true, last: true };
const dateFormatter = new Intl.DateTimeFormat("es-MX", { dateStyle: "long", timeStyle: "short" });

function isNonNegativeInteger(value) {
    return Number.isInteger(value) && value >= 0;
}

function isNullableString(value) {
    return value === null || typeof value === "string";
}

function isNullablePositiveId(value) {
    return value === null || (Number.isSafeInteger(value) && value > 0);
}

function isValidRequestSummary(data) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && Number.isSafeInteger(data.id)
        && data.id > 0
        && typeof data.fechaSolicitud === "string"
        && data.fechaSolicitud.trim()
        && VALID_STATUSES.has(data.estado)
        && isNullablePositiveId(data.mascotaId)
        && isNullableString(data.nombreMascota)
        && isNullableString(data.imagenPrincipal)
        && isNullablePositiveId(data.refugioId)
        && isNullableString(data.nombreRefugio)
    );
}

function isValidPageResponse(data) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && Array.isArray(data.content)
        && data.content.every(isValidRequestSummary)
        && isNonNegativeInteger(data.number)
        && Number.isInteger(data.size)
        && data.size > 0
        && isNonNegativeInteger(data.totalElements)
        && isNonNegativeInteger(data.totalPages)
        && typeof data.first === "boolean"
        && typeof data.last === "boolean"
    );
}

function formatDate(value) {
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? "Fecha no disponible" : dateFormatter.format(date);
}

function getRequestErrorMessage(error) {
    if (!error.response) { return "No fue posible conectar con el backend."; }
    const backendMessage = error.response.data?.message;
    switch (error.response.status) {
        case 400:
            return backendMessage || "Los parámetros de paginación no son válidos.";
        case 401:
            return "La sesión ya no es válida. Inicia sesión nuevamente.";
        case 403:
            return "No tienes permiso para consultar estas solicitudes.";
        case 404:
            return backendMessage || "No se encontró el recurso solicitado.";
        case 500:
            return "Ocurrió un error interno en el servidor.";
        default:
            return backendMessage || "No fue posible cargar tus solicitudes.";
    }
}

function RequestImage({ request }) {
    const [imageFailed, setImageFailed] = useState(false);
    const imageUrl = typeof request.imagenPrincipal === "string" ? request.imagenPrincipal.trim() : "";
    const petName = request.nombreMascota?.trim() || "la mascota";

    if (!imageUrl || imageFailed) {
        return (
            <div className="mis-solicitudes-image-placeholder" role="img" aria-label={`${petName} no tiene imagen disponible`}>Sin imagen</div>
        );
    }

    return (
        <img src={imageUrl} alt={`Fotografía de ${petName}`} onError={() => { setImageFailed(true); }} />
    );
}

function MisSolicitudes() {
    const [page, setPage] = useState(0);
    const [pageData, setPageData] = useState(EMPTY_PAGE);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);

    useEffect(() => {
        const controller = new AbortController();

        const loadRequests = async () => {
            setLoading(true);
            setErrorMessage("");

            try {
                const response = await api.get("/solicitudes/mis-solicitudes", { params: { page, size: PAGE_SIZE }, signal: controller.signal });

                if (!isValidPageResponse(response.data)) {
                    setPageData(EMPTY_PAGE);
                    setErrorMessage("El backend devolvió una estructura de paginación no compatible.");
                    return;
                }

                if (response.data.totalPages > 0 && response.data.number >= response.data.totalPages) {
                    setPage(response.data.totalPages - 1);
                    return;
                }

                setPageData(response.data);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
                setPageData(EMPTY_PAGE);
                setErrorMessage(getRequestErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) { setLoading(false); }
            }
        };

        loadRequests();
        return () => { controller.abort(); };
    }, [page, retryVersion]);

    return (
        <section className="mis-solicitudes-page" aria-labelledby="mis-solicitudes-title" aria-busy={loading}>
            <header className="mis-solicitudes-header">
                <div>
                    <p className="mis-solicitudes-eyebrow">Seguimiento de adopción</p>
                    <h1 id="mis-solicitudes-title">Mis solicitudes</h1>
                    <p>Consulta el estado de tus solicitudes y revisa su historial.</p>
                </div>
                <p className="mis-solicitudes-total" aria-live="polite">
                    <strong>{pageData.totalElements}</strong>{" "}
                    {pageData.totalElements === 1 ? "solicitud" : "solicitudes"}
                </p>
            </header>

            {loading ? (
                <Loader />
            ) : errorMessage ? (
                <div className="mis-solicitudes-feedback mis-solicitudes-error" role="alert">
                    <h2>No fue posible cargar tus solicitudes</h2>
                    <p>{errorMessage}</p>
                    <button type="button" onClick={() => { setRetryVersion((currentVersion) => currentVersion + 1); }}>Reintentar</button>
                </div>
            ) : pageData.content.length === 0 ? (
                <div className="mis-solicitudes-feedback" role="status">
                    <h2>Aún no tienes solicitudes</h2>
                    <p>Cuando solicites adoptar una mascota, podrás consultar aquí su seguimiento.</p>
                    <Link to="/catalogo">Explorar mascotas</Link>
                </div>
            ) : (
                <>
                    <div className="mis-solicitudes-grid">
                        {pageData.content.map((request) => (
                            <article className="mis-solicitudes-card" key={request.id}>
                                <div className="mis-solicitudes-image">
                                    <RequestImage request={request} />
                                </div>
                                <div className="mis-solicitudes-card-body">
                                    <div className="mis-solicitudes-card-heading">
                                        <div>
                                            <p>Solicitud #{request.id}</p>
                                            <h2>{request.nombreMascota?.trim() || "Mascota no disponible"}</h2>
                                        </div>
                                        <span className={`solicitud-status solicitud-status-${request.estado.toLowerCase()}`}>{STATUS_LABELS[request.estado]}</span>
                                    </div>
                                    <dl className="mis-solicitudes-data">
                                        <div>
                                            <dt>Fecha de solicitud</dt>
                                            <dd>{formatDate(request.fechaSolicitud)}</dd>
                                        </div>
                                        <div>
                                            <dt>Refugio</dt>
                                            <dd>{request.nombreRefugio?.trim() || "No disponible"}</dd>
                                        </div>
                                    </dl>
                                    <Link className="mis-solicitudes-detail-link" to={`/mis-solicitudes/${request.id}`}>Ver detalle y seguimiento</Link>
                                </div>
                            </article>
                        ))}
                    </div>
                    <Pagination currentPage={pageData.number} totalPages={pageData.totalPages} first={pageData.first} last={pageData.last} onPageChange={setPage} disabled={loading} ariaLabel="Paginación de mis solicitudes" />
                </>
            )}
        </section>
    );
}

export default MisSolicitudes;