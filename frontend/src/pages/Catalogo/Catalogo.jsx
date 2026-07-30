import "./Catalogo.css";

import { useEffect, useState } from "react";

import CardMascota from "../../components/CardMascota/CardMascota";
import Loader from "../../components/Loader/Loader";
import Pagination from "../../components/Pagination/Pagination";
import SearchBar from "../../components/SearchBar/SearchBar";
import Sidebar from "../../components/Sidebar/Sidebar";
import api from "../../services/api";

const PAGE_SIZE = 12;
const SEARCH_DEBOUNCE_MS = 350;

const INITIAL_FILTERS = {
    especie: "",
    sexo: "",
    tamano: "",
    edad: ""
};

const EMPTY_PAGE = {
    content: [],
    number: 0,
    totalPages: 0,
    totalElements: 0,
    first: true,
    last: true
};

function isNonNegativeInteger(value) {
    return Number.isInteger(value) && value >= 0;
}

function isValidPageResponse(data) {
    return Boolean(
        data
        && Array.isArray(data.content)
        && isNonNegativeInteger(data.number)
        && isNonNegativeInteger(data.totalPages)
        && isNonNegativeInteger(data.totalElements)
        && typeof data.first === "boolean"
        && typeof data.last === "boolean"
    );
}

function getValidAge(value) {
    if (value === "" || !/^\d+$/.test(value)) {
        return null;
    }

    const age = Number(value);

    return Number.isInteger(age) && age >= 0 && age <= 40
        ? age
        : null;
}

function getRequestErrorMessage(error) {
    if (!error.response) {
        return "No fue posible conectar con el backend.";
    }

    const backendMessage = error.response.data?.message;

    switch (error.response.status) {
        case 400:
            return backendMessage
                || "Los filtros o parámetros enviados no son válidos.";
        case 401:
            return "La sesión no es válida.";
        case 403:
            return "No tienes permiso para consultar el catálogo.";
        case 404:
            return backendMessage
                || "No se encontró el recurso solicitado.";
        case 409:
            return backendMessage
                || "La consulta generó un conflicto.";
        case 422:
            return backendMessage
                || "No fue posible procesar la consulta.";
        case 500:
            return "Ocurrió un error interno en el servidor.";
        default:
            return backendMessage
                || "No fue posible cargar el catálogo.";
    }
}

function Catalogo() {
    const [search, setSearch] = useState("");
    const [debouncedSearch, setDebouncedSearch] = useState("");
    const [filters, setFilters] = useState(INITIAL_FILTERS);
    const [page, setPage] = useState(0);
    const [pageData, setPageData] = useState(EMPTY_PAGE);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);

    useEffect(() => {
        const timeoutId = window.setTimeout(() => {
            setDebouncedSearch(search.trim());
        }, SEARCH_DEBOUNCE_MS);

        return () => {
            window.clearTimeout(timeoutId);
        };
    }, [search]);

    useEffect(() => {
        const controller = new AbortController();

        const loadCatalog = async () => {
            setLoading(true);
            setErrorMessage("");

            const params = {
                page,
                size: PAGE_SIZE,
                sort: "nombre,asc"
            };

            if (debouncedSearch) {
                params.nombre = debouncedSearch;
            }

            if (filters.especie) {
                params.especie = filters.especie;
            }

            if (filters.sexo) {
                params.sexo = filters.sexo;
            }

            if (filters.tamano) {
                params.tamano = filters.tamano;
            }

            const validAge = getValidAge(filters.edad);

            if (validAge !== null) {
                params.edad = validAge;
            }

            try {
                const response = await api.get("/mascotas", {
                    params,
                    signal: controller.signal
                });

                if (!isValidPageResponse(response.data)) {
                    setPageData(EMPTY_PAGE);
                    setErrorMessage(
                        "El backend devolvió una estructura de paginación no compatible."
                    );
                    return;
                }

                if (
                    response.data.totalPages > 0
                    && response.data.number >= response.data.totalPages
                ) {
                    setPage(response.data.totalPages - 1);
                    return;
                }

                setPageData(response.data);
            } catch (error) {
                if (
                    error.code === "ERR_CANCELED"
                    || controller.signal.aborted
                ) {
                    return;
                }

                setPageData(EMPTY_PAGE);
                setErrorMessage(getRequestErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) {
                    setLoading(false);
                }
            }
        };

        loadCatalog();

        return () => {
            controller.abort();
        };
    }, [
        debouncedSearch,
        filters.edad,
        filters.especie,
        filters.sexo,
        filters.tamano,
        page,
        retryVersion
    ]);

    const handleSearchChange = (event) => {
        setSearch(event.target.value);
        setPage(0);
    };

    const handleFilterChange = (name, value) => {
        setFilters((currentFilters) => ({
            ...currentFilters,
            [name]: value
        }));
        setPage(0);
    };

    return (
        <section
            className="catalogo"
            aria-labelledby="catalogo-title"
            aria-busy={loading}
        >
            <div className="catalogo-layout">
                <Sidebar
                    values={filters}
                    onChange={handleFilterChange}
                />

                <div className="catalogo-content">
                    <div className="catalogo-top">
                        <div>
                            <h1 id="catalogo-title">
                                Catálogo de mascotas
                            </h1>

                            <p aria-live="polite">
                                <strong className="catalogo-total">
                                    {pageData.totalElements}
                                </strong>{" "}
                                {pageData.totalElements === 1
                                    ? "mascota esperando un hogar"
                                    : "mascotas esperando un hogar"}
                            </p>
                        </div>

                        <SearchBar
                            value={search}
                            onChange={handleSearchChange}
                        />
                    </div>

                    {loading ? (
                        <Loader />
                    ) : errorMessage ? (
                        <div
                            className="catalogo-feedback catalogo-error"
                            role="alert"
                        >
                            <h2>No fue posible cargar el catálogo</h2>
                            <p>{errorMessage}</p>

                            <button
                                type="button"
                                onClick={() => {
                                    setRetryVersion(
                                        (currentVersion) =>
                                            currentVersion + 1
                                    );
                                }}
                            >
                                Reintentar
                            </button>
                        </div>
                    ) : pageData.content.length === 0 ? (
                        <div
                            className="catalogo-feedback catalogo-empty"
                            role="status"
                        >
                            <h2>No se encontraron mascotas</h2>
                            <p>
                                Prueba con otro nombre o cambia los
                                filtros seleccionados.
                            </p>
                        </div>
                    ) : (
                        <>
                            <div className="catalogo-grid">
                                {pageData.content.map((mascota) => (
                                    <CardMascota
                                        key={mascota.id}
                                        mascota={mascota}
                                    />
                                ))}
                            </div>

                            <Pagination
                                currentPage={pageData.number}
                                totalPages={pageData.totalPages}
                                first={pageData.first}
                                last={pageData.last}
                                onPageChange={setPage}
                                disabled={loading}
                            />
                        </>
                    )}
                </div>
            </div>
        </section>
    );
}

export default Catalogo;
