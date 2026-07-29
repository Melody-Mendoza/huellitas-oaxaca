import { Link, useNavigate } from "react-router-dom";
import logo from "../../assets/logo/logo.png";

import toast from "react-hot-toast";
import { useAuth } from "../../context/AuthContext";

import "./Navbar.css";

function Navbar() {
    const { user, token, loading, logout } = useAuth();
    const navigate = useNavigate();
    const isAuthenticated = Boolean(token && user);

    const handleLogout = async () => {
        try {
            await logout();
            toast.success("Sesión cerrada correctamente");
        } catch {
            toast.error(
                "La sesión se cerró localmente, pero el servidor no respondió"
            );
        } finally {
            navigate("/", { replace: true });
        }
    };

    return (
        <header className="navbar">
            <div className="logo">
                <Link to="/">
                    <img src={logo} alt="Huellitas Oaxaca" />
                </Link>
            </div>

            <nav className="menu">
                <Link to="/">Inicio</Link>
                <Link to="/catalogo">Mascotas</Link>
                <Link to="/adopcion">Cómo adoptar</Link>
                <Link to="/historias">Historias</Link>
                <Link to="/nosotros">Nosotros</Link>
            </nav>

            
            {loading ? (
                <span
                    className="btn-login navbar-loading"
                    aria-hidden="true"
                    style={{ visibility: "hidden" }}
                >
                    Cargando
                </span>
            ) : isAuthenticated ? (
                <div
                    style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "0.75rem"
                    }}
                >
                    <Link to="/perfil" className="btn-login">
                        Mi perfil
                    </Link>

                    <button
                        type="button"
                        className="btn-login"
                        onClick={handleLogout}
                        style={{
                            border: "none",
                            cursor: "pointer",
                            font: "inherit"
                        }}
                    >
                        Cerrar sesión
                    </button>
                </div>
            ) : (
                <Link to="/login" className="btn-login">
                    Iniciar sesión
                </Link>
            )}
        </header>
    );
}

export default Navbar;