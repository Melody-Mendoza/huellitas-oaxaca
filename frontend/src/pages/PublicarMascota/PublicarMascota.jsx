import { useEffect, useRef, useState } from "react";
import { Link, useNavigate, useOutletContext } from "react-router-dom";
import toast from "react-hot-toast";
import api from "../../services/api";
import "./PublicarMascota.css";

const EMPTY_FORM = {
    nombre: "",
    especie: "",
    raza: "",
    sexo: "",
    edad: "",
    peso: "",
    tamano: "",
    descripcion: ""
};

function validateForm(form) {
    const errors = {};
    if (!form.nombre.trim()) {
        errors.nombre = "El nombre de la mascota es obligatorio.";
    } else if (form.nombre.trim().length > 100) {
        errors.nombre = "El nombre no puede superar los 100 caracteres.";
    }
    if (!form.especie) { errors.especie = "La especie es obligatoria."; }
    if (!form.raza.trim()) {
        errors.raza = "La raza es obligatoria.";
    } else if (form.raza.trim().length > 100) {
        errors.raza = "La raza no puede superar los 100 caracteres.";
    }
    if (!form.sexo) { errors.sexo = "El sexo es obligatorio."; }
    const age = Number(form.edad);
    if (form.edad === "") {
        errors.edad = "La edad es obligatoria.";
    } else if (!Number.isInteger(age) || age < 0 || age > 40) {
        errors.edad = "La edad debe ser un número entero entre 0 y 40.";
    }
    if (form.peso && (!/^\d{1,3}(\.\d{1,2})?$/.test(form.peso) || Number(form.peso) < 0.1 || Number(form.peso) > 999.99)) {
        errors.peso = "El peso debe estar entre 0.1 y 999.99, con máximo 2 decimales.";
    }
    if (!form.tamano) { errors.tamano = "El tamaño es obligatorio."; }
    if (!form.descripcion.trim()) {
        errors.descripcion = "La descripción es obligatoria.";
    } else if (form.descripcion.trim().length > 2000) {
        errors.descripcion = "La descripción no puede superar los 2000 caracteres.";
    }
    return errors;
}

