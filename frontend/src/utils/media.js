export function resolveMediaUrl(value) {
    if (typeof value !== "string" || !value.trim()) {
        return "";
    }

    const normalized = value.trim();
    if (/^https?:\/\//i.test(normalized)) {
        return normalized;
    }

    return `/${normalized.replace(/^\/+/, "")}`;
}
