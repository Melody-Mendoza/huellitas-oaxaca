import axios from "axios";

export const AUTH_STORAGE_KEY = "huellitasAuth";
export const AUTH_UNAUTHORIZED_EVENT = "auth:unauthorized";

export function readStoredSession() {
    try {
        const storedSession = localStorage.getItem(AUTH_STORAGE_KEY);

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
    baseURL: "/api"
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

api.interceptors.request.use(
    (config) => {
        const token = readStoredSession()?.token;

        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => Promise.reject(error)
);

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            const hadStoredSession =
                Boolean(readStoredSession()?.token);

            clearStoredSession();

            if (hadStoredSession) {
                window.dispatchEvent(
                    new Event(AUTH_UNAUTHORIZED_EVENT)
                );
            }
        }

        return Promise.reject(error);
    }
);

export default api;