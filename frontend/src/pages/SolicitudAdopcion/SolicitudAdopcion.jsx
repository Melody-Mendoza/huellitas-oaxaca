import "./SolicitudAdopcion.css";
import { useState } from "react";

import BarraPasos from "../../components/BarraPasos/BarraPasos";
import DatosPersonales from "../../components/FormularioSolicitud/DatosPersonales";
import Paso2Domicilio from "../../components/FormularioSolicitud/Paso2Domicilio";
import Paso3Experiencia from "../../components/FormularioSolicitud/Paso3Experiencia";
import Paso4Motivo from "../../components/FormularioSolicitud/Paso4Motivo";

function SolicitudAdopcion() {

    const [pasoActual, setPasoActual] = useState(1);

    const [errores, setErrores] = useState({});

    const [solicitudEnviada, setSolicitudEnviada] = useState(false);

    const [formulario, setFormulario] = useState({

        nombre: "",
        edad: "",
        correo: "",
        telefono: "",

        calle: "",
        colonia: "",
        ciudad: "",
        codigoPostal: "",

        tuvoMascotas: "",
        tieneMascotas: "",
        experiencia: "",

        motivo: ""

    });

    const actualizarFormulario = (e) => {
        const { name, value } = e.target;
        setFormulario((prev) => ({
            ...prev,
            [name]: value
        }));

    };

    const validarPaso = () => {
        let nuevosErrores = {};
        if (pasoActual === 1) {
            if (!formulario.nombre.trim()) {
                nuevosErrores.nombre = "El nombre es obligatorio.";
            }

            if (!formulario.edad) {
                nuevosErrores.edad = "La edad es obligatoria.";
            } else if (Number(formulario.edad) < 18) {
                nuevosErrores.edad = "Debes ser mayor de edad.";
            }

            if (!formulario.correo.trim()) {
                nuevosErrores.correo = "El correo es obligatorio.";
            } else if (
                !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formulario.correo)
            ) {
                nuevosErrores.correo = "Correo electrónico inválido.";
            }

            if (!/^\d{10}$/.test(formulario.telefono)) {
                nuevosErrores.telefono = "Debe contener exactamente 10 dígitos.";
            }

        }

        if (pasoActual === 2) {
            if (!formulario.calle.trim())
                nuevosErrores.calle = "Campo obligatorio.";

            if (!formulario.colonia.trim())
                nuevosErrores.colonia = "Campo obligatorio.";

            if (!formulario.ciudad.trim())
                nuevosErrores.ciudad = "Campo obligatorio.";

            if (!/^\d{5}$/.test(formulario.codigoPostal))
                nuevosErrores.codigoPostal = "Debe tener 5 dígitos.";

        }

        if (pasoActual === 3) {
            if (!formulario.tuvoMascotas)
                nuevosErrores.tuvoMascotas = "Selecciona una opción.";

            if (!formulario.tieneMascotas)
                nuevosErrores.tieneMascotas = "Selecciona una opción.";

            if (formulario.experiencia.trim().length < 15)
                nuevosErrores.experiencia =
                    "Describe un poco más tu experiencia.";

        }

        if (pasoActual === 4) {
            if (formulario.motivo.trim().length < 20)
                nuevosErrores.motivo =
                    "Escribe un motivo un poco más detallado.";

        }

        setErrores(nuevosErrores);

        return Object.keys(nuevosErrores).length === 0;

    };

    const siguientePaso = () => {
        if (!validarPaso()) return;
        setErrores({});
        if (pasoActual < 4) {
            setPasoActual(pasoActual + 1);
        }
    };

    const pasoAnterior = () => {
        setErrores({});
        if (pasoActual > 1) {
            setPasoActual(pasoActual - 1);
        }
    };

    const enviarSolicitud = () => {
        if (!validarPaso()) return;
        console.log(formulario);
        setSolicitudEnviada(true);
        // await axios.post("/api/solicitudes", formulario);
    };





    return (
        <main className="solicitud">
            <section className="contenedor-solicitud">
                <h1>Solicitud de Adopción</h1>
                <p>
                    Estás a unos pasos de cambiar una vida.
                    <br />
                    Por favor, completa la siguiente información con sinceridad.
                </p>

                <BarraPasos pasoActual={pasoActual} />
                {pasoActual === 1 && (
                    <DatosPersonales
                        siguiente={siguientePaso}
                        formulario={formulario}
                        actualizarFormulario={actualizarFormulario}
                        errores={errores}
                    />
                )}
                {pasoActual === 2 && (
                    <Paso2Domicilio
                        anterior={pasoAnterior}
                        siguiente={siguientePaso}
                        formulario={formulario}
                        actualizarFormulario={actualizarFormulario}
                        errores={errores}
                    />
                )}
                {pasoActual === 3 && (
                    <Paso3Experiencia
                        anterior={pasoAnterior}
                        siguiente={siguientePaso}
                        formulario={formulario}
                        actualizarFormulario={actualizarFormulario}
                        errores={errores}
                    />

                )}

                {pasoActual === 4 && (

                    <Paso4Motivo
                        anterior={pasoAnterior}
                        formulario={formulario}
                        actualizarFormulario={actualizarFormulario}
                        enviarSolicitud={enviarSolicitud}
                        errores={errores}
                    />

                )}

            </section>
            {solicitudEnviada && (

                <div className="mensaje-exito">
                    Tu solicitud fue enviada correctamente.
                </div>
            )}
        </main>
    );

}

export default SolicitudAdopcion;