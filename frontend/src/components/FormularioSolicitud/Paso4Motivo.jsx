import "./DatosPersonales.css";

function Paso4Motivo({
    anterior,
    formulario = {},
    actualizarFormulario,
    enviarSolicitud,
    errores = {}
}) {

    return (
        <section className="datos-personales">
            <h2>Motivo de adopción</h2>
            <div className="grid-formulario">
                <div className="campo campo-completo">
                    <label>
                        ¿Por qué deseas adoptar esta mascota?
                    </label>
                    <textarea
                        name="motivo"
                        value={formulario.motivo || ""}
                        onChange={actualizarFormulario}
                        rows="7"
                        placeholder="Cuéntanos por qué deseas adoptar y cómo será la vida de la mascota contigo."
                    />
                    {errores.motivo && (
                        <small className="mensaje-error">
                            {errores.motivo}
                        </small>
                    )}
                </div>
            </div>
            <div className="acciones">
                <button
                    type="button"
                    className="btn-secundario"
                    onClick={anterior}
                >
                    ← Anterior
                </button>

                <button
                    type="button"
                    onClick={enviarSolicitud}
                >
                    Enviar solicitud
                </button>
            </div>
        </section>
    );

}

export default Paso4Motivo;