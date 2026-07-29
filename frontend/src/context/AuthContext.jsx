import {
    createContext,
    useContext,
    useEffect,
    useState
} from "react";
import api, {
    AUTH_UNAUTHORIZED_EVENT,
    clearStoredSession,
    readStoredSession,
    writeStoredSession
} from "../services/api";
import { signInWithGoogle } from "../services/googleAuth";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [session, setSession] = useState(
        () => readStoredSession()
    );
    const [loading, setLoading] = useState(true);

    const saveSession = (nextSession) => {
        setSession(nextSession);

        if (nextSession) {
            writeStoredSession(nextSession);
        } else {
            clearStoredSession();
        }
    };

    useEffect(() => {
        let active = true;

        const handleUnauthorized = () => {
            if (active) {
                clearStoredSession();
                setSession(null);
                setLoading(false);
            }
        };

        const restoreSession = async () => {
            const storedSession = readStoredSession();

            if (!storedSession?.token) {
                if (active) {
                    setSession(null);
                    setLoading(false);
                }
                return;
            }

            try {
                const response = await api.get("/auth/me");

                if (active) {
                    const restoredSession = {
                        token: storedSession.token,
                        user: response.data
                    };

                    writeStoredSession(restoredSession);
                    setSession(restoredSession);
                }
            } catch {
                if (active) {
                    clearStoredSession();
                    setSession(null);
                }
            } finally {
                if (active) {
                    setLoading(false);
                }
            }
        };

        window.addEventListener(
            AUTH_UNAUTHORIZED_EVENT,
            handleUnauthorized
        );

        restoreSession();

        return () => {
            active = false;
            window.removeEventListener(
                AUTH_UNAUTHORIZED_EVENT,
                handleUnauthorized
            );
        };
    }, []);

    const login = async (credentials) => {
        const response = await api.post(
            "/auth/login",
            credentials
        );

        const nextSession = {
            token: response.data.token,
            user: response.data.usuario
        };

        saveSession(nextSession);

        return response.data;
    };

    const loginWithGoogle = async () => {
        const idToken = await signInWithGoogle();
        const response = await api.post("/auth/google", { idToken });

        const nextSession = {
            token: response.data.token,
            user: response.data.usuario
        };

        saveSession(nextSession);

        return response.data;
    };

    const logout = async () => {
        let logoutError = null;

        try {
            if (session?.token) {
                await api.post("/auth/logout");
            }
        } catch (error) {
            logoutError = error;
        } finally {
            saveSession(null);
        }

        if (logoutError) {
            throw logoutError;
        }
    };

    const updateUser = (updatedUser) => {
        setSession((currentSession) => {
            if (!currentSession?.token) {
                return currentSession;
            }

            const nextSession = {
                token: currentSession.token,
                user: updatedUser
            };

            writeStoredSession(nextSession);
            return nextSession;
        });
    };

    return (
        <AuthContext.Provider
            value={{
                user: session?.user ?? null,
                token: session?.token ?? null,
                loading,
                login,
                loginWithGoogle,
                logout,
                updateUser
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
