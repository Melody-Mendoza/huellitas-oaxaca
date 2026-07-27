import "./DatosPersonales.css";

function Paso2Domicilio({
    anterior,
    siguiente,
    formulario = {},
    actualizarFormulario,
    errores = {}
}) {

    return (

        <section className="datos-personales">

            <h2>Domicilio</h2>

            <div className="grid-formulario">

                <div className="campo">

                    <label>Calle y número</label>

                    <input
                        type="text"
                        name="calle"
                        value={formulario.calle || ""}
                        onChange={actualizarFormulario}
                        placeholder="Ej. Av. Juárez #120"
                    />

                    {errores.calle && (
                        <small className="mensaje-error">
                            {errores.calle}
                        </small>
                    )}

                </div>

                <div className="campo">

                    <label>Colonia</label>

                    <input
                        type="text"
                        name="colonia"
                        value={formulario.colonia || ""}
                        onChange={actualizarFormulario}
                        placeholder="Centro"
                    />

                    {errores.colonia && (
                        <small className="mensaje-error">
                            {errores.colonia}
                        </small>
                    )}

                </div>

                <div className="campo">

                    <label>Ciudad</label>

                    <input
                        type="text"
                        name="ciudad"
                        value={formulario.ciudad || ""}
                        onChange={actualizarFormulario}
                        placeholder="Oaxaca de Juárez"
                    />

                    {errores.ciudad && (
                        <small className="mensaje-error">
                            {errores.ciudad}
                        </small>
                    )}

                </div>

                <div className="campo">

                    <label>Código Postal</label>

                    <input
                        type="text"
                        name="codigoPostal"
                        value={formulario.codigoPostal || ""}
                        onChange={actualizarFormulario}
                        placeholder="68000"
                    />

                    {errores.codigoPostal && (
                        <small className="mensaje-error">
                            {errores.codigoPostal}
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
                    onClick={siguiente}
                >
                    Siguiente →
                </button>

            </div>

        </section>

    );

}

export default Paso2Domicilio;