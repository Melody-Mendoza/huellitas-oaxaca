import { Link } from "react-router-dom";
import logo from "../../assets/logo/logo.png";
import "./Navbar.css";

function Navbar() {
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

            <Link to="/login" className="btn-login">
                Iniciar sesión
            </Link>
        </header>
    );
}

export default Navbar;