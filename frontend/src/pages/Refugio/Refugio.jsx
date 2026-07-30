import { useEffect, useState } from "react";
import { Link, useOutletContext } from "react-router-dom";

import Loader from "../../components/Loader/Loader";
import api from "../../services/api";

import "./Refugio.css";

const METRICS = [
    ["totalMascotas", "Total de mascotas"],
    ["mascotasDisponibles", "Mascotas disponibles"],
    ["mascotasEnProceso", "Mascotas en proceso"],
    ["mascotasAdoptadas", "Mascotas adoptadas"],
    ["totalSolicitudes", "Total de solicitudes"],
    ["solicitudesPendientes", "Solicitudes pendientes"],
    ["solicitudesAprobadas", "Solicitudes aprobadas"],
    ["solicitudesRechazadas", "Solicitudes rechazadas"]
];

function isValidPanel(data) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && METRICS.every(([property]) => (
            Number.isSafeInteger(data[property]) && data[property] >= 0
        ))
    );
}

function getErrorMessage(error) {
    if (!error.response) {
        return "No fue posible conectar con el backend.";
    }
    const backendMessage = error.response.data?.message;
    switch (error.response.status) {
        case 400:
            return backendMessage || "El identificador del refugio no es válido.";
        case 401:
            return "La sesión ya no es válida. Inicia sesión nuevamente.";
        case 403:
            return backendMessage || "No tienes permiso para consultar este refugio.";
        case 404:
            return backendMessage || "No se encontró el refugio solicitado.";
        case 500:
            return "Ocurrió un error interno en el servidor.";
        default:
            return "No fue posible cargar el panel del refugio.";
    }
}

function Refugio() {
    const { selectedRefuge } = useOutletContext();
    const [panel, setPanel] = useState(null);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);

    useEffect(() => {
        const controller = new AbortController();
        const loadPanel = async () => {
            setLoading(true);
            setPanel(null);
            setErrorMessage("");
            try {
                const response = await api.get(
                    `/refugios/${selectedRefuge.id}/panel`,
                    { signal: controller.signal }
                );
                if (!isValidPanel(response.data)) {
                    setErrorMessage(
                        "El backend devolvió métricas de refugio no compatibles."
                    );
                    return;
                }
                setPanel(response.data);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) {
                    return;
                }
                setErrorMessage(getErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            }
        };
        loadPanel();
        return () => controller.abort();
    }, [selectedRefuge.id, retryVersion]);

    if (loading) {
        return (
            <section className="refuge-panel-loading" aria-labelledby="refuge-panel-loading-title">
                <h1 id="refuge-panel-loading-title" className="refuge-panel-visually-hidden">
                    Panel del refugio
                </h1>
                <Loader />
            </section>
        );
    }
    if (errorMessage || !panel) {
        return (
            <section
                className="refuge-panel-feedback"
                aria-labelledby="refuge-panel-error-title"
                role="alert"
            >
                <h1 id="refuge-panel-error-title">No fue posible cargar el panel</h1>
                <p>{errorMessage || "No fue posible consultar las métricas."}</p>
                <button
                    type="button"
                    onClick={() => setRetryVersion((version) => version + 1)}
                >
                    Reintentar
                </button>
            </section>
        );
    }

    return (
        <section className="refuge-panel" aria-labelledby="refuge-panel-title">
            <header className="refuge-panel-header">
                <div>
                    <p>Administración del refugio</p>
                    <h1 id="refuge-panel-title">{selectedRefuge.nombre}</h1>
                    <p>Métricas actuales de mascotas y solicitudes de adopción.</p>
                </div>
                <Link to="/refugio/perfil">Editar perfil del refugio</Link>
            </header>
            <div className="refuge-metrics">
                {METRICS.map(([property, label]) => (
                    <article className="refuge-metric-card" key={property}>
                        <strong>{panel[property]}</strong>
                        <h2>{label}</h2>
                    </article>
                ))}
            </div>
        </section>
    );
}

export default Refugio;
