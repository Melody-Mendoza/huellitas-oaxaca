import { useEffect, useState } from "react";
import { NavLink, Outlet } from "react-router-dom";
import Loader from "../components/Loader/Loader";
import Navbar from "../components/Navbar/Navbar";
import api from "../services/api";
import "./RefugioLayout.css";

function isNullableString(value) {
    return value === null || typeof value === "string";
}

function isValidRefuge(refuge) {
    return Boolean(
        refuge
        && typeof refuge === "object"
        && !Array.isArray(refuge)
        && Number.isSafeInteger(refuge.id)
        && refuge.id > 0
        && typeof refuge.nombre === "string"
        && refuge.nombre.trim()
        && isNullableString(refuge.descripcion)
        && isNullableString(refuge.direccion)
        && isNullableString(refuge.telefono)
        && isNullableString(refuge.correo)
        && typeof refuge.activo === "boolean"
    );
}

function isValidRefugeList(data) {
    if (!Array.isArray(data) || !data.every(isValidRefuge)) { return false; }
    return new Set(data.map((refuge) => refuge.id)).size === data.length;
}

function getErrorMessage(error) {
    if (!error.response) { return "No fue posible conectar con el backend."; }
    const backendMessage = error.response.data?.message;
    switch (error.response.status) {
        case 400:
            return backendMessage || "La solicitud no es válida.";
        case 401:
            return "La sesión ya no es válida. Inicia sesión nuevamente.";
        case 403:
            return "No tienes permiso para administrar refugios.";
        case 404:
            return backendMessage || "No se encontró el refugio solicitado.";
        case 422:
            return backendMessage || "No fue posible procesar la solicitud.";
        case 500:
            return "Ocurrió un error interno en el servidor.";
        default:
            return "No fue posible cargar tus refugios.";
    }
}

function RefugioLayout() {
    const [refuges, setRefuges] = useState([]);
    const [selectedId, setSelectedId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);

    useEffect(() => {
        const controller = new AbortController();
        const loadRefuges = async () => {
            setLoading(true);
            setErrorMessage("");
            try {
                const response = await api.get("/refugios/mis-refugios", { signal: controller.signal });
                if (!isValidRefugeList(response.data)) {
                    setRefuges([]);
                    setSelectedId(null);
                    setErrorMessage("El backend devolvió una lista de refugios no compatible.");
                    return;
                }
                setRefuges(response.data);
                setSelectedId((currentId) => (response.data.some((refuge) => refuge.id === currentId) ? currentId : response.data[0]?.id ?? null));
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
                setRefuges([]);
                setSelectedId(null);
                setErrorMessage(getErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) { setLoading(false); }
            }
        };
        loadRefuges();
        return () => controller.abort();
    }, [retryVersion]);

    const selectedRefuge = refuges.find((refuge) => refuge.id === selectedId) ?? null;
    const updateSelectedRefuge = (updatedRefuge) => {
        if (!isValidRefuge(updatedRefuge)) { return; }
        setRefuges((currentRefuges) => currentRefuges.map((refuge) => (refuge.id === updatedRefuge.id ? updatedRefuge : refuge)));
    };

    let content;
    if (loading) {
        content = (
            <section className="refuge-layout-feedback" aria-labelledby="refuge-loading-title">
                <h1 id="refuge-loading-title" className="refuge-visually-hidden">Área de refugio</h1>
                <Loader />
            </section>
        );
    } else if (errorMessage) {
        content = (
            <section className="refuge-layout-feedback" aria-labelledby="refuge-error-title" role="alert">
                <h1 id="refuge-error-title">No fue posible cargar tus refugios</h1>
                <p>{errorMessage}</p>
                <button type="button" onClick={() => setRetryVersion((version) => version + 1)}>Reintentar</button>
            </section>
        );
    } else if (refuges.length === 0) {
        content = (
            <section className="refuge-layout-feedback" aria-labelledby="refuge-empty-title" role="status">
                <h1 id="refuge-empty-title">No tienes refugios activos</h1>
                <p>Tu cuenta no tiene refugios activos asignados actualmente.</p>
            </section>
        );
    } else if (selectedRefuge) {
        content = (
            <>
                <div className="refuge-layout-toolbar">
                    <div className="refuge-selector">
                        {refuges.length > 1 ? (
                            <>
                                <label htmlFor="refuge-selection">Refugio administrado</label>
                                <select id="refuge-selection" value={selectedRefuge.id} onChange={(event) => setSelectedId(Number(event.target.value))}>
                                    {refuges.map((refuge) => (
                                        <option key={refuge.id} value={refuge.id}>{refuge.nombre}</option>
                                    ))}
                                </select>
                            </>
                        ) : (
                            <div>
                                <span>Refugio administrado</span>
                                <strong>{selectedRefuge.nombre}</strong>
                            </div>
                        )}
                    </div>
                    <nav className="refuge-navigation" aria-label="Navegación del refugio">
                        <NavLink to="/refugio" end className={({ isActive }) => isActive ? "active" : undefined}>Panel</NavLink>
                        <NavLink to="/refugio/perfil" className={({ isActive }) => isActive ? "active" : undefined}>Perfil del refugio</NavLink>
                    </nav>
                </div>
                <div key={selectedRefuge.id}>
                    <Outlet context={{ selectedRefuge, updateSelectedRefuge }} />
                </div>
            </>
        );
    }

    return (
        <>
            <Navbar />
            <main className="refuge-layout">{content}</main>
        </>
    );
}

export default RefugioLayout;