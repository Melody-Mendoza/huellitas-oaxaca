import {
    useEffect,
    useState
} from "react";
import toast from "react-hot-toast";

import Loader from "../../components/Loader/Loader";
import Modal from "../../components/Modal/Modal";
import Pagination from "../../components/Pagination/Pagination";
import { useAuth } from "../../context/AuthContext";
import api from "../../services/api";

import "./Admin.css";

const dateFormatter = new Intl.DateTimeFormat(
    "es-MX",
    {
        dateStyle: "medium",
        timeStyle: "short"
    }
);

function getFullName(user) {
    const fullName = [
        user?.nombre,
        user?.apellidoPaterno,
        user?.apellidoMaterno
    ]
        .filter(Boolean)
        .join(" ")
        .trim();

    return fullName || "Nombre no disponible";
}

function formatRegistrationDate(value) {
    if (!value) {
        return "Sin fecha";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return "Fecha no disponible";
    }

    return dateFormatter.format(date);
}

function getRequestErrorMessage(error) {
    if (!error.response) {
        return "No fue posible conectar con el backend.";
    }

    const backendMessage =
        error.response.data?.message;

    switch (error.response.status) {
        case 400:
            return (
                backendMessage
                || "La petición enviada no es válida."
            );

        case 401:
            return (
                "La sesión ya no es válida. "
                + "Inicia sesión nuevamente."
            );

        case 403:
            return (
                "No tienes permiso para administrar usuarios."
            );

        case 404:
            return (
                backendMessage
                || "No se encontró el usuario solicitado."
            );

        case 422:
            return (
                backendMessage
                || "La operación incumple una regla de negocio."
            );

        case 500:
            return (
                "Ocurrió un error interno en el servidor."
            );

        default:
            return (
                backendMessage
                || "No fue posible completar la operación."
            );
    }
}

function isValidPage(data) {
    return Boolean(data && Array.isArray(data.content) && Number.isInteger(data.number)
        && Number.isInteger(data.totalPages) && Number.isInteger(data.totalElements)
        && typeof data.first === "boolean" && typeof data.last === "boolean");
}

