import { useEffect, useRef, useState } from "react";
import { Link, useOutletContext, useParams } from "react-router-dom";
import toast from "react-hot-toast";
import Loader from "../../components/Loader/Loader";
import api from "../../services/api";
import { resolveMediaUrl } from "../../utils/media";
import "./EditarMascota.css";

const SPECIES = new Set(["PERRO", "GATO"]);
const SEXES = new Set(["MACHO", "HEMBRA"]);
const SIZES = new Set(["PEQUENO", "MEDIANO", "GRANDE"]);
const STATUSES = new Set(["DISPONIBLE", "EN_PROCESO", "ADOPTADO"]);
const STATUS_LABELS = {
    DISPONIBLE: "Disponible",
    EN_PROCESO: "En proceso",
    ADOPTADO: "Adoptado"
};

function parsePetId(value) {
    if (!/^[1-9]\d*$/.test(value ?? "")) { return null; }
    const id = Number(value);
    return Number.isSafeInteger(id) ? id : null;
}

function isValidDetail(data, petId, refugeId) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && data.id === petId
        && typeof data.nombre === "string"
        && data.nombre.trim()
        && SPECIES.has(data.especie)
        && typeof data.raza === "string"
        && data.raza.trim()
        && SEXES.has(data.sexo)
        && Number.isSafeInteger(data.edad)
        && data.edad >= 0
        && data.edad <= 40
        && (data.peso === null || (typeof data.peso === "number" && data.peso >= 0.1 && data.peso <= 999.99))
        && SIZES.has(data.tamano)
        && typeof data.descripcion === "string"
        && data.descripcion.trim()
        && STATUSES.has(data.estado)
        && typeof data.fechaIngreso === "string"
        && /^\d{4}-\d{2}-\d{2}$/.test(data.fechaIngreso)
        && (data.imagenPrincipal === null || typeof data.imagenPrincipal === "string")
        && Array.isArray(data.imagenesAdicionales)
        && data.imagenesAdicionales.every((image) => typeof image === "string")
        && data.refugioId === refugeId
        && typeof data.nombreRefugio === "string"
    );
}

function toForm(detail) {
    return {
        nombre: detail.nombre,
        especie: detail.especie,
        raza: detail.raza,
        sexo: detail.sexo,
        edad: String(detail.edad),
        peso: detail.peso === null ? "" : String(detail.peso),
        tamano: detail.tamano,
        descripcion: detail.descripcion
    };
}

function validateForm(form) {
    const errors = {};
    if (!form.nombre.trim()) {
        errors.nombre = "El nombre de la mascota no puede estar vacío.";
    } else if (form.nombre.trim().length > 100) {
        errors.nombre = "El nombre no puede superar los 100 caracteres.";
    }
    if (!form.raza.trim()) {
        errors.raza = "La raza no puede estar vacía.";
    } else if (form.raza.trim().length > 100) {
        errors.raza = "La raza no puede superar los 100 caracteres.";
    }
    const age = Number(form.edad);
    if (!Number.isInteger(age) || age < 0 || age > 40) {
        errors.edad = "La edad debe ser un número entero entre 0 y 40.";
    }
    if (form.peso && (!/^\d{1,3}(\.\d{1,2})?$/.test(form.peso) || Number(form.peso) < 0.1 || Number(form.peso) > 999.99)) {
        errors.peso = "El peso debe estar entre 0.1 y 999.99, con máximo 2 decimales.";
    }
    if (!form.descripcion.trim()) {
        errors.descripcion = "La descripción no puede estar vacía.";
    } else if (form.descripcion.trim().length > 2000) {
        errors.descripcion = "La descripción no puede superar los 2000 caracteres.";
    }
    return errors;
}

function createPatch(form, detail) {
    const values = {
        nombre: form.nombre.trim(),
        especie: form.especie,
        raza: form.raza.trim(),
        sexo: form.sexo,
        edad: Number(form.edad),
        peso: form.peso === "" ? null : Number(form.peso),
        tamano: form.tamano,
        descripcion: form.descripcion.trim()
    };
    return Object.fromEntries(
        Object.entries(values).filter(([field, value]) => value !== detail[field])
    );
}

