import { useEffect, useState } from "react";
import { Link, useOutletContext } from "react-router-dom";
import Loader from "../../components/Loader/Loader";
import Pagination from "../../components/Pagination/Pagination";
import api from "../../services/api";
import { resolveMediaUrl } from "../../utils/media";
import "./MascotasRefugio.css";

const PAGE_SIZE = 10;
const EMPTY_FILTERS = { nombre: "", especie: "", estado: "" };
const SPECIES = new Set(["PERRO", "GATO"]);
const SEXES = new Set(["MACHO", "HEMBRA"]);
const SIZES = new Set(["PEQUENO", "MEDIANO", "GRANDE"]);
const STATUSES = new Set(["DISPONIBLE", "EN_PROCESO", "ADOPTADO"]);
const LABELS = {
    PERRO: "Perro",
    GATO: "Gato",
    MACHO: "Macho",
    HEMBRA: "Hembra",
    PEQUENO: "Pequeño",
    MEDIANO: "Mediano",
    GRANDE: "Grande",
    DISPONIBLE: "Disponible",
    EN_PROCESO: "En proceso",
    ADOPTADO: "Adoptado"
};

function getErrorMessage(error) {
    if (!error.response) { return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."; }
    const message = error.response.data?.message;
    if (error.response.status === 401) { return "La sesión ya no es válida. Inicia sesión nuevamente."; }
    if (error.response.status === 403) { return message || "No tienes permiso para consultar estas mascotas."; }
    if (error.response.status === 404) { return message || "No se encontró el refugio solicitado."; }
    if (error.response.status === 400) { return message || "Los filtros enviados no son válidos."; }
    return error.response.status === 500
        ? "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."
        : "No fue posible cargar las mascotas.";
}

function isValidPet(pet) {
    return Boolean(
        pet
        && typeof pet === "object"
        && !Array.isArray(pet)
        && Number.isSafeInteger(pet.id)
        && pet.id > 0
        && typeof pet.nombre === "string"
        && SPECIES.has(pet.especie)
        && typeof pet.raza === "string"
        && SEXES.has(pet.sexo)
        && Number.isSafeInteger(pet.edad)
        && pet.edad >= 0
        && pet.edad <= 40
        && SIZES.has(pet.tamano)
        && STATUSES.has(pet.estado)
        && typeof pet.fechaIngreso === "string"
        && /^\d{4}-\d{2}-\d{2}$/.test(pet.fechaIngreso)
        && (pet.imagenPrincipal === null || typeof pet.imagenPrincipal === "string")
    );
}

function isValidPage(data) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && Array.isArray(data.content)
        && data.content.every(isValidPet)
        && new Set(data.content.map((pet) => pet.id)).size === data.content.length
        && Number.isSafeInteger(data.totalElements)
        && data.totalElements >= 0
        && Number.isSafeInteger(data.totalPages)
        && data.totalPages >= 0
        && Number.isSafeInteger(data.number)
        && data.number >= 0
        && typeof data.first === "boolean"
        && typeof data.last === "boolean"
    );
}

