import "./DatosPersonales.css";

function DatosPersonales({
    siguiente,
    formulario = {},
    actualizarFormulario,
    errores = {}
}) {

    return (
        <section className="datos-personales">
            <h2>Datos personales</h2>
            <div className="grid-formulario">
                <div className="campo">
                    <label>Nombre completo</label>
                    <input
                        type="text"
                        name="nombre"
                        value={formulario.nombre || ""}
                        onChange={actualizarFormulario}
                        placeholder="Ej. Ana García"
                    />
                    {errores.nombre && (
                        <small className="mensaje-error">
                            {errores.nombre}
                        </small>
                    )}
                </div>
                <div className="campo">
                    <label>Edad</label>
                    <input
                        type="number"
                        name="edad"
                        value={formulario.edad || ""}
                        onChange={actualizarFormulario}
                        placeholder="Ej. 28"
                    />
                    {errores.edad && (
                        <small className="mensaje-error">
                            {errores.edad}
                        </small>
                    )}
                </div>
                <div className="campo">
                    <label>Correo electrónico</label>
                    <input
                        type="email"
                        name="correo"
                        value={formulario.correo || ""}
                        onChange={actualizarFormulario}
                        placeholder="ana@email.com"
                    />
                    {errores.correo && (
                        <small className="mensaje-error">
                            {errores.correo}
                        </small>
                    )}
                </div>
                <div className="campo">
                    <label>Teléfono</label>
                    <input
                        type="text"
                        name="telefono"
                        value={formulario.telefono || ""}
                        onChange={actualizarFormulario}
                        placeholder="10 dígitos"
                    />
                    {errores.telefono && (
                        <small className="mensaje-error">
                            {errores.telefono}
                        </small>
                    )}
                </div>
            </div>
            <div className="acciones">
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

export default DatosPersonales;