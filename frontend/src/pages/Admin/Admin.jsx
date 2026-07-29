import {
    useEffect,
    useState
} from "react";

import Loader from "../../components/Loader/Loader";
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
                "No tienes permiso para consultar "
                + "los usuarios."
            );

        case 404:
            return (
                backendMessage
                || "No se encontró el recurso solicitado."
            );

        case 500:
            return (
                "Ocurrió un error interno en el servidor."
            );

        default:
            return (
                backendMessage
                || "No fue posible cargar los usuarios."
            );
    }
}

function Admin() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [loadError, setLoadError] = useState("");
    const [retryVersion, setRetryVersion] =
        useState(0);

    useEffect(() => {
        const controller = new AbortController();

        const loadUsers = async () => {
            setLoading(true);
            setLoadError("");

            try {
                const response = await api.get(
                    "/usuarios",
                    {
                        signal: controller.signal
                    }
                );

                const receivedUsers =
                    Array.isArray(response.data)
                        ? response.data
                        : [];

                setUsers(receivedUsers);
            } catch (error) {
                if (
                    error.code === "ERR_CANCELED"
                    || controller.signal.aborted
                ) {
                    return;
                }

                setLoadError(
                    getRequestErrorMessage(error)
                );
            } finally {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            }
        };

        loadUsers();

        return () => {
            controller.abort();
        };
    }, [retryVersion]);

    if (loading) {
        return <Loader />;
    }

    if (loadError) {
        return (
            <section
                className="admin-page admin-feedback"
                role="alert"
                aria-labelledby="admin-error-title"
            >
                <h1 id="admin-error-title">
                    No fue posible cargar los usuarios
                </h1>

                <p>{loadError}</p>

                <button
                    type="button"
                    className="admin-primary-button"
                    onClick={() => {
                        setRetryVersion(
                            (currentVersion) =>
                                currentVersion + 1
                        );
                    }}
                >
                    Reintentar
                </button>
            </section>
        );
    }

    return (
        <section
            className="admin-page"
            aria-labelledby="admin-title"
        >
            <header className="admin-header">
                <div>
                    <p className="admin-eyebrow">
                        Administración
                    </p>

                    <h1 id="admin-title">
                        Usuarios
                    </h1>

                    <p className="admin-description">
                        Consulta las cuentas registradas
                        en Huellitas Oaxaca.
                    </p>
                </div>

                <p
                    className="admin-user-count"
                    aria-live="polite"
                >
                    {users.length}{" "}
                    {users.length === 1
                        ? "usuario"
                        : "usuarios"}
                </p>
            </header>

            {users.length === 0 ? (
                <div
                    className="admin-empty-state"
                    role="status"
                >
                    <h2>
                        No hay usuarios registrados
                    </h2>

                    <p>
                        El backend devolvió un listado
                        vacío.
                    </p>
                </div>
            ) : (
                <div className="admin-table-container">
                    <table className="admin-users-table">
                        <caption className="admin-visually-hidden">
                            Usuarios registrados en
                            Huellitas Oaxaca
                        </caption>

                        <thead>
                            <tr>
                                <th scope="col">
                                    Nombre completo
                                </th>

                                <th scope="col">
                                    Correo
                                </th>

                                <th scope="col">
                                    Rol
                                </th>

                                <th scope="col">
                                    Estado
                                </th>

                                <th scope="col">
                                    Fecha de registro
                                </th>
                            </tr>
                        </thead>

                        <tbody>
                            {users.map((listedUser) => (
                                <tr key={listedUser.id}>
                                    <td
                                        data-label={
                                            "Nombre completo"
                                        }
                                    >
                                        <strong>
                                            {getFullName(
                                                listedUser
                                            )}
                                        </strong>
                                    </td>

                                    <td data-label="Correo">
                                        {listedUser.correo
                                            || "Sin correo"}
                                    </td>

                                    <td data-label="Rol">
                                        {listedUser.rol?.nombre
                                            || "Sin rol"}
                                    </td>

                                    <td data-label="Estado">
                                        <span
                                            className={
                                                listedUser.activo
                                                    ? "admin-status admin-status-active"
                                                    : "admin-status admin-status-inactive"
                                            }
                                        >
                                            {listedUser.activo
                                                ? "Activo"
                                                : "Inactivo"}
                                        </span>
                                    </td>

                                    <td
                                        data-label={
                                            "Fecha de registro"
                                        }
                                    >
                                        {formatRegistrationDate(
                                            listedUser.fechaRegistro
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            <p className="admin-pagination-notice">
                El listado todavía no incluye
                paginación porque el backend no ofrece
                paginación del lado del servidor.
            </p>
        </section>
    );
}

export default Admin;