function MascotasRefugio() {
    const { selectedRefuge } = useOutletContext();
    const [draftFilters, setDraftFilters] = useState(EMPTY_FILTERS);
    const [filters, setFilters] = useState(EMPTY_FILTERS);
    const [page, setPage] = useState(0);
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);
    const hasFilters = Object.values(filters).some(Boolean);

    useEffect(() => {
        const controller = new AbortController();
        const loadPets = async () => {
            setLoading(true);
            setResult(null);
            setErrorMessage("");
            try {
                const params = { page, size: PAGE_SIZE };
                if (filters.nombre) { params.nombre = filters.nombre; }
                if (filters.especie) { params.especie = filters.especie; }
                if (filters.estado) { params.estado = filters.estado; }
                const response = await api.get(
                    `/refugios/${selectedRefuge.id}/mascotas`,
                    { params, signal: controller.signal }
                );
                if (!isValidPage(response.data)) {
                    setErrorMessage("Recibimos una respuesta inesperada. Intenta de nuevo.");
                    return;
                }
                setResult(response.data);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
                setErrorMessage(getErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) { setLoading(false); }
            }
        };
        loadPets();
        return () => controller.abort();
    }, [selectedRefuge.id, filters, page, retryVersion]);

    const applyFilters = (event) => {
        event.preventDefault();
        setPage(0);
        setFilters({ ...draftFilters, nombre: draftFilters.nombre.trim() });
    };
    const clearFilters = () => {
        setDraftFilters(EMPTY_FILTERS);
        setPage(0);
        setFilters(EMPTY_FILTERS);
    };

    return (
        <section className="shelter-pets" aria-labelledby="shelter-pets-title">
            <header className="shelter-pets-header">
                <div>
                    <p>Mascotas del refugio</p>
                    <h1 id="shelter-pets-title">Gestión de mascotas</h1>
                    <p>Consulta y actualiza las mascotas de {selectedRefuge.nombre}.</p>
                </div>
                <Link to="/refugio/mascotas/nueva">Publicar mascota</Link>
            </header>
            <form className="shelter-pets-filters" onSubmit={applyFilters}>
                <div>
                    <label htmlFor="pet-filter-name">Nombre</label>
                    <input id="pet-filter-name" value={draftFilters.nombre} maxLength="100" onChange={(event) => setDraftFilters((current) => ({ ...current, nombre: event.target.value }))} />
                </div>
                <div>
                    <label htmlFor="pet-filter-species">Especie</label>
                    <select id="pet-filter-species" value={draftFilters.especie} onChange={(event) => setDraftFilters((current) => ({ ...current, especie: event.target.value }))}>
                        <option value="">Todas</option>
                        <option value="PERRO">Perro</option>
                        <option value="GATO">Gato</option>
                    </select>
                </div>
                <div>
                    <label htmlFor="pet-filter-status">Estado</label>
                    <select id="pet-filter-status" value={draftFilters.estado} onChange={(event) => setDraftFilters((current) => ({ ...current, estado: event.target.value }))}>
                        <option value="">Todos</option>
                        <option value="DISPONIBLE">Disponible</option>
                        <option value="EN_PROCESO">En proceso</option>
                        <option value="ADOPTADO">Adoptado</option>
                    </select>
                </div>
                <button type="submit" disabled={loading}>Aplicar filtros</button>
                <button type="button" className="secondary" onClick={clearFilters} disabled={loading || (!hasFilters && !Object.values(draftFilters).some(Boolean))}>Limpiar</button>
            </form>
            {loading && <div className="shelter-pets-loading"><Loader /></div>}
            {!loading && errorMessage && (
                <div className="shelter-pets-feedback" role="alert">
                    <h2>No fue posible cargar las mascotas</h2>
                    <p>{errorMessage}</p>
                    <button type="button" onClick={() => setRetryVersion((version) => version + 1)}>Reintentar</button>
                </div>
            )}
            {!loading && !errorMessage && result?.content.length === 0 && (
                <div className="shelter-pets-feedback" role="status">
                    <h2>{hasFilters ? "Sin resultados" : "Aún no hay mascotas publicadas"}</h2>
                    <p>{hasFilters ? "No hay mascotas que coincidan con los filtros aplicados." : "Publica la primera mascota de este refugio para comenzar."}</p>
                    {hasFilters ? <button type="button" onClick={clearFilters}>Limpiar filtros</button> : <Link to="/refugio/mascotas/nueva">Publicar mascota</Link>}
                </div>
            )}
            {!loading && !errorMessage && result?.content.length > 0 && (
                <>
                    <div className="shelter-pets-summary">
                        <p><strong>{result.totalElements}</strong> {result.totalElements === 1 ? "mascota" : "mascotas"}</p>
                        <span>Página {result.number + 1} de {result.totalPages}</span>
                    </div>
                    <div className="shelter-pets-grid">
                        {result.content.map((pet) => (
                            <article className="shelter-pet-card" key={pet.id}>
                                {resolveMediaUrl(pet.imagenPrincipal) ? <img src={resolveMediaUrl(pet.imagenPrincipal)} alt={`Imagen de ${pet.nombre}`} /> : <div className="shelter-pet-placeholder" aria-hidden="true">Sin imagen</div>}
                                <div className="shelter-pet-content">
                                    <div>
                                        <span className={`shelter-pet-status status-${pet.estado.toLowerCase()}`}>{LABELS[pet.estado]}</span>
                                        <h2>{pet.nombre}</h2>
                                    </div>
                                    <dl>
                                        <div><dt>Especie</dt><dd>{LABELS[pet.especie]}</dd></div>
                                        <div><dt>Raza</dt><dd>{pet.raza}</dd></div>
                                        <div><dt>Sexo</dt><dd>{LABELS[pet.sexo]}</dd></div>
                                        <div><dt>Edad</dt><dd>{pet.edad} {pet.edad === 1 ? "año" : "años"}</dd></div>
                                        <div><dt>Tamaño</dt><dd>{LABELS[pet.tamano]}</dd></div>
                                        <div><dt>Ingreso</dt><dd>{pet.fechaIngreso}</dd></div>
                                    </dl>
                                    <Link to={`/refugio/mascotas/${pet.id}/editar`}>Editar</Link>
                                </div>
                            </article>
                        ))}
                    </div>
                    <Pagination currentPage={result.number} totalPages={result.totalPages} first={result.first} last={result.last} onPageChange={setPage} disabled={loading} ariaLabel="Paginación de mascotas del refugio" />
                </>
            )}
        </section>
    );
}

export default MascotasRefugio;
