import {
    useEffect,
    useRef,
    useState
} from "react";

import {
    Link,
    useNavigate
} from "react-router-dom";

import {
    ChevronDown,
    LayoutDashboard
} from "lucide-react";

import toast from "react-hot-toast";

import logo from "../../assets/logo/logo.png";
import Avatar from "../Avatar/Avatar";

import { useAuth } from "../../context/AuthContext";
import {
    getUserRole,
    USER_ROLES
} from "../../utils/constants";

import "./Navbar.css";

function Navbar() {
    const {
        user,
        token,
        loading,
        logout
    } = useAuth();

    const navigate = useNavigate();

    const [
        isUserMenuOpen,
        setIsUserMenuOpen
    ] = useState(false);

    const userMenuRef = useRef(null);
    const userMenuTriggerRef = useRef(null);

    const isAuthenticated =
        Boolean(token && user);

    const userRole =
        getUserRole(user);

    const isAdmin =
        userRole === USER_ROLES.ADMIN;

    const isUser =
        userRole === USER_ROLES.USUARIO;

    const userName = [
        user?.nombre,
        user?.apellidoPaterno
    ]
        .filter(Boolean)
        .join(" ")
        .trim()
        || user?.correo
        || "Usuario";

    useEffect(() => {
        if (!isUserMenuOpen) {
            return undefined;
        }

        const handlePointerDown = (event) => {
            if (
                !userMenuRef.current?.contains(
                    event.target
                )
            ) {
                setIsUserMenuOpen(false);
            }
        };

        const handleKeyDown = (event) => {
            if (event.key === "Escape") {
                setIsUserMenuOpen(false);

                userMenuTriggerRef.current?.focus();
            }
        };

        document.addEventListener(
            "pointerdown",
            handlePointerDown
        );

        document.addEventListener(
            "keydown",
            handleKeyDown
        );

        return () => {
            document.removeEventListener(
                "pointerdown",
                handlePointerDown
            );

            document.removeEventListener(
                "keydown",
                handleKeyDown
            );
        };
    }, [isUserMenuOpen]);

    const closeUserMenu = () => {
        setIsUserMenuOpen(false);
    };

    const handleLogout = async () => {
        closeUserMenu();

        try {
            await logout();

            toast.success(
                "Sesión cerrada correctamente"
            );
        } catch {
            toast.error(
                "La sesión se cerró localmente, "
                + "pero el servidor no respondió"
            );
        } finally {
            navigate("/", {
                replace: true
            });
        }
    };

    return (
        <header className="navbar">
            <div className="logo">
                <Link to="/">
                    <img
                        src={logo}
                        alt="Huellitas Oaxaca"
                    />
                </Link>
            </div>

            <nav
                className="menu"
                aria-label="Navegación principal"
            >
                <Link to="/">
                    Inicio
                </Link>

                <Link to="/catalogo">
                    Mascotas
                </Link>

                <Link to="/adopcion">
                    Cómo adoptar
                </Link>

                <Link to="/historias">
                    Historias
                </Link>

                <Link to="/nosotros">
                    Nosotros
                </Link>
            </nav>

            <div className="navbar-actions">
                {loading ? (
                    <span
                        className={
                            "btn-login navbar-loading"
                        }
                        aria-hidden="true"
                    >
                        Cargando
                    </span>
                ) : isAuthenticated ? (
                    <div
                        ref={userMenuRef}
                        className="navbar-user"
                    >
                        <button
                            ref={userMenuTriggerRef}
                            type="button"
                            className={
                                "navbar-user-trigger"
                            }
                            aria-haspopup="menu"
                            aria-expanded={
                                isUserMenuOpen
                            }
                            aria-controls={
                                "navbar-user-menu"
                            }
                            onClick={() => {
                                setIsUserMenuOpen(
                                    (isOpen) => !isOpen
                                );
                            }}
                        >
                            <Avatar
                                user={user}
                                size={42}
                            />

                            <span
                                className={
                                    "navbar-user-name"
                                }
                            >
                                {userName}
                            </span>

                            <ChevronDown
                                className={
                                    isUserMenuOpen
                                        ? "navbar-user-chevron is-open"
                                        : "navbar-user-chevron"
                                }
                                size={18}
                                aria-hidden="true"
                            />
                        </button>

                        {isUserMenuOpen && (
                            <div
                                id="navbar-user-menu"
                                className={
                                    "navbar-user-menu"
                                }
                                role="menu"
                            >
                                {isAdmin && (
                                    <Link
                                        to="/admin"
                                        role="menuitem"
                                        onClick={
                                            closeUserMenu
                                        }
                                    >
                                        <LayoutDashboard
                                            size={17}
                                            aria-hidden="true"
                                        />

                                        Panel administrativo
                                    </Link>
                                )}

                                {isUser && (
                                    <Link
                                        to="/mis-solicitudes"
                                        role="menuitem"
                                        onClick={
                                            closeUserMenu
                                        }
                                    >
                                        Mis solicitudes
                                    </Link>
                                )}

                                <Link
                                    to="/perfil"
                                    role="menuitem"
                                    onClick={
                                        closeUserMenu
                                    }
                                >
                                    Mi perfil
                                </Link>

                                <button
                                    type="button"
                                    role="menuitem"
                                    onClick={
                                        handleLogout
                                    }
                                >
                                    Cerrar sesión
                                </button>
                            </div>
                        )}
                    </div>
                ) : (
                    <Link
                        to="/login"
                        className="btn-login"
                    >
                        Iniciar sesión
                    </Link>
                )}
            </div>
        </header>
    );
}

export default Navbar;
