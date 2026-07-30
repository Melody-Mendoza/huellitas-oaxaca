import { useEffect, useRef, useState } from "react";
import {
    Camera,
    Eye,
    EyeOff,
    Trash2
} from "lucide-react";
import { useForm, useWatch } from "react-hook-form";
import toast from "react-hot-toast";
import Avatar from "../../components/Avatar/Avatar";
import Loader from "../../components/Loader/Loader";
import Modal from "../../components/Modal/Modal";
import api from "../../services/api";
import { useAuth } from "../../context/AuthContext";
import "./Perfil.css";

const PASSWORD_BUSINESS_ERRORS = {
    "La contraseña actual es incorrecta": "passwordActual",
    "Las contraseñas nuevas no coinciden": "confirmarPassword",
    "La nueva contraseña debe ser diferente a la actual":
        "nuevaPassword"
};

function isValidPasswordResponse(response) {
    return Boolean(
        response.status === 200
        && response.data
        && typeof response.data === "object"
        && typeof response.data.mensaje === "string"
        && response.data.mensaje.trim()
    );
}

function getPasswordErrorMessage(error) {
    if (!error.response) {
        return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde.";
    }

    const backendMessage = error.response.data?.message;

    switch (error.response.status) {
        case 400:
            return typeof backendMessage === "string"
                ? backendMessage
                : "Revisa los datos ingresados.";
        case 401:
            return "La sesión no es válida. Inicia sesión nuevamente.";
        case 403:
            return "No tienes permiso para cambiar la contraseña.";
        case 404:
            return "No se encontró el usuario autenticado.";
        case 422:
            return typeof backendMessage === "string"
                ? backendMessage
                : "No fue posible aplicar el cambio de contraseña.";
        case 500:
            return "No fue posible cargar la información en este momento. Inténtalo nuevamente más tarde.";
        default:
            return "No fue posible cambiar la contraseña.";
    }
}

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
        control: passwordControl,
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

    const nuevaPassword = useWatch({
        control: passwordControl,
        name: "nuevaPassword"
    });

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
        if (isUpdatingPhoto) {
            return;
        }

        setIsUpdatingPhoto(true);

        try {
            const confirmation = await Modal.confirm(
                "Eliminar fotografía",
                "¿Deseas eliminar tu fotografía de perfil?"
            );

            if (!confirmation.isConfirmed) {
                return;
            }

            const response = await api.delete("/perfil/foto");

            if (
                response.status !== 200
                || !response.data
                || typeof response.data !== "object"
            ) {
                toast.error(
                    "Recibimos una respuesta inesperada. Intenta de nuevo."
                );
                return;
            }

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
        if (
            profile?.tienePasswordLocal !== true
            || isSavingPassword
        ) {
            return;
        }

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

            if (!isValidPasswordResponse(response)) {
                toast.error(
                    "Recibimos una respuesta inesperada. Intenta de nuevo."
                );
                return;
            }

            resetPassword();
            toast.success(response.data.mensaje);
        } catch (error) {
            const status = error.response?.status;
            const message = getPasswordErrorMessage(error);

            applyServerErrors(error, setPasswordError);

            if (status === 422) {
                const field = PASSWORD_BUSINESS_ERRORS[message];

                setPasswordError(field ?? "root.server", {
                    type: "server",
                    message
                });
            }

            toast.error(message);
        }
    };

    if (loadingProfile) {
        return <Loader />;
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

            <section aria-labelledby="perfil-password-title">
                <h2 id="perfil-password-title">
                    Cambiar contraseña
                </h2>

                {profile?.tienePasswordLocal === true ? (
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
                            aria-invalid={Boolean(
                                passwordErrors.passwordActual
                            )}
                            aria-describedby={
                                passwordErrors.passwordActual
                                    ? "password-actual-error"
                                    : undefined
                            }
                            disabled={isSavingPassword}
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
                            disabled={isSavingPassword}
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
                        <p
                            id="password-actual-error"
                            className="error"
                            role="alert"
                        >
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
                            aria-invalid={Boolean(
                                passwordErrors.nuevaPassword
                            )}
                            aria-describedby={
                                passwordErrors.nuevaPassword
                                    ? "password-nueva-error"
                                    : "password-nueva-help"
                            }
                            disabled={isSavingPassword}
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
                            disabled={isSavingPassword}
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
                    <p
                        id="password-nueva-help"
                        className="perfil-password-help"
                    >
                        Entre 8 y 14 caracteres, con una mayúscula,
                        un número y un carácter especial.
                    </p>
                    {passwordErrors.nuevaPassword && (
                        <p
                            id="password-nueva-error"
                            className="error"
                            role="alert"
                        >
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
                            aria-invalid={Boolean(
                                passwordErrors.confirmarPassword
                            )}
                            aria-describedby={
                                passwordErrors.confirmarPassword
                                    ? "password-confirmar-error"
                                    : undefined
                            }
                            disabled={isSavingPassword}
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
                            disabled={isSavingPassword}
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
                        <p
                            id="password-confirmar-error"
                            className="error"
                            role="alert"
                        >
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
                ) : (
                    <div
                        className={
                            profile?.tienePasswordLocal === false
                                ? "perfil-password-notice"
                                : "perfil-password-notice perfil-password-notice-warning"
                        }
                        role="status"
                        aria-live="polite"
                    >
                        {profile?.tienePasswordLocal === false ? (
                            <>
                                <strong>
                                    Cuenta vinculada con Google
                                </strong>
                                <p>
                                    Esta cuenta inicia sesión mediante
                                    Google. Actualmente no tiene una
                                    contraseña local que pueda cambiarse
                                    desde este perfil.
                                </p>
                            </>
                        ) : (
                            <>
                                <strong>
                                    Información no disponible
                                </strong>
                                <p>
                                    No pudimos confirmar si esta cuenta
                                    tiene una contraseña local. Por
                                    seguridad, el cambio de contraseña no
                                    está disponible en este momento.
                                </p>
                            </>
                        )}
                    </div>
                )}
            </section>
        </main>
    );
}

export default Perfil;
