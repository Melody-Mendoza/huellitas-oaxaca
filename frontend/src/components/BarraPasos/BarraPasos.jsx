import "./BarraPasos.css";

const pasos = [
    "Datos personales",
    "Domicilio",
    "Experiencia",
    "Motivo",
];

function BarraPasos({ pasoActual }) {

    return (
        <div className="barra-pasos">
            {pasos.map((paso, index) => (
                <div
                    className="paso-item"
                    key={index}
                >
                    {index !== 0 &&
                        <div
                            className={
                                index < pasoActual
                                    ? "linea activa"
                                    : "linea"
                            }
                        />

                    }

                    <div
                        className={
                            index + 1 <= pasoActual
                                ? "circulo activo"
                                : "circulo"
                        }
                    >
                        {index + 1}
                    </div>
                    <span>{paso}</span>
                </div>
            ))}
        </div>
    );
}

export default BarraPasos;