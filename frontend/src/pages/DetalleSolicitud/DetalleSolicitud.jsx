import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import Loader from "../../components/Loader/Loader";
import api from "../../services/api";
import { resolveMediaUrl } from "../../utils/media";
import "./DetalleSolicitud.css";
const VALID_STATUSES = new Set(["PENDIENTE", "APROBADA", "RECHAZADA"]);
const STATUS_LABELS = {PENDIENTE: "Pendiente",APROBADA: "Aprobada",RECHAZADA: "Rechazada"};
const dateFormatter = new Intl.DateTimeFormat("es-MX", {dateStyle: "long",timeStyle: "short"});
function parseRequestId(value) {
    if (typeof value !== "string" || !/^[1-9]\d*$/.test(value)) { return null; }
    const parsedValue = Number(value);
    return Number.isSafeInteger(parsedValue) && parsedValue > 0? parsedValue: null;
}
function isNullableString(value) { return value === null || typeof value === "string"; }
function isNullablePositiveId(value) { return value === null || ( Number.isSafeInteger(value) && value > 0 ); }
function isValidDetail(data, requestId) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && data.id === requestId
        && typeof data.fechaSolicitud === "string"
        && data.fechaSolicitud.trim()
        && VALID_STATUSES.has(data.estado)
        && isNullableString(data.comentarios)
        && isNullablePositiveId(data.mascotaId)
        && isNullableString(data.nombreMascota)
        && isNullableString(data.imagenPrincipal)
        && isNullablePositiveId(data.refugioId)
        && isNullableString(data.nombreRefugio)
    );
}
function isValidHistory(data) {return Array.isArray(data) && data.every((item) => ( item && typeof item === "object" && !Array.isArray(item) && VALID_STATUSES.has(item.estado) && typeof item.fecha === "string" && item.fecha.trim() )); }
function formatDate(value) { const date = new Date(value);  return Number.isNaN(date.getTime()) ? "Fecha no disponible" : dateFormatter.format(date); }
function getRequestErrorMessage(error) {
    if (!error.response) { return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."; }
    const backendMessage = error.response.data?.message;
    switch (error.response.status) {
        case 400:
            return backendMessage || "El identificador de la solicitud no es válido.";
        case 401:
            return "La sesión ya no es válida. Inicia sesión nuevamente.";
        case 403:
            return "No tienes permiso para consultar esta solicitud.";
        case 404:
            return "La solicitud no fue encontrada o no te pertenece.";
        case 500:
            return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde.";
        default:
            return backendMessage || "No fue posible consultar la solicitud.";
    }
}
function DetailImage({ detail }) {
    const [imageFailed, setImageFailed] = useState(false);
    const imageUrl = resolveMediaUrl(detail.imagenPrincipal);
    const petName = detail.nombreMascota?.trim() || "la mascota";
    if (!imageUrl || imageFailed) {
        return (
            <div className="detalle-solicitud-image-placeholder" role="img" aria-label={`${petName} no tiene imagen disponible`} >Sin imagen disponible</div>
        );
    }
    return (
        <img src={imageUrl} alt={`Fotografía de ${petName}`} onError={() => { setImageFailed(true); }} />
    );
}
function DetalleSolicitud() {
    const { solicitudId } = useParams();
    const requestId = parseRequestId(solicitudId);
    const [detail, setDetail] = useState(null);
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(requestId !== null);
    const [detailError, setDetailError] = useState("");
    const [historyError, setHistoryError] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);
    useEffect(() => {
        if (requestId === null) { return undefined; }
        const controller = new AbortController();
        const loadRequest = async () => {
            setLoading(true);
            setDetail(null);
            setHistory([]);
            setDetailError("");
            setHistoryError("");
            const requestUrl = `/solicitudes/mis-solicitudes/${requestId}`;
            const [detailResult, historyResult] = await Promise.allSettled([ api.get(requestUrl, { signal: controller.signal }), api.get(`${requestUrl}/historial`, { signal: controller.signal }) ]);
            if (controller.signal.aborted) { return; }
            if (detailResult.status === "fulfilled") {
                if (isValidDetail(detailResult.value.data, requestId)) {
                    setDetail(detailResult.value.data);
                } else {
                    setDetailError( "Recibimos una respuesta inesperada. Intenta de nuevo." );
                }
            } else if (detailResult.reason?.code !== "ERR_CANCELED") { setDetailError( getRequestErrorMessage(detailResult.reason) ); }
            if (historyResult.status === "fulfilled") {
                if (isValidHistory(historyResult.value.data)) {
                    setHistory(historyResult.value.data);
                } else {
                    setHistoryError( "Recibimos una respuesta inesperada. Intenta de nuevo." );
                }
            } else if (historyResult.reason?.code !== "ERR_CANCELED") { setHistoryError( getRequestErrorMessage(historyResult.reason) ); }
            setLoading(false);
        };
        loadRequest();
        return () => { controller.abort(); };
    }, [requestId, retryVersion]);
    if (requestId === null) {
        return (
            <section className="detalle-solicitud-page detalle-solicitud-feedback" aria-labelledby="detalle-solicitud-invalid-title" role="alert" >
                <h1 id="detalle-solicitud-invalid-title">Identificador no válido</h1>
                <p>El identificador de la solicitud debe ser un entero positivo válido.</p>
                <Link to="/mis-solicitudes">Volver a mis solicitudes</Link>
            </section>
        );
    }
    if (loading) {
        return (
            <section className="detalle-solicitud-page detalle-solicitud-loading" aria-labelledby="detalle-solicitud-loading-title" >
                <h1 id="detalle-solicitud-loading-title" className="detalle-solicitud-visually-hidden" >Detalle de solicitud</h1>
                <Loader />
            </section>
        );
    }
    if (detailError || !detail) {
        return (
            <section className="detalle-solicitud-page detalle-solicitud-feedback" aria-labelledby="detalle-solicitud-error-title" role="alert" >
                <h1 id="detalle-solicitud-error-title">No fue posible cargar la solicitud</h1>
                <p>{detailError || "No fue posible consultar el detalle solicitado."}</p>
                <div className="detalle-solicitud-feedback-actions">
                    <button type="button" onClick={() => { setRetryVersion( (currentVersion) => currentVersion + 1 ); }} >Reintentar</button>
                    <Link to="/mis-solicitudes">Volver a mis solicitudes</Link>
                </div>
            </section>
        );
    }
    const comments = detail.comentarios?.trim();
    return (
        <section className="detalle-solicitud-page" aria-labelledby="detalle-solicitud-title" >
            <Link className="detalle-solicitud-back-link" to="/mis-solicitudes" >Volver a mis solicitudes</Link>
            <header className="detalle-solicitud-header">
                <div>
                    <p>Solicitud #{detail.id}</p>
                    <h1 id="detalle-solicitud-title">{detail.nombreMascota?.trim() || "Detalle de solicitud"}</h1>
                    <p>Registrada el {formatDate(detail.fechaSolicitud)}</p>
                </div>
                <span className={`solicitud-status solicitud-status-${detail.estado.toLowerCase()}`} >{STATUS_LABELS[detail.estado]}</span>
            </header>
            <div className="detalle-solicitud-grid">
                <div className="detalle-solicitud-image">
                    <DetailImage detail={detail} />
                </div>
                <div className="detalle-solicitud-summary">
                    <h2>Información de la solicitud</h2>
                    <dl>
                        <div>
                            <dt>Mascota</dt>
                            <dd>{detail.nombreMascota?.trim() || "No disponible"}</dd>
                        </div>
                        <div>
                            <dt>Refugio</dt>
                            <dd>{detail.nombreRefugio?.trim() || "No disponible"}</dd>
                        </div>
                        <div>
                            <dt>Estado actual</dt>
                            <dd>{STATUS_LABELS[detail.estado]}</dd>
                        </div>
                    </dl>
                    {detail.mascotaId !== null && (
                        <Link className="detalle-solicitud-pet-link" to={`/mascota/${detail.mascotaId}`} >Ver mascota</Link>
                    )}
                </div>
            </div>
            <section className="detalle-solicitud-panel" aria-labelledby="detalle-solicitud-comments-title" >
                <h2 id="detalle-solicitud-comments-title">Comentarios enviados</h2>
                <p>{comments || "No agregaste comentarios a esta solicitud."}</p>
            </section>
            <section className="detalle-solicitud-panel" aria-labelledby="detalle-solicitud-history-title" >
                <h2 id="detalle-solicitud-history-title">Historial</h2>
                {historyError ? (
                    <div className="detalle-solicitud-history-error" role="alert" >
                        <p>{historyError}</p>
                        <button type="button" onClick={() => { setRetryVersion( (currentVersion) => currentVersion + 1 ); }} >Reintentar historial</button>
                    </div>
                ) : history.length === 0 ? (
                    <p role="status">Sin movimientos registrados.</p>
                ) : (
                    <ol className="detalle-solicitud-timeline">
                        {history.map((item, index) => (
                            <li key={`${item.estado}-${item.fecha}-${index}`}>
                                <span className={`solicitud-status solicitud-status-${item.estado.toLowerCase()}`} >{STATUS_LABELS[item.estado]}</span>
                                <time dateTime={item.fecha}>{formatDate(item.fecha)}</time>
                            </li>
                        ))}
                    </ol>
                )}
            </section>
        </section>
    );
}
export default DetalleSolicitud;
