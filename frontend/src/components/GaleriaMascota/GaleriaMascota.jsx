import "./GaleriaMascota.css";
import { useState } from "react";

function GaleriaMascota({ imagenes }) {

    const [imagenPrincipal, setImagenPrincipal] = useState(imagenes[0]);

    return (

        <div className="galeria">

            <img
                src={imagenPrincipal}
                alt="Mascota"
                className="imagen-principal"
            />

            <div className="miniaturas">

                {

                    imagenes.map((imagen, index) => (

                        <img

                            key={index}

                            src={imagen}

                            alt="Miniatura"

                            className={imagenPrincipal === imagen ? "active" : ""}

                            onClick={() => setImagenPrincipal(imagen)}

                        />

                    ))

                }

            </div>

        </div>

    );

}

export default GaleriaMascota;