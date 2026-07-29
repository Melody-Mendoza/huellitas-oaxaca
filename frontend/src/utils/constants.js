export const DEFAULT_AVATAR =
    "/images/default-avatar.png";

export const USER_ROLES = {
    ADMIN: "ADMIN",
    USUARIO: "USUARIO",
    REFUGIO: "REFUGIO"
};

const VALID_USER_ROLES = new Set(
    Object.values(USER_ROLES)
);

export function getUserRole(user) {
    const roleName = user?.rol?.nombre;

    if (typeof roleName !== "string") {
        return null;
    }

    const normalizedRole =
        roleName.trim().toUpperCase();

    return VALID_USER_ROLES.has(normalizedRole)
        ? normalizedRole
        : null;
}