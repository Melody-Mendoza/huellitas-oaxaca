import { useEffect, useRef, useState } from "react";
import { useOutletContext } from "react-router-dom";
import toast from "react-hot-toast";
import Loader from "../../components/Loader/Loader";
import api from "../../services/api";
import "./PerfilRefugio.css";

const FIELD_NAMES = ["nombre", "descripcion", "direccion", "telefono", "correo"];
const EMPTY_FORM = { nombre: "", descripcion: "", direccion: "", telefono: "", correo: "" };

function isNullableString(value) {
    return value === null || typeof value === "string";
}

function isValidProfile(data, refugeId) {
    return Boolean(
        data
        && typeof data === "object"
        && !Array.isArray(data)
        && data.id === refugeId
        && typeof data.nombre === "string"
        && data.nombre.trim()
        && isNullableString(data.descripcion)
        && isNullableString(data.direccion)
        && isNullableString(data.telefono)
        && isNullableString(data.correo)
        && typeof data.activo === "boolean"
    );
}

function toFormData(profile) {
    return { nombre: profile.nombre ?? "", descripcion: profile.descripcion ?? "", direccion: profile.direccion ?? "", telefono: profile.telefono ?? "", correo: profile.correo ?? "" };
}

function getErrorMessage(error) {
    if (!error.response) { return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde."; }
    const backendMessage = error.response.data?.message;
    switch (error.response.status) {
        case 400:
            return backendMessage || "Revisa los datos ingresados.";
        case 401:
            return "La sesión ya no es válida. Inicia sesión nuevamente.";
        case 403:
            return backendMessage || "No tienes permiso para modificar este refugio.";
        case 404:
            return backendMessage || "No se encontró el refugio solicitado.";
        case 422:
            return backendMessage || "No fue posible actualizar el perfil.";
        case 500:
            return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde.";
        default:
            return "No fue posible completar la operación.";
    }
}

function validateForm(formData) {
    const errors = {};
    if (!formData.nombre.trim()) {
        errors.nombre = "El nombre del refugio no puede estar vacío.";
    } else if (formData.nombre.trim().length > 150) {
        errors.nombre = "El nombre no puede superar los 150 caracteres.";
    }
    if (formData.descripcion.trim().length > 1000) { errors.descripcion = "La descripción no puede superar los 1000 caracteres."; }
    if (formData.direccion.trim().length > 255) { errors.direccion = "La dirección no puede superar los 255 caracteres."; }
    if (formData.telefono.trim() && !/^\d{10}$/.test(formData.telefono.trim())) { errors.telefono = "El teléfono debe contener 10 dígitos."; }
    const email = formData.correo.trim();
    if (email.length > 150) {
        errors.correo = "El correo no puede superar los 150 caracteres.";
    } else if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        errors.correo = "El correo no tiene un formato válido.";
    }
    return errors;
}

