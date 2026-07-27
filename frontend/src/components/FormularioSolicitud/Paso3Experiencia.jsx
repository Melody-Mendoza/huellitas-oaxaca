import "./DatosPersonales.css";

function Paso3Experiencia({
    anterior,
    siguiente,
    formulario = {},
    actualizarFormulario,
    errores = {}
}) {

    return (

        <section className="datos-personales">

            <h2>Experiencia con mascotas</h2>

            <div className="grid-formulario">

                <div className="campo">

                    <label>¿Has tenido mascotas?</label>

                    <select
                        name="tuvoMascotas"
                        value={formulario.tuvoMascotas || ""}
                        onChange={actualizarFormulario}
                    >
                        <option value="">Selecciona una opción</option>
                        <option value="Sí">Sí</option>
                        <option value="No">No</option>
                    </select>

                    {errores.tuvoMascotas && (
                        <small className="mensaje-error">
                            {errores.tuvoMascotas}
                        </small>
                    )}

                </div>

                <div className="campo">

                    <label>¿Actualmente tienes mascotas?</label>

                    <select
                        name="tieneMascotas"
                        value={formulario.tieneMascotas || ""}
                        onChange={actualizarFormulario}
                    >
                        <option value="">Selecciona una opción</option>
                        <option value="Sí">Sí</option>
                        <option value="No">No</option>
                    </select>

                    {errores.tieneMascotas && (
                        <small className="mensaje-error">
                            {errores.tieneMascotas}
                        </small>
                    )}

                </div>

                <div className="campo campo-completo">

                    <label>
                        Cuéntanos brevemente tu experiencia
                    </label>

                    <textarea
                        name="experiencia"
                        value={formulario.experiencia || ""}
                        onChange={actualizarFormulario}
                        rows="5"
                        placeholder="Ej. He tenido perros desde pequeño y conozco sus cuidados..."
                    />

                    {errores.experiencia && (
                        <small className="mensaje-error">
                            {errores.experiencia}
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

export default Paso3Experiencia;