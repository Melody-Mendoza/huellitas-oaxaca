import axios from "axios";

export const AUTH_STORAGE_KEY = "huellitasAuth";

export function readStoredSession() {
    try {
        const storedSession = localStorage.getItem(
            AUTH_STORAGE_KEY
        );

        if (!storedSession) {
            return null;
        }

        const session = JSON.parse(storedSession);

        if (!session?.token || !session?.user) {
            localStorage.removeItem(AUTH_STORAGE_KEY);
            return null;
        }

        return session;
    } catch {
        localStorage.removeItem(AUTH_STORAGE_KEY);
        return null;
    }
}

export function writeStoredSession(session) {
    localStorage.setItem(
        AUTH_STORAGE_KEY,
        JSON.stringify(session)
    );
}

export function clearStoredSession() {
    localStorage.removeItem(AUTH_STORAGE_KEY);
}

const api = axios.create({
    baseURL: "/api",
    headers: {
        "Content-Type": "application/json"
    }
});

let unauthorizedHandler = null;

export function setUnauthorizedHandler(handler) {
    unauthorizedHandler = handler;

    return () => {
        if (unauthorizedHandler === handler) {
            unauthorizedHandler = null;
        }
    };
}

function getAuthorizationHeader(config) {
    const headers = config?.headers;

    if (typeof headers?.get === "function") {
        return headers.get("Authorization");
    }

    return (
        headers?.Authorization
        ?? headers?.authorization
        ?? null
    );
}

api.interceptors.request.use(
    (config) => {
        const token = readStoredSession()?.token;

        if (token) {
            config.headers.Authorization =
                `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            const currentSession = readStoredSession();

            const failedAuthorization =
                getAuthorizationHeader(error.config);

            const currentAuthorization =
                currentSession?.token
                    ? `Bearer ${currentSession.token}`
                    : null;
            if (
                currentAuthorization
                && failedAuthorization === currentAuthorization
            ) {
                clearStoredSession();
                unauthorizedHandler?.();
            }
        }

        return Promise.reject(error);
    }
);

export default api;