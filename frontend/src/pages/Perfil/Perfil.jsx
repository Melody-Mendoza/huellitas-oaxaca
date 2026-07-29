import { useEffect, useRef, useState } from "react";
import {
    Camera,
    Eye,
    EyeOff,
    Trash2
} from "lucide-react";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import Avatar from "../../components/Avatar/Avatar";
import api from "../../services/api";
import { useAuth } from "../../context/AuthContext";
import "./Perfil.css";

function Perfil() {
    const { user, updateUser } = useAuth();
    const [profile, setProfile] = useState(user);
    const [loadingProfile, setLoadingProfile] = useState(true);
    const [isUpdatingPhoto, setIsUpdatingPhoto] = useState(false);
    const [showCurrentPassword, setShowCurrentPassword] =
        useState(false);
    const [showNewPassword, setShowNewPassword] =
        useState(false);
    const [showConfirmPassword, setShowConfirmPassword] =
        useState(false);
    const photoInputRef = useRef(null);

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

    const handlePhotoChange = async (event) => {
        const archivo = event.target.files?.[0];

        event.target.value = "";

        if (!archivo) {
            return;
        }

        if (!["image/jpeg", "image/png"].includes(archivo.type)) {
            toast.error("Selecciona una imagen JPG o PNG");
            return;
        }

        const maxPhotoSize = 5 * 1024 * 1024;

        if (archivo.size > maxPhotoSize) {
            toast.error(
                "La fotografía no debe superar los 5 MiB"
            );
            return;
        }

        const formData = new FormData();
        formData.append("foto", archivo);

        try {
            setIsUpdatingPhoto(true);

            const response = await api.patch(
                "/perfil/foto",
                formData
            );

            setProfile(response.data);
            updateUser(response.data);
            toast.success(
                "Fotografía actualizada correctamente"
            );
        } catch (error) {
            if (error.response?.status !== 401) {
                toast.error(
                    error.response?.data?.message
                    ?? "No fue posible actualizar la fotografía"
                );
            }
        } finally {
            setIsUpdatingPhoto(false);
        }
    };

    const handleDeletePhoto = async () => {
        const confirmed = window.confirm(
            "¿Deseas eliminar tu fotografía de perfil?"
        );

        if (!confirmed) {
            return;
        }

        try {
            setIsUpdatingPhoto(true);

            const response = await api.delete("/perfil/foto");

            setProfile(response.data);
            updateUser(response.data);
            toast.success(
                "Fotografía eliminada correctamente"
            );
        } catch (error) {
            if (error.response?.status !== 401) {
                toast.error(
                    error.response?.data?.message
                    ?? "No fue posible eliminar la fotografía"
                );
            }
        } finally {
            setIsUpdatingPhoto(false);
        }
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
            const status = error.response?.status;
            const responseData = error.response?.data;
            const message = responseData?.message
                ?? "No fue posible cambiar la contraseña";

            applyServerErrors(error, setPasswordError);

            if (status === 422) {
                const passwordErrorFields = {
                    "La contraseña actual es incorrecta":
                        "passwordActual",
                    "Las contraseñas nuevas no coinciden":
                        "confirmarPassword",
                    "La nueva contraseña debe ser diferente a la actual":
                        "nuevaPassword"
                };
                const field = passwordErrorFields[message];

                setPasswordError(field ?? "root.server", {
                    type: "server",
                    message
                });
            }

            if (status !== 401) {
                toast.error(message);
            }
        }
    };

    if (loadingProfile) {
        return <p>Cargando perfil...</p>;
    }

    return (
        <main className="perfil-page">
            <h1>Mi perfil</h1>

            <section className="perfil-photo-section">
                <Avatar
                    user={profile}
                    size={144}
                    className="perfil-photo-avatar"
                />

                <div className="perfil-photo-content">
                    <h2>Fotografía de perfil</h2>
                    <p>
                        Usa una imagen JPG o PNG de hasta 5 MiB.
                        La fotografía aparecerá en tu perfil y en
                        la barra de navegación.
                    </p>

                    <input
                        ref={photoInputRef}
                        className="perfil-photo-input"
                        type="file"
                        accept=".jpg,.jpeg,.png,image/jpeg,image/png"
                        onChange={handlePhotoChange}
                        disabled={isUpdatingPhoto}
                    />

                    <div className="perfil-photo-actions">
                        <button
                            type="button"
                            className={
                                "perfil-photo-button "
                                + "perfil-photo-button-primary"
                            }
                            onClick={() => {
                                photoInputRef.current?.click();
                            }}
                            disabled={isUpdatingPhoto}
                        >
                            <Camera
                                size={19}
                                aria-hidden="true"
                            />
                            {isUpdatingPhoto
                                ? "Procesando..."
                                : profile?.fotoPerfil
                                    ? "Reemplazar fotografía"
                                    : "Subir fotografía"}
                        </button>

                        {profile?.fotoPerfil && (
                            <button
                                type="button"
                                className={
                                    "perfil-photo-button "
                                    + "perfil-photo-button-danger"
                                }
                                onClick={handleDeletePhoto}
                                disabled={isUpdatingPhoto}
                            >
                                <Trash2
                                    size={19}
                                    aria-hidden="true"
                                />
                                Eliminar fotografía
                            </button>
                        )}
                    </div>
                </div>
            </section>

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
                    <div className="perfil-password-field">
                        <input
                            id="password-actual"
                            type={
                                showCurrentPassword
                                    ? "text"
                                    : "password"
                            }
                            autoComplete="current-password"
                            {...registerPassword(
                                "passwordActual",
                                {
                                    required:
                                        "La contraseña actual es obligatoria."
                                }
                            )}
                        />
                        <button
                            type="button"
                            className="perfil-password-toggle"
                            onClick={() => (
                                setShowCurrentPassword(
                                    (visible) => !visible
                                )
                            )}
                            aria-label={
                                showCurrentPassword
                                    ? "Ocultar contraseña actual"
                                    : "Mostrar contraseña actual"
                            }
                            aria-pressed={showCurrentPassword}
                        >
                            {showCurrentPassword
                                ? (
                                    <EyeOff
                                        size={20}
                                        aria-hidden="true"
                                    />
                                )
                                : (
                                    <Eye
                                        size={20}
                                        aria-hidden="true"
                                    />
                                )}
                        </button>
                    </div>
                    {passwordErrors.passwordActual && (
                        <p className="error">
                            {passwordErrors.passwordActual.message}
                        </p>
                    )}

                    <label htmlFor="password-nueva">
                        Nueva contraseña
                    </label>
                    <div className="perfil-password-field">
                        <input
                            id="password-nueva"
                            type={
                                showNewPassword
                                    ? "text"
                                    : "password"
                            }
                            autoComplete="new-password"
                            {...registerPassword(
                                "nuevaPassword",
                                {
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
                                    validate: {
                                        uppercase: (value) => (
                                            /[A-Z]/.test(value)
                                            || "La nueva contraseña debe contener una mayúscula."
                                        ),
                                        number: (value) => (
                                            /\d/.test(value)
                                            || "La nueva contraseña debe contener un número."
                                        ),
                                        special: (value) => (
                                            /[^A-Za-z0-9]/.test(value)
                                            || "La nueva contraseña debe contener un carácter especial."
                                        )
                                    }
                                }
                            )}
                        />
                        <button
                            type="button"
                            className="perfil-password-toggle"
                            onClick={() => (
                                setShowNewPassword(
                                    (visible) => !visible
                                )
                            )}
                            aria-label={
                                showNewPassword
                                    ? "Ocultar nueva contraseña"
                                    : "Mostrar nueva contraseña"
                            }
                            aria-pressed={showNewPassword}
                        >
                            {showNewPassword
                                ? (
                                    <EyeOff
                                        size={20}
                                        aria-hidden="true"
                                    />
                                )
                                : (
                                    <Eye
                                        size={20}
                                        aria-hidden="true"
                                    />
                                )}
                        </button>
                    </div>
                    {passwordErrors.nuevaPassword && (
                        <p className="error">
                            {passwordErrors.nuevaPassword.message}
                        </p>
                    )}

                    <label htmlFor="password-confirmar">
                        Confirmar nueva contraseña
                    </label>
                    <div className="perfil-password-field">
                        <input
                            id="password-confirmar"
                            type={
                                showConfirmPassword
                                    ? "text"
                                    : "password"
                            }
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
                        <button
                            type="button"
                            className="perfil-password-toggle"
                            onClick={() => (
                                setShowConfirmPassword(
                                    (visible) => !visible
                                )
                            )}
                            aria-label={
                                showConfirmPassword
                                    ? "Ocultar confirmación de contraseña"
                                    : "Mostrar confirmación de contraseña"
                            }
                            aria-pressed={showConfirmPassword}
                        >
                            {showConfirmPassword
                                ? (
                                    <EyeOff
                                        size={20}
                                        aria-hidden="true"
                                    />
                                )
                                : (
                                    <Eye
                                        size={20}
                                        aria-hidden="true"
                                    />
                                )}
                        </button>
                    </div>
                    {passwordErrors.confirmarPassword && (
                        <p className="error">
                            {passwordErrors.confirmarPassword.message}
                        </p>
                    )}
                    {passwordErrors.root?.server && (
                        <p
                            className="error"
                            role="alert"
                        >
                            {passwordErrors.root.server.message}
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
