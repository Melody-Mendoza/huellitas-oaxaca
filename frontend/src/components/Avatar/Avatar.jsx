import "./Avatar.css";

function getDisplayName(user) {
    const fullName = [
        user?.nombre,
        user?.apellidoPaterno,
        user?.apellidoMaterno
    ]
        .filter(Boolean)
        .join(" ")
        .trim();

    return fullName || user?.correo || "Usuario";
}

function getInitials(user) {
    const nameParts = [
        user?.nombre,
        user?.apellidoPaterno
    ].filter(Boolean);

    if (nameParts.length > 0) {
        return nameParts
            .map((part) => part.trim().charAt(0))
            .join("")
            .toUpperCase();
    }

    return user?.correo?.trim().charAt(0).toUpperCase() || "U";
}

function Avatar({ user, size = 44, className = "" }) {
    const displayName = getDisplayName(user);
    const photoUrl = user?.fotoPerfil?.trim();

    return (
        <span
            className={`avatar ${className}`.trim()}
            style={{ "--avatar-size": `${size}px` }}
            role="img"
            aria-label={`Foto de perfil de ${displayName}`}
        >
            <span className="avatar-fallback" aria-hidden="true">
                {getInitials(user)}
            </span>

            {photoUrl && (
                <img
                    key={photoUrl}
                    className="avatar-image"
                    src={photoUrl}
                    alt=""
                    onError={(event) => {
                        event.currentTarget.hidden = true;
                    }}
                />
            )}
        </span>
    );
}

export default Avatar;