function getErrorMessage(error, operation) {
    if (!error.response) { return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."; }
    const message = error.response.data?.message;
    if (error.response.status === 401) { return "La sesión ya no es válida. Inicia sesión nuevamente."; }
    if (error.response.status === 403) { return message || "No tienes permiso para administrar esta mascota."; }
    if (error.response.status === 404) { return message || "No se encontró la mascota en este refugio."; }
    if (error.response.status === 400) { return message || "Revisa los datos enviados."; }
    if (error.response.status === 422) { return message || "El cambio solicitado no está permitido."; }
    if (error.response.status === 500) { return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."; }
    return `No fue posible ${operation}.`;
}

function isValidImageList(data) {
    return Array.isArray(data) && data.every((image) => image
        && Number.isSafeInteger(image.id) && image.id > 0
        && typeof image.url === "string" && typeof image.principal === "boolean");
}

function EditarMascota() {
    const { selectedRefuge } = useOutletContext();
    const { mascotaId } = useParams();
    const petId = parsePetId(mascotaId);
    const [detail, setDetail] = useState(null);
    const [form, setForm] = useState(null);
    const [loading, setLoading] = useState(petId !== null);
    const [loadError, setLoadError] = useState("");
    const [fieldErrors, setFieldErrors] = useState({});
    const [operationError, setOperationError] = useState("");
    const [feedbackMessage, setFeedbackMessage] = useState("");
    const [images, setImages] = useState([]);
    const [imagesLoading, setImagesLoading] = useState(true);
    const [imagesError, setImagesError] = useState("");
    const [selectedFile, setSelectedFile] = useState(null);
    const [imageSaving, setImageSaving] = useState(false);
    const [saving, setSaving] = useState(false);
    const [retryVersion, setRetryVersion] = useState(0);
    const mutationLock = useRef(false);
    const mutationController = useRef(null);

    useEffect(() => {
        if (petId === null) { return undefined; }
        const controller = new AbortController();
        const loadPet = async () => {
            setLoading(true);
            setDetail(null);
            setForm(null);
            setLoadError("");
            setOperationError("");
            setFeedbackMessage("");
            try {
                const response = await api.get(
                    `/refugios/${selectedRefuge.id}/mascotas/${petId}`,
                    { signal: controller.signal }
                );
                if (!isValidDetail(response.data, petId, selectedRefuge.id)) {
                    setLoadError("Recibimos una respuesta inesperada. Intenta de nuevo.");
                    return;
                }
                setDetail(response.data);
                setForm(toForm(response.data));
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
                setLoadError(getErrorMessage(error, "cargar la mascota"));
            } finally {
                if (!controller.signal.aborted) { setLoading(false); }
            }
        };
        loadPet();
        return () => {
            controller.abort();
            mutationController.current?.abort();
        };
    }, [petId, selectedRefuge.id, retryVersion]);

    useEffect(() => {
        if (petId === null) { return undefined; }
        const controller = new AbortController();
        const loadImages = async () => {
            setImagesLoading(true);
            setImagesError("");
            try {
                const response = await api.get(`/refugios/${selectedRefuge.id}/mascotas/${petId}/imagenes`, { signal: controller.signal });
                if (!isValidImageList(response.data)) {
                    setImagesError("Recibimos una respuesta inesperada. Intenta de nuevo.");
                    return;
                }
                setImages(response.data);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
                setImagesError(getErrorMessage(error, "cargar las imágenes"));
            } finally {
                if (!controller.signal.aborted) { setImagesLoading(false); }
            }
        };
        loadImages();
        return () => controller.abort();
    }, [petId, selectedRefuge.id, retryVersion]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
        setFieldErrors((current) => ({ ...current, [name]: undefined }));
        setOperationError("");
        setFeedbackMessage("");
    };
    const startMutation = () => {
        const controller = new AbortController();
        mutationController.current = controller;
        mutationLock.current = true;
        setSaving(true);
        setOperationError("");
        setFeedbackMessage("");
        return controller;
    };
    const finishMutation = (controller) => {
        if (!controller.signal.aborted) {
            mutationLock.current = false;
            setSaving(false);
        }
    };
    const handleSubmit = async (event) => {
        event.preventDefault();
        if (mutationLock.current || saving) { return; }
        const errors = validateForm(form);
        if (Object.keys(errors).length) {
            setFieldErrors(errors);
            return;
        }
        const payload = createPatch(form, detail);
        if (Object.keys(payload).length === 0) {
            setFeedbackMessage("No hay cambios por guardar.");
            return;
        }
        const controller = startMutation();
        setFieldErrors({});
        try {
            const response = await api.patch(
                `/refugios/${selectedRefuge.id}/mascotas/${petId}`,
                payload,
                { signal: controller.signal }
            );
            if (!isValidDetail(response.data, petId, selectedRefuge.id)) {
                setOperationError("Recibimos una respuesta inesperada. Intenta de nuevo.");
                return;
            }
            setDetail(response.data);
            setForm(toForm(response.data));
            setFeedbackMessage("Los datos de la mascota se actualizaron correctamente.");
            toast.success("Mascota actualizada correctamente.");
        } catch (error) {
            if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
            const validationErrors = error.response?.data?.validationErrors;
            if (error.response?.status === 400 && validationErrors && typeof validationErrors === "object" && !Array.isArray(validationErrors)) {
                setFieldErrors(validationErrors);
            }
            setOperationError(getErrorMessage(error, "actualizar la mascota"));
        } finally {
            finishMutation(controller);
        }
    };
    const handleStatusChange = async () => {
        if (mutationLock.current || saving || detail.estado === "ADOPTADO") { return; }
        const nextStatus = detail.estado === "DISPONIBLE" ? "EN_PROCESO" : "DISPONIBLE";
        if (nextStatus === detail.estado) { return; }
        const controller = startMutation();
        try {
            const response = await api.patch(
                `/refugios/${selectedRefuge.id}/mascotas/${petId}/estado`,
                { estado: nextStatus },
                { signal: controller.signal }
            );
            if (!isValidDetail(response.data, petId, selectedRefuge.id)) {
                setOperationError("Recibimos una respuesta inesperada. Intenta de nuevo.");
                return;
            }
            setDetail(response.data);
            setFeedbackMessage(`Estado actualizado a ${STATUS_LABELS[response.data.estado].toLowerCase()}.`);
            toast.success("Estado actualizado correctamente.");
        } catch (error) {
            if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
            setOperationError(getErrorMessage(error, "actualizar el estado"));
        } finally {
            finishMutation(controller);
        }
    };
    const handleImageUpload = async (event) => {
        event.preventDefault();
        if (!selectedFile || imageSaving) { return; }
        if (!new Set(["image/jpeg", "image/png"]).has(selectedFile.type) || selectedFile.size > 5 * 1024 * 1024) {
            setImagesError("Selecciona una imagen JPG o PNG de máximo 5 MiB.");
            return;
        }
        const formData = new FormData();
        formData.append("imagen", selectedFile);
        setImageSaving(true);
        setImagesError("");
        try {
            const response = await api.post(`/refugios/${selectedRefuge.id}/mascotas/${petId}/imagenes`, formData);
            if (!isValidImageList([response.data])) {
                setImagesError("Recibimos una respuesta inesperada. Intenta de nuevo.");
                return;
            }
            setImages((current) => [...current, response.data]);
            setSelectedFile(null);
            event.target.reset();
            toast.success("Imagen subida correctamente.");
        } catch (error) {
            setImagesError(getErrorMessage(error, "subir la imagen"));
        } finally {
            setImageSaving(false);
        }
    };
    const handleSetPrincipal = async (imageId) => {
        try {
            const response = await api.patch(`/refugios/${selectedRefuge.id}/mascotas/${petId}/imagenes/${imageId}/principal`);
            if (!isValidImageList([response.data])) { setImagesError("Recibimos una respuesta inesperada. Intenta de nuevo."); return; }
            setImages((current) => current.map((image) => ({ ...image, principal: image.id === imageId })));
        } catch (error) { setImagesError(getErrorMessage(error, "seleccionar la imagen principal")); }
    };
    const handleDeleteImage = async (imageId) => {
        try {
            await api.delete(`/refugios/${selectedRefuge.id}/mascotas/${petId}/imagenes/${imageId}`);
            setImages((current) => current.filter((image) => image.id !== imageId));
            toast.success("Imagen eliminada correctamente.");
        } catch (error) { setImagesError(getErrorMessage(error, "eliminar la imagen")); }
    };

    if (petId === null) {
        return (
            <section className="edit-pet-feedback" role="alert">
                <h1>Identificador de mascota no válido</h1>
                <p>La ruta debe contener un identificador entero positivo y seguro.</p>
                <Link to="/refugio/mascotas">Volver al listado</Link>
            </section>
        );
    }
    if (loading) {
        return <section className="edit-pet-loading" aria-label="Cargando mascota"><Loader /></section>;
    }
    if (loadError || !detail || !form) {
        return (
            <section className="edit-pet-feedback" role="alert">
                <h1>No fue posible cargar la mascota</h1>
                <p>{loadError || "No fue posible consultar el detalle."}</p>
                <div>
                    <button type="button" onClick={() => setRetryVersion((version) => version + 1)}>Reintentar</button>
                    <Link to="/refugio/mascotas">Volver al listado</Link>
                </div>
            </section>
        );
    }

    const nextStatus = detail.estado === "DISPONIBLE" ? "EN_PROCESO" : "DISPONIBLE";
    return (
        <section className="edit-pet" aria-labelledby="edit-pet-title">
            <header className="edit-pet-header">
                <div>
                    <p>Gestión de mascotas</p>
                    <h1 id="edit-pet-title">Editar a {detail.nombre}</h1>
                    <p>Publicada el {detail.fechaIngreso} en {selectedRefuge.nombre}.</p>
                </div>
                <Link to="/refugio/mascotas">Volver al listado</Link>
            </header>
            <aside className="edit-pet-status" aria-labelledby="edit-pet-status-title">
                <div>
                    <h2 id="edit-pet-status-title">Estado de publicación</h2>
                    <p><strong>{STATUS_LABELS[detail.estado]}</strong></p>
                    {detail.estado === "ADOPTADO" && <p>El estado adoptado es informativo y no puede cambiarse manualmente.</p>}
                </div>
                {detail.estado !== "ADOPTADO" && (
                    <button type="button" onClick={handleStatusChange} disabled={saving}>
                        {saving ? "Guardando..." : `Cambiar a ${STATUS_LABELS[nextStatus].toLowerCase()}`}
                    </button>
                )}
            </aside>
            <form className="edit-pet-form" onSubmit={handleSubmit} noValidate>
                <div className="edit-pet-field">
                    <label htmlFor="edit-pet-name">Nombre</label>
                    <input id="edit-pet-name" name="nombre" value={form.nombre} maxLength="100" onChange={handleChange} aria-invalid={Boolean(fieldErrors.nombre)} />
                    {fieldErrors.nombre && <p role="alert">{fieldErrors.nombre}</p>}
                </div>
                <div className="edit-pet-field">
                    <label htmlFor="edit-pet-species">Especie</label>
                    <select id="edit-pet-species" name="especie" value={form.especie} onChange={handleChange}>
                        <option value="PERRO">Perro</option>
                        <option value="GATO">Gato</option>
                    </select>
                </div>
                <div className="edit-pet-field">
                    <label htmlFor="edit-pet-breed">Raza</label>
                    <input id="edit-pet-breed" name="raza" value={form.raza} maxLength="100" onChange={handleChange} aria-invalid={Boolean(fieldErrors.raza)} />
                    {fieldErrors.raza && <p role="alert">{fieldErrors.raza}</p>}
                </div>
                <div className="edit-pet-field">
                    <label htmlFor="edit-pet-sex">Sexo</label>
                    <select id="edit-pet-sex" name="sexo" value={form.sexo} onChange={handleChange}>
                        <option value="MACHO">Macho</option>
                        <option value="HEMBRA">Hembra</option>
                    </select>
                </div>
                <div className="edit-pet-field">
                    <label htmlFor="edit-pet-age">Edad en años</label>
                    <input id="edit-pet-age" name="edad" type="number" min="0" max="40" step="1" value={form.edad} onChange={handleChange} aria-invalid={Boolean(fieldErrors.edad)} />
                    {fieldErrors.edad && <p role="alert">{fieldErrors.edad}</p>}
                </div>
                <div className="edit-pet-field">
                    <label htmlFor="edit-pet-weight">Peso en kg <span>(opcional)</span></label>
                    <input id="edit-pet-weight" name="peso" type="number" min="0.1" max="999.99" step="0.01" value={form.peso} onChange={handleChange} aria-invalid={Boolean(fieldErrors.peso)} />
                    {fieldErrors.peso && <p role="alert">{fieldErrors.peso}</p>}
                </div>
                <div className="edit-pet-field">
                    <label htmlFor="edit-pet-size">Tamaño</label>
                    <select id="edit-pet-size" name="tamano" value={form.tamano} onChange={handleChange}>
                        <option value="PEQUENO">Pequeño</option>
                        <option value="MEDIANO">Mediano</option>
                        <option value="GRANDE">Grande</option>
                    </select>
                </div>
                <div className="edit-pet-field edit-pet-wide">
                    <label htmlFor="edit-pet-description">Descripción</label>
                    <textarea id="edit-pet-description" name="descripcion" rows="7" value={form.descripcion} maxLength="2000" onChange={handleChange} aria-invalid={Boolean(fieldErrors.descripcion)} />
                    <span>{form.descripcion.length} / 2000</span>
                    {fieldErrors.descripcion && <p role="alert">{fieldErrors.descripcion}</p>}
                </div>
                {operationError && <p className="edit-pet-error" role="alert">{operationError}</p>}
                {feedbackMessage && <p className="edit-pet-success" role="status">{feedbackMessage}</p>}
                <button type="submit" disabled={saving}>{saving ? "Guardando..." : "Guardar cambios"}</button>
            </form>
            <section className="edit-pet-images" aria-labelledby="edit-pet-images-title">
                <h2 id="edit-pet-images-title">Imágenes</h2>
                <p>JPG o PNG, máximo 5 MiB por archivo y hasta 8 imágenes.</p>
                {imagesLoading && <p role="status">Cargando imágenes...</p>}
                {imagesError && <p role="alert">{imagesError}</p>}
                {!imagesLoading && !imagesError && images.length === 0 && <p role="status">Esta mascota todavía no tiene imágenes.</p>}
                <div className="edit-pet-image-list">
                    {images.map((image) => <article key={image.id}><img src={resolveMediaUrl(image.url)} alt={`Imagen de ${detail.nombre}`} /><div><span>{image.principal ? "Principal" : "Adicional"}</span>{!image.principal && <button type="button" onClick={() => handleSetPrincipal(image.id)}>Hacer principal</button>}<button type="button" onClick={() => handleDeleteImage(image.id)}>Eliminar</button></div></article>)}
                </div>
                {images.length < 8 && <form onSubmit={handleImageUpload}><label htmlFor="pet-image-upload">Agregar imagen</label><input id="pet-image-upload" type="file" accept="image/jpeg,image/png" onChange={(event) => setSelectedFile(event.target.files?.[0] ?? null)} /><button type="submit" disabled={!selectedFile || imageSaving}>{imageSaving ? "Subiendo..." : "Subir imagen"}</button></form>}
            </section>
        </section>
    );
}

export default EditarMascota;
