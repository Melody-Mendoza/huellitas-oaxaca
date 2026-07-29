import { useEffect, useState } from "react";
import "./Perfil.css";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import api from "../../services/api";
import { useAuth } from "../../context/AuthContext";

const passwordPattern =
    /^(?=.[A-Z])(?=.\d)(?=.*[^A-Za-z0-9]).+$/;

function Perfil() {
    const { user, updateUser } = useAuth();
    const [profile, setProfile] = useState(user);
    const [loadingProfile, setLoadingProfile] = useState(true);

    const {
        register: registerProfile,
        handleSubmit: handleProfileSubmit,
        reset: resetProfile,
        setError: setProfileError,
        formState: {
            errors: profileErrors,
            isSubmitting: isSavingProfile
        }
    } = useForm({
        defaultValues: {
            nombre: "",
            apellidoPaterno: "",
            apellidoMaterno: "",
            telefono: ""
        }
    });

    const {
        register: registerPassword,
        handleSubmit: handlePasswordSubmit,
        watch: watchPassword,
        reset: resetPassword,
        setError: setPasswordError,
        formState: {
            errors: passwordErrors,
            isSubmitting: isSavingPassword
        }
    } = useForm({
        defaultValues: {
            passwordActual: "",
            nuevaPassword: "",
            confirmarPassword: ""
        }
    });

    const nuevaPassword = watchPassword("nuevaPassword");

    useEffect(() => {
        let active = true;

        const loadProfile = async () => {
            try {
                const response = await api.get("/perfil");

                if (!active) {
                    return;
                }

                const loadedProfile = response.data;

                setProfile(loadedProfile);
                resetProfile({
                    nombre: loadedProfile.nombre ?? "",
                    apellidoPaterno:
                        loadedProfile.apellidoPaterno ?? "",
                    apellidoMaterno:
                        loadedProfile.apellidoMaterno ?? "",
                    telefono: loadedProfile.telefono ?? ""
                });
            } catch (error) {
                if (active && error.response?.status !== 401) {
                    toast.error(
                        error.response?.data?.message
                        ?? "No fue posible cargar el perfil"
                    );
                }
            } finally {
                if (active) {
                    setLoadingProfile(false);
                }
            }
        };

        loadProfile();

        return () => {
            active = false;
        };
    }, [resetProfile]);

    const applyServerErrors = (error, setError) => {
        const validationErrors =
            error.response?.data?.validationErrors;

        if (!validationErrors) {
            return;
        }

        Object.entries(validationErrors).forEach(
            ([field, message]) => {
                setError(field, {
                    type: "server",
                    message
                });
            }
        );
    };

    const onProfileSubmit = async (data) => {
        const payload = {
            nombre: data.nombre.trim(),
            apellidoPaterno: data.apellidoPaterno.trim(),
            apellidoMaterno: data.apellidoMaterno.trim(),
            telefono: data.telefono.trim()
        };

        try {
            const response = await api.put("/perfil", payload);
            const updatedProfile = response.data;

            setProfile(updatedProfile);
            updateUser(updatedProfile);
            resetProfile({
                nombre: updatedProfile.nombre ?? "",
                apellidoPaterno:
                    updatedProfile.apellidoPaterno ?? "",
                apellidoMaterno:
                    updatedProfile.apellidoMaterno ?? "",
                telefono: updatedProfile.telefono ?? ""
            });

            toast.success("Perfil actualizado correctamente");
        } catch (error) {
            applyServerErrors(error, setProfileError);

            if (error.response?.status !== 401) {
                toast.error(
                    error.response?.data?.message
                    ?? "No fue posible actualizar el perfil"
                );
            }
        }
    };

    const onPasswordSubmit = async (data) => {
        const payload = {
            passwordActual: data.passwordActual,
            nuevaPassword: data.nuevaPassword,
            confirmarPassword: data.confirmarPassword
        };

        try {
            const response = await api.patch(
                "/perfil/password",
                payload
            );

            resetPassword();
            toast.success(
                response.data?.mensaje
                ?? "Contraseña actualizada correctamente"
            );
        } catch (error) {
            applyServerErrors(error, setPasswordError);

            if (error.response?.status === 422) {
                setPasswordError("passwordActual", {
                    type: "server",
                    message: error.response.data?.message
                        ?? "No fue posible cambiar la contraseña"
                });
            }

            if (error.response?.status !== 401) {
                toast.error(
                    error.response?.data?.message
                    ?? "No fue posible cambiar la contraseña"
                );
            }
        }
    };

    if (loadingProfile) {
        return <p>Cargando perfil...</p>;
    }

    return (
        <main className="perfil-page">
            <h1>Mi perfil</h1>

            <section>
                <h2>Datos personales</h2>

                <form
                    onSubmit={handleProfileSubmit(onProfileSubmit)}
                    noValidate
                >
                    <label htmlFor="perfil-correo">
                        Correo electrónico
                    </label>
                    <input
                        id="perfil-correo"
                        type="email"
                        value={profile?.correo ?? ""}
                        disabled
                    />

                    <label htmlFor="perfil-rol">Rol</label>
                    <input
                        id="perfil-rol"
                        type="text"
                         value={profile?.rol?.nombre ?? ""}
                        disabled
                    />

                    <label htmlFor="perfil-nombre">Nombre</label>
                    <input
                        id="perfil-nombre"
                        type="text"
                        {...registerProfile("nombre", {
                            required:
                                "El nombre es obligatorio.",
                            maxLength: {
                                value: 30,
                                message:
                                    "El nombre no puede superar los 30 caracteres."
                            }
                        })}
                    />
                    {profileErrors.nombre && (
                        <p className="error">
                            {profileErrors.nombre.message}
                        </p>
                    )}

                    <label htmlFor="perfil-apellido-paterno">
                        Apellido paterno
                    </label>
                    <input
                        id="perfil-apellido-paterno"
                        type="text"
                        {...registerProfile("apellidoPaterno", {
                            required:
                                "El apellido paterno es obligatorio.",
                            maxLength: {
                                value: 20,
                                message:
                                    "El apellido paterno no puede superar los 20 caracteres."
                            }
                        })}
                    />
                    {profileErrors.apellidoPaterno && (
                        <p className="error">
                            {profileErrors.apellidoPaterno.message}
                        </p>
                    )}

                    <label htmlFor="perfil-apellido-materno">
                        Apellido materno
                    </label>
                    <input
                        id="perfil-apellido-materno"
                        type="text"
                        {...registerProfile("apellidoMaterno", {
                            maxLength: {
                                value: 20,
                                message:
                                    "El apellido materno no puede superar los 20 caracteres."
                            }
                        })}
                    />
                    {profileErrors.apellidoMaterno && (
                        <p className="error">
                            {profileErrors.apellidoMaterno.message}
                        </p>
                    )}

                    <label htmlFor="perfil-telefono">
                        Teléfono
                    </label>
                    <input
                        id="perfil-telefono"
                        type="tel"
                        {...registerProfile("telefono", {
                            pattern: {
                                value: /^$|^[0-9]{10}$/,
                                message:
                                    "El teléfono debe contener 10 dígitos."
                            }
                        })}
                    />
                    {profileErrors.telefono && (
                        <p className="error">
                            {profileErrors.telefono.message}
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={isSavingProfile}
                    >
                        {isSavingProfile
                            ? "Guardando..."
                            : "Guardar perfil"}
                    </button>
                </form>
            </section>

            <section>
                <h2>Cambiar contraseña</h2>

                <form
                    onSubmit={handlePasswordSubmit(
                        onPasswordSubmit
                    )}
                    noValidate
                >
                    <label htmlFor="password-actual">
                        Contraseña actual
                    </label>
                    <input
                        id="password-actual"
                        type="password"
                        autoComplete="current-password"
                        {...registerPassword("passwordActual", {
                            required:
                                "La contraseña actual es obligatoria."
                        })}
                    />
                    {passwordErrors.passwordActual && (
                        <p className="error">
                            {passwordErrors.passwordActual.message}
                        </p>
                    )}

                    <label htmlFor="password-nueva">
                        Nueva contraseña
                    </label>
                    <input
                        id="password-nueva"
                        type="password"
                        autoComplete="new-password"
                        {...registerPassword("nuevaPassword", {
                            required:
                                "La nueva contraseña es obligatoria.",
                            minLength: {
                                value: 8,
                                message:
                                    "La nueva contraseña debe tener entre 8 y 14 caracteres."
                            },
                            maxLength: {
                                value: 14,
                                message:
                                    "La nueva contraseña debe tener entre 8 y 14 caracteres."
                            },
                            pattern: {
                                value: passwordPattern,
                                message:
                                    "La nueva contraseña debe contener una mayúscula, un número y un carácter especial."
                            }
                        })}
                    />
                       {passwordErrors.nuevaPassword && (
                        <p className="error">
                            {passwordErrors.nuevaPassword.message}
                        </p>
                    )}

                    <label htmlFor="password-confirmar">
                        Confirmar nueva contraseña
                    </label>
                    <input
                        id="password-confirmar"
                        type="password"
                        autoComplete="new-password"
                        {...registerPassword(
                            "confirmarPassword",
                            {
                                required:
                                    "La confirmación de contraseña es obligatoria.",
                                validate: (value) => (
                                    value === nuevaPassword
                                    || "Las contraseñas nuevas no coinciden."
                                )
                            }
                        )}
                    />
                    {passwordErrors.confirmarPassword && (
                        <p className="error">
                            {passwordErrors.confirmarPassword.message}
                        </p>
                    )}

                    <button
                        type="submit"
                        disabled={isSavingPassword}
                    >
                        {isSavingPassword
                            ? "Actualizando..."
                            : "Cambiar contraseña"}
                    </button>
                </form>
            </section>
        </main>
    );
}

export default Perfil;