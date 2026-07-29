import "./Loader.css";

function Loader() {
    return (
        <div
            className="loader-container"
            role="status"
            aria-live="polite"
            aria-label="Cargando contenido"
        >
            <div
                className="loader"
                aria-hidden="true"
            />

            <p>Cargando...</p>
        </div>
    );
}

export default Loader;