function PerfilRefugio() {
    const { selectedRefuge, updateSelectedRefuge } = useOutletContext();
    const [profile, setProfile] = useState(null);
    const [formData, setFormData] = useState(EMPTY_FORM);
    const [initialData, setInitialData] = useState(EMPTY_FORM);
    const [fieldErrors, setFieldErrors] = useState({});
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [formMessage, setFormMessage] = useState("");
    const [submitError, setSubmitError] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);
    const submissionLockRef = useRef(false);

    useEffect(() => {
        const controller = new AbortController();
        const loadProfile = async () => {
            setLoading(true);
            setProfile(null);
            setErrorMessage("");
            setFieldErrors({});
            setFormMessage("");
            setSubmitError("");
            try {
                const response = await api.get(`/refugios/${selectedRefuge.id}/perfil`, { signal: controller.signal });
                if (!isValidProfile(response.data, selectedRefuge.id)) {
                    setErrorMessage("Recibimos una respuesta inesperada. Intenta de nuevo.");
                    return;
                }
                const nextFormData = toFormData(response.data);
                setProfile(response.data);
                setFormData(nextFormData);
                setInitialData(nextFormData);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) { return; }
                setErrorMessage(getErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) { setLoading(false); }
            }
        };
        loadProfile();
        return () => controller.abort();
    }, [selectedRefuge.id, retryVersion]);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setFormData((currentData) => ({ ...currentData, [name]: value }));
        setFieldErrors((currentErrors) => ({ ...currentErrors, [name]: undefined }));
        setFormMessage("");
        setSubmitError("");
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (submissionLockRef.current || saving || !profile) { return; }
        const clientErrors = validateForm(formData);
        if (Object.keys(clientErrors).length > 0) {
            setFieldErrors(clientErrors);
            return;
        }
        const normalizedData = Object.fromEntries(FIELD_NAMES.map((field) => [field, formData[field].trim()]));
        const payload = Object.fromEntries(FIELD_NAMES.filter((field) => normalizedData[field] !== initialData[field]).map((field) => [field, normalizedData[field]]));
        if (Object.keys(payload).length === 0) {
            setFormMessage("No hay cambios pendientes por guardar.");
            return;
        }
        submissionLockRef.current = true;
        setSaving(true);
        setFieldErrors({});
        setFormMessage("");
        setSubmitError("");
        try {
            const response = await api.patch(`/refugios/${selectedRefuge.id}/perfil`, payload);
            if (!isValidProfile(response.data, selectedRefuge.id)) {
                setSubmitError("Recibimos una respuesta inesperada. Intenta de nuevo.");
                return;
            }
            const nextFormData = toFormData(response.data);
            setProfile(response.data);
            setFormData(nextFormData);
            setInitialData(nextFormData);
            updateSelectedRefuge(response.data);
            toast.success("Perfil del refugio actualizado correctamente.");
        } catch (error) {
            const validationErrors = error.response?.data?.validationErrors;
            if (error.response?.status === 400 && validationErrors && typeof validationErrors === "object" && !Array.isArray(validationErrors)) {
                setFieldErrors(validationErrors);
            }
            setSubmitError(getErrorMessage(error));
        } finally {
            submissionLockRef.current = false;
            setSaving(false);
        }
    };

    if (loading) {
        return (
            <section className="refuge-profile-loading" aria-labelledby="refuge-profile-loading-title">
                <h1 id="refuge-profile-loading-title" className="refuge-profile-visually-hidden">Perfil del refugio</h1>
                <Loader />
            </section>
        );
    }
    if (errorMessage || !profile) {
        return (
            <section className="refuge-profile-feedback" aria-labelledby="refuge-profile-error-title" role="alert">
                <h1 id="refuge-profile-error-title">No fue posible cargar el perfil</h1>
                <p>{errorMessage || "No fue posible consultar el refugio."}</p>
                <button type="button" onClick={() => setRetryVersion((version) => version + 1)}>Reintentar</button>
            </section>
        );
    }

    return (
        <section className="refuge-profile" aria-labelledby="refuge-profile-title">
            <header className="refuge-profile-header">
                <div>
                    <p>Información pública</p>
                    <h1 id="refuge-profile-title">Perfil del refugio</h1>
                    <p>Actualiza los datos visibles de {profile.nombre}.</p>
                </div>
                <span className="refuge-profile-status">{profile.activo ? "Refugio activo" : "Refugio inactivo"}</span>
            </header>
            <form className="refuge-profile-form" onSubmit={handleSubmit} noValidate>
                <div className="refuge-profile-field refuge-profile-field-wide">
                    <label htmlFor="refuge-name">Nombre</label>
                    <input id="refuge-name" name="nombre" value={formData.nombre} maxLength="150" onChange={handleChange} aria-invalid={Boolean(fieldErrors.nombre)} />
                    {fieldErrors.nombre && <p role="alert">{fieldErrors.nombre}</p>}
                </div>
                <div className="refuge-profile-field refuge-profile-field-wide">
                    <label htmlFor="refuge-description">Descripción</label>
                    <textarea id="refuge-description" name="descripcion" rows="6" value={formData.descripcion} maxLength="1000" onChange={handleChange} aria-invalid={Boolean(fieldErrors.descripcion)} />
                    <span>{formData.descripcion.length} / 1000</span>
                    {fieldErrors.descripcion && <p role="alert">{fieldErrors.descripcion}</p>}
                </div>
                <div className="refuge-profile-field refuge-profile-field-wide">
                    <label htmlFor="refuge-address">Dirección</label>
                    <input id="refuge-address" name="direccion" value={formData.direccion} maxLength="255" onChange={handleChange} aria-invalid={Boolean(fieldErrors.direccion)} />
                    {fieldErrors.direccion && <p role="alert">{fieldErrors.direccion}</p>}
                </div>
                <div className="refuge-profile-field">
                    <label htmlFor="refuge-phone">Teléfono</label>
                    <input id="refuge-phone" name="telefono" type="tel" value={formData.telefono} inputMode="numeric" onChange={handleChange} aria-invalid={Boolean(fieldErrors.telefono)} />
                    {fieldErrors.telefono && <p role="alert">{fieldErrors.telefono}</p>}
                </div>
                <div className="refuge-profile-field">
                    <label htmlFor="refuge-email">Correo electrónico</label>
                    <input id="refuge-email" name="correo" type="email" value={formData.correo} maxLength="150" onChange={handleChange} aria-invalid={Boolean(fieldErrors.correo)} />
                    {fieldErrors.correo && <p role="alert">{fieldErrors.correo}</p>}
                </div>
                {formMessage && (
                    <p className="refuge-profile-message" role="status">{formMessage}</p>
                )}
                {submitError && (
                    <p className="refuge-profile-error" role="alert">{submitError}</p>
                )}
                <button type="submit" disabled={saving}>{saving ? "Guardando..." : "Guardar cambios"}</button>
            </form>
        </section>
    );
}

export default PerfilRefugio;