function Admin() {
    const { user: authenticatedUser } = useAuth();
    const [filters, setFilters] = useState({ texto: "", rol: "", activo: "" });
    const [draftFilters, setDraftFilters] = useState(filters);
    const [page, setPage] = useState(0);
    const [pageData, setPageData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);
    const [updatingUserId, setUpdatingUserId] = useState(null);

    useEffect(() => {
        const controller = new AbortController();
        const loadUsers = async () => {
            setLoading(true);
            setLoadError("");
            try {
                const params = { page, size: 10, sort: "fechaRegistro,desc" };
                Object.entries(filters).forEach(([key, value]) => {
                    if (value) params[key] = value;
                });
                const response = await api.get("/admin/usuarios", { params, signal: controller.signal });
                if (!isValidPage(response.data)) {
                    setLoadError("El backend devolvió una página de usuarios no compatible.");
                    return;
                }
                setPageData(response.data);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) return;
                setLoadError(getRequestErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) setLoading(false);
            }
        };
        loadUsers();
        return () => controller.abort();
    }, [filters, page, retryVersion]);

    const applyFilters = (event) => {
        event.preventDefault();
        setPage(0);
        setFilters({ ...draftFilters, texto: draftFilters.texto.trim() });
    };
    const handleStateChange = async (targetUser) => {
        if (updatingUserId !== null) return;
        const nextActiveState = !targetUser.activo;
        if (!nextActiveState && String(targetUser.id) === String(authenticatedUser?.id)) {
            toast.error("No puedes desactivar tu propia cuenta.");
            return;
        }
        const confirmation = await Modal.confirm(nextActiveState ? "Activar usuario" : "Desactivar usuario", `¿Deseas cambiar el estado de ${getFullName(targetUser)}?`);
        if (!confirmation.isConfirmed) return;
        setUpdatingUserId(targetUser.id);
        try {
            await api.patch(`/admin/usuarios/${targetUser.id}/estado`, { activo: nextActiveState });
            toast.success(nextActiveState ? "Usuario activado correctamente." : "Usuario desactivado correctamente.");
            setRetryVersion((version) => version + 1);
        } catch (error) {
            toast.error(getRequestErrorMessage(error));
        } finally {
            setUpdatingUserId(null);
        }
    };

    if (loading) return <section className="admin-page" aria-label="Cargando usuarios"><h1 className="admin-visually-hidden">Usuarios</h1><Loader /></section>;
    if (loadError) return <section className="admin-page admin-feedback" role="alert"><h1>No fue posible cargar los usuarios</h1><p>{loadError}</p><button type="button" className="admin-primary-button" onClick={() => setRetryVersion((version) => version + 1)}>Reintentar</button></section>;
    const users = pageData?.content ?? [];
    return (
        <section className="admin-page" aria-labelledby="admin-title" aria-busy={updatingUserId !== null}>
            <header className="admin-header">
                <div><p className="admin-eyebrow">Administración</p><h1 id="admin-title">Usuarios</h1><p className="admin-description">Consulta las cuentas registradas y administra su estado de acceso.</p></div>
                <p className="admin-user-count" aria-live="polite">{pageData?.totalElements ?? 0} usuarios</p>
            </header>
            <form className="admin-filters" onSubmit={applyFilters}>
                <label>Buscar<input value={draftFilters.texto} onChange={(event) => setDraftFilters((current) => ({ ...current, texto: event.target.value }))} /></label>
                <label>Rol<select value={draftFilters.rol} onChange={(event) => setDraftFilters((current) => ({ ...current, rol: event.target.value }))}><option value="">Todos</option><option value="ADMIN">Admin</option><option value="USUARIO">Usuario</option><option value="REFUGIO">Refugio</option></select></label>
                <label>Estado<select value={draftFilters.activo} onChange={(event) => setDraftFilters((current) => ({ ...current, activo: event.target.value }))}><option value="">Todos</option><option value="true">Activo</option><option value="false">Inactivo</option></select></label>
                <button type="submit" className="admin-primary-button">Aplicar filtros</button>
            </form>
            {users.length === 0 ? <div className="admin-empty-state" role="status"><h2>No hay usuarios registrados</h2><p>No hay resultados para los filtros seleccionados.</p></div> : (
                <div className="admin-table-container"><table className="admin-users-table"><caption className="admin-visually-hidden">Usuarios registrados en Huellitas Oaxaca</caption><thead><tr><th>Nombre completo</th><th>Correo</th><th>Rol</th><th>Estado</th><th>Fecha de registro</th><th>Acción</th></tr></thead><tbody>
                    {users.map((listedUser) => {
                        const isSelfDeactivation = listedUser.activo && String(listedUser.id) === String(authenticatedUser?.id);
                        const actionText = listedUser.activo ? "Desactivar" : "Activar";
                        return <tr key={listedUser.id}><td data-label="Nombre completo"><strong>{getFullName(listedUser)}</strong></td><td data-label="Correo">{listedUser.correo || "Sin correo"}</td><td data-label="Rol">{listedUser.rol?.nombre || "Sin rol"}</td><td data-label="Estado"><span className={listedUser.activo ? "admin-status admin-status-active" : "admin-status admin-status-inactive"}>{listedUser.activo ? "Activo" : "Inactivo"}</span></td><td data-label="Fecha de registro">{formatRegistrationDate(listedUser.fechaRegistro)}</td><td data-label="Acción"><button type="button" className={listedUser.activo ? "admin-action-button admin-action-danger" : "admin-action-button admin-action-success"} disabled={updatingUserId !== null || isSelfDeactivation} onClick={() => handleStateChange(listedUser)}>{updatingUserId === listedUser.id ? "Actualizando..." : actionText}</button></td></tr>;
                    })}
                </tbody></table></div>
            )}
            {pageData && <Pagination currentPage={pageData.number} totalPages={pageData.totalPages} first={pageData.first} last={pageData.last} onPageChange={setPage} disabled={loading} ariaLabel="Paginación de usuarios" />}
        </section>
    );
}

export default Admin;
