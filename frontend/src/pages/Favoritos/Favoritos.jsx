import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import Loader from "../../components/Loader/Loader";
import Pagination from "../../components/Pagination/Pagination";
import api from "../../services/api";
import { resolveMediaUrl } from "../../utils/media";
import "./Favoritos.css";

const EMPTY_PAGE = { content: [], number: 0, totalPages: 0, totalElements: 0, first: true, last: true };
const LABELS = { PERRO: "Perro", GATO: "Gato", MACHO: "Macho", HEMBRA: "Hembra", PEQUENO: "Pequeño", MEDIANO: "Mediano", GRANDE: "Grande", DISPONIBLE: "Disponible", EN_PROCESO: "En proceso", ADOPTADO: "Adoptado" };

function isValidPage(data) {
    return Boolean(data && Array.isArray(data.content) && Number.isInteger(data.number) && Number.isInteger(data.totalPages) && Number.isInteger(data.totalElements) && typeof data.first === "boolean" && typeof data.last === "boolean");
}

function Favoritos() {
    const [page, setPage] = useState(0);
    const [result, setResult] = useState(EMPTY_PAGE);
    const [loading, setLoading] = useState(true);
    const [errorMessage, setErrorMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);
    const [removingId, setRemovingId] = useState(null);
    useEffect(() => {
        const controller = new AbortController();
        const loadFavorites = async () => {
            setLoading(true);
            setErrorMessage("");
            try {
                const response = await api.get("/favoritos", { params: { page, size: 12 }, signal: controller.signal });
                if (!isValidPage(response.data)) { setErrorMessage("Recibimos una respuesta inesperada. Intenta de nuevo."); return; }
                setResult(response.data);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) return;
                setErrorMessage(error.response?.data?.message || "No fue posible cargar tus favoritos.");
            } finally {
                if (!controller.signal.aborted) setLoading(false);
            }
        };
        loadFavorites();
        return () => controller.abort();
    }, [page, retryVersion]);
    const removeFavorite = async (mascotaId) => {
        if (removingId !== null) return;
        setRemovingId(mascotaId);
        try {
            await api.delete(`/favoritos/${mascotaId}`);
            setResult((current) => ({ ...current, content: current.content.filter((favorite) => favorite.mascotaId !== mascotaId), totalElements: Math.max(0, current.totalElements - 1) }));
        } catch (error) {
            setErrorMessage(error.response?.data?.message || "No fue posible quitar el favorito.");
        } finally {
            setRemovingId(null);
        }
    };
    if (loading) return <section className="favorites-page" aria-label="Cargando favoritos"><h1 className="favorites-hidden">Mis favoritos</h1><Loader /></section>;
    if (errorMessage) return <section className="favorites-page favorites-feedback" role="alert"><h1>No fue posible cargar tus favoritos</h1><p>{errorMessage}</p><button type="button" onClick={() => setRetryVersion((version) => version + 1)}>Reintentar</button></section>;
    return <section className="favorites-page" aria-labelledby="favorites-title"><h1 id="favorites-title">Mis favoritos</h1>{result.content.length === 0 ? <div role="status"><p>Aún no tienes mascotas favoritas.</p><Link to="/catalogo">Explorar catálogo</Link></div> : <><div className="favorites-grid">{result.content.map((favorite) => <article key={favorite.mascotaId}>{resolveMediaUrl(favorite.imagenPrincipal) ? <img src={resolveMediaUrl(favorite.imagenPrincipal)} alt={`Imagen de ${favorite.nombre}`} /> : <div className="favorites-placeholder">Sin imagen</div>}<div><span>{LABELS[favorite.estado] || favorite.estado}</span><h2>{favorite.nombre || "Mascota"}</h2><p>{LABELS[favorite.especie] || "Especie no disponible"} · {favorite.nombreRefugio || "Refugio no disponible"}</p><Link to={`/mascota/${favorite.mascotaId}`}>Ver detalle</Link><button type="button" disabled={removingId !== null} onClick={() => removeFavorite(favorite.mascotaId)}>{removingId === favorite.mascotaId ? "Quitando..." : "Quitar favorito"}</button></div></article>)}</div><Pagination currentPage={result.number} totalPages={result.totalPages} first={result.first} last={result.last} onPageChange={setPage} ariaLabel="Paginación de favoritos" /></>}</section>;
}

export default Favoritos;
