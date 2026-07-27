import { Link } from "react-router-dom";
import "./Footer.css";

function Footer() {
    return (
        <footer className="footer">

            <div className="footer-container">

                <div className="footer-logo">
                    <h3>Huellitas Oaxaca</h3>
                </div>

                <div className="footer-links">

                    <Link to="/privacidad">
                        Privacidad
                    </Link>

                    <Link to="/terminos">
                        Términos
                    </Link>

                    <Link to="/contacto">
                        Contacto
                    </Link>

                    <Link to="/donaciones">
                        Donaciones
                    </Link>

                </div>

                <div className="footer-copy">

                    © 2026 Huellitas Oaxaca. Adopción Responsable.

                </div>

            </div>

        </footer>
    );
}

export default Footer;