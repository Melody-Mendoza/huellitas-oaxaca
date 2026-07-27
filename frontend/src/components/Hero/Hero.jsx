import "./Hero.css";

import { Link } from "react-router-dom";
import dogHero from "../../assets/images/pantalla.jpg";
import { Search } from "lucide-react";

function Hero() {
    return (
        <section className="hero">

            <div className="hero-info">

                <h1>
                    Encuentra a tu nuevo compañero de vida
                </h1>

                <p>
                    Conectamos corazones en Oaxaca a través de un proceso
                    de adopción responsable, ético y transparente.
                </p>

                <div className="hero-actions">

                    <Link to="/catalogo" className="hero-btn">
                        Ver mascotas
                    </Link>

                    <div className="search">

                        <Search size={18} />

                        <input
                            type="text"
                            placeholder="Buscar por nombre o raza..."
                        />

                    </div>

                </div>

            </div>

            <div className="hero-image">

                <img
                    src={dogHero}
                    alt="Perro"
                />

            </div>

        </section>
    );
}

export default Hero;