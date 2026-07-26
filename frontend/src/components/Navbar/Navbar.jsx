import "./Navbar.css";
import { Link } from "react-router-dom";
function Navbar(){
    return(
        <header className="navbar">
            <div className="logo">
                🐾 Huellitas Oaxaca
            </div>
            <nav>
                <Link to="/">Inicio</Link>
                <Link to="/catalogo">Catálogo</Link>
                <Link to="/donaciones">Donaciones</Link>
                <Link to="/login">Login</Link>
                <Link to="/register">Registro</Link>
            </nav>
        </header>
    )
}

export default Navbar;