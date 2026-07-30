import { useEffect, useState } from "react";
import Loader from "../../components/Loader/Loader";
import api from "../../services/api";
import "./Donaciones.css";

const EMPTY_FORM = { monto: "", refugioId: "", metodoPago: "", mensaje: "" };
const METHODS = { EFECTIVO: "Efectivo", TRANSFERENCIA: "Transferencia", PAYPAL: "PayPal" };

function isValidRefugeList(data) {
    return Array.isArray(data) && data.every((refuge) => refuge && Number.isSafeInteger(refuge.id) && refuge.id > 0 && typeof refuge.nombre === "string");
}

function getErrorMessage(error) {
    if (!error.response) return "No fue posible conectar con el backend.";
    const message = error.response.data?.message;
    if ([400, 422].includes(error.response.status)) return message || "Revisa los datos de la donación.";
    if (error.response.status === 403) return "Sólo las cuentas de usuario pueden realizar donaciones.";
    if (error.response.status === 500) return "Ocurrió un error interno en el servidor.";
    return message || "No fue posible completar la donación.";
}

function Donaciones() {
    const [refuges, setRefuges] = useState([]);
    const [form, setForm] = useState(EMPTY_FORM);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [feedbackMessage, setFeedbackMessage] = useState("");
    const [retryVersion, setRetryVersion] = useState(0);

    useEffect(() => {
        const controller = new AbortController();
        const loadRefuges = async () => {
            setLoading(true);
            setErrorMessage("");
            try {
                const response = await api.get("/refugios/disponibles-para-donacion", { signal: controller.signal });
                if (!isValidRefugeList(response.data)) {
                    setErrorMessage("El backend devolvió una lista de refugios no compatible.");
                    return;
                }
                setRefuges(response.data);
            } catch (error) {
                if (error.code === "ERR_CANCELED" || controller.signal.aborted) return;
                setErrorMessage(getErrorMessage(error));
            } finally {
                if (!controller.signal.aborted) setLoading(false);
            }
        };
        loadRefuges();
        return () => controller.abort();
    }, [retryVersion]);

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (saving) return;
        if (!form.monto || Number(form.monto) < 10 || Number(form.monto) > 50000 || !form.refugioId || !form.metodoPago) {
            setFeedbackMessage("Indica un monto entre $10 y $50,000, un refugio y un método de pago.");
            return;
        }
        setSaving(true);
        setFeedbackMessage("");
        try {
            await api.post("/donaciones", { monto: Number(form.monto), refugioId: Number(form.refugioId), metodoPago: form.metodoPago, mensaje: form.mensaje.trim() || null });
            setFeedbackMessage("La donación quedó registrada para seguimiento. Este flujo no procesa pagos en línea.");
            setForm(EMPTY_FORM);
        } catch (error) {
            setFeedbackMessage(getErrorMessage(error));
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <section className="donations-page" aria-label="Cargando refugios"><h1 className="donations-hidden">Donaciones</h1><Loader /></section>;
    if (errorMessage) return <section className="donations-page donations-feedback" role="alert"><h1>No fue posible cargar los refugios</h1><p>{errorMessage}</p><button type="button" onClick={() => setRetryVersion((version) => version + 1)}>Reintentar</button></section>;
    return <section className="donations-page" aria-labelledby="donations-title"><h1 id="donations-title">Apoya a un refugio</h1><p>Registra una donación académica. El backend no procesa pagos financieros.</p>{refuges.length === 0 ? <p role="status">No hay refugios disponibles para donación.</p> : <form onSubmit={handleSubmit} noValidate><label htmlFor="donation-amount">Monto</label><input id="donation-amount" type="number" min="10" max="50000" step="0.01" value={form.monto} onChange={(event) => setForm((current) => ({ ...current, monto: event.target.value }))} /><label htmlFor="donation-refuge">Refugio</label><select id="donation-refuge" value={form.refugioId} onChange={(event) => setForm((current) => ({ ...current, refugioId: event.target.value }))}><option value="">Selecciona un refugio</option>{refuges.map((refuge) => <option key={refuge.id} value={refuge.id}>{refuge.nombre}</option>)}</select><label htmlFor="donation-method">Método de pago</label><select id="donation-method" value={form.metodoPago} onChange={(event) => setForm((current) => ({ ...current, metodoPago: event.target.value }))}><option value="">Selecciona un método</option>{Object.entries(METHODS).map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select><label htmlFor="donation-message">Mensaje (opcional)</label><textarea id="donation-message" maxLength="500" rows="4" value={form.mensaje} onChange={(event) => setForm((current) => ({ ...current, mensaje: event.target.value }))} /><button type="submit" disabled={saving}>{saving ? "Registrando..." : "Registrar donación"}</button>{feedbackMessage && <p role="status">{feedbackMessage}</p>}</form>}</section>;
}

export default Donaciones;