function getErrorMessage(error) {
    if (!error.response) { return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."; }
    const message = error.response.data?.message;
    if (error.response.status === 401) { return "La sesión ya no es válida. Inicia sesión nuevamente."; }
    if (error.response.status === 403) { return message || "No tienes permiso para publicar mascotas en este refugio."; }
    if (error.response.status === 404) { return message || "No se encontró el refugio solicitado."; }
    if (error.response.status === 400) { return message || "Revisa los datos ingresados."; }
    if (error.response.status === 422) { return message || "No fue posible publicar la mascota."; }
    return error.response.status === 500
        ? "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."
        : "No fue posible completar la operación.";
}

function PublicarMascota() {
    const { selectedRefuge } = useOutletContext();
    const navigate = useNavigate();
    const [form, setForm] = useState(EMPTY_FORM);
    const [fieldErrors, setFieldErrors] = useState({});
    const [submitError, setSubmitError] = useState("");
    const [saving, setSaving] = useState(false);
    const submissionLock = useRef(false);
    const requestController = useRef(null);

    useEffect(() => () => requestController.current?.abort(), []);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
        setFieldErrors((current) => ({ ...current, [name]: undefined }));
        setSubmitError("");
    };
    const handleSubmit = async (event) => {
        event.preventDefault();
        if (submissionLock.current || saving) { return; }
        const errors = validateForm(form);
        if (Object.keys(errors).length) {
            setFieldErrors(errors);
            return;
        }
        const payload = {
            nombre: form.nombre.trim(),
            especie: form.especie,
            raza: form.raza.trim(),
            sexo: form.sexo,
            edad: Number(form.edad),
            peso: form.peso === "" ? null : Number(form.peso),
            tamano: form.tamano,
            descripcion: form.descripcion.trim()
        };
        const controller = new AbortController();
        requestController.current = controller;
        submissionLock.current = true;
        setSaving(true);
        setFieldErrors({});
        setSubmitError("");
        try {
            const response = await api.post(
                `/refugios/${selectedRefuge.id}/mascotas`,
                payload,
                { signal: controller.signal }
            );
            if (!Number.isSafeInteger(response.data?.id) || response.data.id < 1) {
                setSubmitError("No pudimos obtener el identificador de la mascota creada. Intenta de nuevo.");
                return;
            }
            toast.success("Mascota publicada correctamente.");
            navigate(`/refugio/mascotas/${response.data.id}/editar`, { replace: true });
        } catch (error) {
            if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
            const validationErrors = error.response?.data?.validationErrors;
            if (error.response?.status === 400 && validationErrors && typeof validationErrors === "object" && !Array.isArray(validationErrors)) {
                setFieldErrors(validationErrors);
            }
            setSubmitError(getErrorMessage(error));
        } finally {
            if (!controller.signal.aborted) {
                submissionLock.current = false;
                setSaving(false);
            }
        }
    };

    return (
        <section className="publish-pet" aria-labelledby="publish-pet-title">
            <header className="publish-pet-header">
                <div>
                    <p>Nueva publicación</p>
                    <h1 id="publish-pet-title">Publicar mascota</h1>
                    <p>Registra una mascota en {selectedRefuge.nombre}. El estado inicial será disponible.</p>
                </div>
                <Link to="/refugio/mascotas">Volver al listado</Link>
            </header>
            <form className="publish-pet-form" onSubmit={handleSubmit} noValidate>
                <div className="publish-pet-field">
                    <label htmlFor="publish-pet-name">Nombre</label>
                    <input id="publish-pet-name" name="nombre" value={form.nombre} maxLength="100" onChange={handleChange} aria-invalid={Boolean(fieldErrors.nombre)} />
                    {fieldErrors.nombre && <p role="alert">{fieldErrors.nombre}</p>}
                </div>
                <div className="publish-pet-field">
                    <label htmlFor="publish-pet-species">Especie</label>
                    <select id="publish-pet-species" name="especie" value={form.especie} onChange={handleChange} aria-invalid={Boolean(fieldErrors.especie)}>
                        <option value="">Selecciona</option>
                        <option value="PERRO">Perro</option>
                        <option value="GATO">Gato</option>
                    </select>
                    {fieldErrors.especie && <p role="alert">{fieldErrors.especie}</p>}
                </div>
                <div className="publish-pet-field">
                    <label htmlFor="publish-pet-breed">Raza</label>
                    <input id="publish-pet-breed" name="raza" value={form.raza} maxLength="100" onChange={handleChange} aria-invalid={Boolean(fieldErrors.raza)} />
                    {fieldErrors.raza && <p role="alert">{fieldErrors.raza}</p>}
                </div>
                <div className="publish-pet-field">
                    <label htmlFor="publish-pet-sex">Sexo</label>
                    <select id="publish-pet-sex" name="sexo" value={form.sexo} onChange={handleChange} aria-invalid={Boolean(fieldErrors.sexo)}>
                        <option value="">Selecciona</option>
                        <option value="MACHO">Macho</option>
                        <option value="HEMBRA">Hembra</option>
                    </select>
                    {fieldErrors.sexo && <p role="alert">{fieldErrors.sexo}</p>}
                </div>
                <div className="publish-pet-field">
                    <label htmlFor="publish-pet-age">Edad en años</label>
                    <input id="publish-pet-age" name="edad" type="number" min="0" max="40" step="1" value={form.edad} onChange={handleChange} aria-invalid={Boolean(fieldErrors.edad)} />
                    {fieldErrors.edad && <p role="alert">{fieldErrors.edad}</p>}
                </div>
                <div className="publish-pet-field">
                    <label htmlFor="publish-pet-weight">Peso en kg <span>(opcional)</span></label>
                    <input id="publish-pet-weight" name="peso" type="number" min="0.1" max="999.99" step="0.01" value={form.peso} onChange={handleChange} aria-invalid={Boolean(fieldErrors.peso)} />
                    {fieldErrors.peso && <p role="alert">{fieldErrors.peso}</p>}
                </div>
                <div className="publish-pet-field">
                    <label htmlFor="publish-pet-size">Tamaño</label>
                    <select id="publish-pet-size" name="tamano" value={form.tamano} onChange={handleChange} aria-invalid={Boolean(fieldErrors.tamano)}>
                        <option value="">Selecciona</option>
                        <option value="PEQUENO">Pequeño</option>
                        <option value="MEDIANO">Mediano</option>
                        <option value="GRANDE">Grande</option>
                    </select>
                    {fieldErrors.tamano && <p role="alert">{fieldErrors.tamano}</p>}
                </div>
                <div className="publish-pet-field publish-pet-wide">
                    <label htmlFor="publish-pet-description">Descripción</label>
                    <textarea id="publish-pet-description" name="descripcion" rows="7" value={form.descripcion} maxLength="2000" onChange={handleChange} aria-invalid={Boolean(fieldErrors.descripcion)} />
                    <span>{form.descripcion.length} / 2000</span>
                    {fieldErrors.descripcion && <p role="alert">{fieldErrors.descripcion}</p>}
                </div>
                {submitError && <p className="publish-pet-error" role="alert">{submitError}</p>}
                <button type="submit" disabled={saving}>{saving ? "Publicando..." : "Publicar mascota"}</button>
            </form>
        </section>
    );
}

export default PublicarMascota;
