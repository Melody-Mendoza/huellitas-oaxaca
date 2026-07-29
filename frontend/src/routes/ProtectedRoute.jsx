import {
    Link,
    Navigate,
    useLocation
} from "react-router-dom";

import Loader from "../components/Loader/Loader";
import { useAuth } from "../context/AuthContext";
import { getUserRole } from "../utils/constants";

function ProtectedRoute({
    children,
    allowedRoles = []
}) {
    const {
        token,
        user,
        loading
    } = useAuth();

    const location = useLocation();

    if (loading) {
        return <Loader />;
    }

    if (!token || !user) {
        return (
            <Navigate
                to="/login"
                replace
                state={{
                    from: location
                }}
            />
        );
    }

    const requiresRole =
        Array.isArray(allowedRoles)
        && allowedRoles.length > 0;

    if (requiresRole) {
        const userRole = getUserRole(user);

        const hasAllowedRole =
            allowedRoles.includes(userRole);

        if (!hasAllowedRole) {
            return (
                <section
                    role="alert"
                    aria-labelledby="access-denied-title"
                >
                    <h1 id="access-denied-title">
                        Acceso denegado
                    </h1>

                    <p>
                        No tienes permiso para acceder a esta sección.
                    </p>

                    <Link to="/">
                        Volver al inicio
                    </Link>
                </section>
            );
        }
    }

    return children;
}

export default ProtectedRoute;