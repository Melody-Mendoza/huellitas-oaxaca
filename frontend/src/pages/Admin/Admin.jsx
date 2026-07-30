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
        return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde.";
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
                "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."
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
    const [refugePage, setRefugePage] = useState(null);
    const [refugeLoading, setRefugeLoading] = useState(true);
    const [refugeError, setRefugeError] = useState("");
    const [refugeRetry, setRefugeRetry] = useState(0);
    const [selectedRefuge, setSelectedRefuge] = useState(null);
    const [adminForm, setAdminForm] = useState({ nombre: "", apellidoPaterno: "", apellidoMaterno: "", correo: "", telefono: "", password: "", confirmarPassword: "" });
    const [refugeForm, setRefugeForm] = useState({ responsableNombre: "", responsableApellidoPaterno: "", responsableApellidoMaterno: "", responsableCorreo: "", responsableTelefono: "", responsablePassword: "", confirmarPassword: "", nombre: "", descripcion: "", direccion: "", telefono: "", correo: "", motivo: "" });
    const [formSaving, setFormSaving] = useState(false);

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
                    setLoadError("Recibimos una respuesta inesperada. Intenta de nuevo.");
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

    useEffect(() => {
        const controller = new AbortController();
        const loadRefuges = async () => {
            setRefugeLoading(true);
            setRefugeError("");
            try {
                const response = await api.get("/admin/refugios", { params: { page: 0, size: 50, sort: "nombre,asc" }, signal: controller.signal });
                setRefugePage(response.data);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) return;
                setRefugeError(getRequestErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) setRefugeLoading(false);
            }
        };
        loadRefuges();
        return () => controller.abort();
    }, [refugeRetry]);

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

    const updateAdminForm = (event) => setAdminForm((current) => ({ ...current, [event.target.name]: event.target.value }));
    const updateRefugeForm = (event) => setRefugeForm((current) => ({ ...current, [event.target.name]: event.target.value }));

    const createAdmin = async (event) => {
        event.preventDefault();
        if (adminForm.password !== adminForm.confirmarPassword) {
            toast.error("Las contraseñas no coinciden.");
            return;
        }
        setFormSaving(true);
        try {
            const payload = {
                nombre: adminForm.nombre,
                apellidoPaterno: adminForm.apellidoPaterno,
                apellidoMaterno: adminForm.apellidoMaterno,
                correo: adminForm.correo,
                telefono: adminForm.telefono,
                password: adminForm.password
            };
            await api.post("/admin/usuarios/administradores", payload);
            toast.success("Administrador creado correctamente.");
            setAdminForm({ nombre: "", apellidoPaterno: "", apellidoMaterno: "", correo: "", telefono: "", password: "", confirmarPassword: "" });
            setRetryVersion((version) => version + 1);
        } catch (error) {
            toast.error(error.response?.data?.message || "No fue posible crear el administrador.");
        } finally {
            setFormSaving(false);
        }
    };

    const createRefuge = async (event) => {
        event.preventDefault();
        if (refugeForm.responsablePassword !== refugeForm.confirmarPassword) {
            toast.error("Las contraseñas del responsable no coinciden.");
            return;
        }
        setFormSaving(true);
        try {
            const payload = Object.fromEntries(
                Object.entries(refugeForm).filter(([key]) => key !== "confirmarPassword")
            );
            await api.post("/admin/refugios/completo", payload);
            toast.success("Cuenta y refugio creados correctamente.");
            setRefugeForm({ responsableNombre: "", responsableApellidoPaterno: "", responsableApellidoMaterno: "", responsableCorreo: "", responsableTelefono: "", responsablePassword: "", confirmarPassword: "", nombre: "", descripcion: "", direccion: "", telefono: "", correo: "", motivo: "" });
            setRefugeRetry((version) => version + 1);
            setRetryVersion((version) => version + 1);
        } catch (error) {
            toast.error(error.response?.data?.message || "No fue posible registrar el refugio.");
        } finally {
            setFormSaving(false);
        }
    };

    const changeRefugeState = async (refuge, field, value, message) => {
        try {
            await api.patch(`/admin/refugios/${refuge.id}/${field}`, { [field === "aprobacion" ? "aprobado" : "activo"]: value, motivo: message });
            toast.success("Estado del refugio actualizado.");
            setRefugeRetry((version) => version + 1);
        } catch (error) {
            toast.error(error.response?.data?.message || "No fue posible actualizar el refugio.");
        }
    };

    const showRefugeDetail = async (id) => {
        try {
            const response = await api.get(`/admin/refugios/${id}`);
            setSelectedRefuge(response.data);
        } catch (error) {
            toast.error(error.response?.data?.message || "No fue posible consultar el refugio.");
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

            <section className="admin-management-section" aria-labelledby="admin-create-title">
                <div className="admin-section-heading"><div><p className="admin-eyebrow">Cuentas privilegiadas</p><h2 id="admin-create-title">Crear administrador</h2></div></div>
                <form className="admin-management-form" onSubmit={createAdmin}>
                    {["nombre", "apellidoPaterno", "apellidoMaterno", "correo", "telefono", "password", "confirmarPassword"].map((field) => <label key={field}>{({ nombre: "Nombre", apellidoPaterno: "Apellido paterno", apellidoMaterno: "Apellido materno", correo: "Correo", telefono: "Teléfono", password: "Contraseña", confirmarPassword: "Confirmar contraseña" })[field]}<input name={field} type={field.toLowerCase().includes("password") ? "password" : field === "correo" ? "email" : "text"} value={adminForm[field]} onChange={updateAdminForm} required={!['apellidoMaterno', 'telefono'].includes(field)} /></label>)}
                    <button type="submit" className="admin-primary-button" disabled={formSaving}>{formSaving ? "Guardando..." : "Crear administrador"}</button>
                </form>
            </section>

            <section className="admin-management-section" aria-labelledby="refuges-title">
                <div className="admin-section-heading"><div><p className="admin-eyebrow">Operación</p><h2 id="refuges-title">Refugios</h2></div><p className="admin-user-count">{refugePage?.totalElements ?? 0} refugios</p></div>
                <form className="admin-management-form admin-refuge-form" onSubmit={createRefuge}>
                    <h3>Registrar refugio y responsable</h3>
                    {["responsableNombre", "responsableApellidoPaterno", "responsableApellidoMaterno", "responsableCorreo", "responsableTelefono", "responsablePassword", "confirmarPassword", "nombre", "direccion", "telefono", "correo", "motivo"].map((field) => <label key={field}>{({ responsableNombre: "Nombre del responsable", responsableApellidoPaterno: "Apellido paterno", responsableApellidoMaterno: "Apellido materno", responsableCorreo: "Correo del responsable", responsableTelefono: "Teléfono del responsable", responsablePassword: "Contraseña del responsable", confirmarPassword: "Confirmar contraseña", nombre: "Nombre del refugio", direccion: "Dirección", telefono: "Teléfono del refugio", correo: "Correo del refugio", motivo: "Motivo" })[field]}<input name={field} type={field.toLowerCase().includes("password") ? "password" : field.toLowerCase().includes("correo") ? "email" : "text"} value={refugeForm[field]} onChange={updateRefugeForm} required={!['responsableApellidoMaterno'].includes(field)} /></label>)}
                    <label className="admin-form-wide">Descripción<textarea name="descripcion" value={refugeForm.descripcion} onChange={updateRefugeForm} required rows="3" /></label>
                    <button type="submit" className="admin-primary-button" disabled={formSaving}>{formSaving ? "Guardando..." : "Registrar refugio"}</button>
                </form>
                {refugeLoading ? <Loader /> : refugeError ? <div className="admin-empty-state" role="alert"><p>{refugeError}</p><button type="button" className="admin-primary-button" onClick={() => setRefugeRetry((version) => version + 1)}>Reintentar</button></div> : <div className="admin-table-container"><table className="admin-users-table"><caption className="admin-visually-hidden">Refugios registrados</caption><thead><tr><th>Refugio</th><th>Responsable</th><th>Estado</th><th>Aprobación</th><th>Acciones</th></tr></thead><tbody>{(refugePage?.content ?? []).map((refuge) => <tr key={refuge.id}><td data-label="Refugio"><strong>{refuge.nombre}</strong></td><td data-label="Responsable">{refuge.responsable?.correo || "Sin responsable"}</td><td data-label="Estado">{refuge.activo ? "Activo" : "Inactivo"}</td><td data-label="Aprobación">{refuge.aprobado ? "Aprobado" : "Pendiente"}</td><td data-label="Acciones"><button type="button" className="admin-action-button admin-action-success" onClick={() => showRefugeDetail(refuge.id)}>Ver detalle</button>{!refuge.aprobado && <button type="button" className="admin-action-button admin-action-success" onClick={() => changeRefugeState(refuge, "aprobacion", true, "Aprobación administrativa")}>Aprobar</button>}{refuge.aprobado && <button type="button" className="admin-action-button admin-action-danger" onClick={() => changeRefugeState(refuge, "aprobacion", false, "Revisión administrativa")}>Rechazar</button>}<button type="button" className="admin-action-button" onClick={() => changeRefugeState(refuge, "estado", !refuge.activo, refuge.activo ? "Pausa operativa" : "Reactivación operativa")}>{refuge.activo ? "Desactivar" : "Activar"}</button></td></tr>)}</tbody></table></div>}
                {selectedRefuge && <div className="admin-empty-state" role="dialog" aria-label="Detalle del refugio"><h3>{selectedRefuge.nombre}</h3><p>{selectedRefuge.descripcion}</p><p>{selectedRefuge.direccion} · {selectedRefuge.telefono}</p><p>Responsable: {selectedRefuge.responsable?.correo || "No disponible"}</p><button type="button" className="admin-primary-button" onClick={() => setSelectedRefuge(null)}>Cerrar</button></div>}
            </section>
        </section>
    );
}

export default Admin;
