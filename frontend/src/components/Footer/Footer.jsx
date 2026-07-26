import "./Footer.css";
import { Link } from "react-router-dom";

function Footer() {
    return (
        <footer className="footer">
            <div className="footer-container">
                <div className="footer-brand">
                    <h2>🐾 Huellitas Oaxaca</h2>
                    <p>
                        Conectamos mascotas con familias responsables.
                    </p>
                </div>
                <div className="footer-links">
                    <h3>Enlaces</h3>
                    <Link to="/">Inicio</Link>
                    <Link to="/catalogo">Catálogo</Link>
                    <Link to="/donaciones">Donaciones</Link>
                </div>
                <div className="footer-contact">
                    <h3>Contacto</h3>
                    <p>Oaxaca de Juárez</p>
                    <p>contacto@huellitas.com</p>
                    <p>951 000 0000</p>
                </div>
            </div>
            <div className="footer-copy">
                © 2026 Huellitas Oaxaca. Todos los derechos reservados.
            </div>
        </footer>
    );
}

export default Footer;