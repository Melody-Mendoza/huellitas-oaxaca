import "./Sidebar.css";

const SIZE_OPTIONS = [
    { value: "", label: "Todos" },
    { value: "PEQUENO", label: "Pequeño" },
    { value: "MEDIANO", label: "Mediano" },
    { value: "GRANDE", label: "Grande" }
];

function Sidebar({
    values = {
        especie: "",
        sexo: "",
        tamano: "",
        edad: ""
    },
    onChange = () => {},
    disabled = false
}) {
    return (
        <aside
            className="sidebar"
            aria-label="Filtros del catálogo"
        >
            <h3>Filtros</h3>

            <fieldset className="filter-group">
                <legend>Especie</legend>

                <label>
                    <input
                        type="radio"
                        name="especie"
                        value=""
                        checked={values.especie === ""}
                        onChange={(event) => {
                            onChange("especie", event.target.value);
                        }}
                        disabled={disabled}
                    />
                    Todas
                </label>

                <label>
                    <input
                        type="radio"
                        name="especie"
                        value="PERRO"
                        checked={values.especie === "PERRO"}
                        onChange={(event) => {
                            onChange("especie", event.target.value);
                        }}
                        disabled={disabled}
                    />
                    Perros
                </label>

                <label>
                    <input
                        type="radio"
                        name="especie"
                        value="GATO"
                        checked={values.especie === "GATO"}
                        onChange={(event) => {
                            onChange("especie", event.target.value);
                        }}
                        disabled={disabled}
                    />
                    Gatos
                </label>
            </fieldset>

            <div className="filter-group">
                <label
                    className="filter-heading"
                    htmlFor="catalogo-edad"
                >
                    Edad exacta
                </label>

                <input
                    id="catalogo-edad"
                    className="age-filter"
                    type="number"
                    min="0"
                    max="40"
                    step="1"
                    inputMode="numeric"
                    placeholder="0 a 40 años"
                    value={values.edad}
                    onChange={(event) => {
                        onChange("edad", event.target.value);
                    }}
                    disabled={disabled}
                />
            </div>

            <fieldset className="filter-group">
                <legend>Tamaño</legend>

                <div className="chips">
                    {SIZE_OPTIONS.map((option) => (
                        <button
                            key={option.value || "TODOS"}
                            type="button"
                            className={
                                values.tamano === option.value
                                    ? "active"
                                    : ""
                            }
                            aria-pressed={
                                values.tamano === option.value
                            }
                            onClick={() => {
                                onChange("tamano", option.value);
                            }}
                            disabled={disabled}
                        >
                            {option.label}
                        </button>
                    ))}
                </div>
            </fieldset>

            <fieldset className="filter-group">
                <legend>Sexo</legend>

                <label>
                    <input
                        type="radio"
                        name="sexo"
                        value=""
                        checked={values.sexo === ""}
                        onChange={(event) => {
                            onChange("sexo", event.target.value);
                        }}
                        disabled={disabled}
                    />
                    Ambos
                </label>

                <label>
                    <input
                        type="radio"
                        name="sexo"
                        value="MACHO"
                        checked={values.sexo === "MACHO"}
                        onChange={(event) => {
                            onChange("sexo", event.target.value);
                        }}
                        disabled={disabled}
                    />
                    Macho
                </label>

                <label>
                    <input
                        type="radio"
                        name="sexo"
                        value="HEMBRA"
                        checked={values.sexo === "HEMBRA"}
                        onChange={(event) => {
                            onChange("sexo", event.target.value);
                        }}
                        disabled={disabled}
                    />
                    Hembra
                </label>
            </fieldset>
        </aside>
    );
}

export default Sidebar;
