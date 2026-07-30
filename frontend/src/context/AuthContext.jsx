/* eslint-disable react-refresh/only-export-components */
import {
    createContext,
    useContext,
    useEffect,
    useRef,
    useState
} from "react";

import api, {
    clearStoredSession,
    readStoredSession,
    setUnauthorizedHandler,
    writeStoredSession
} from "../services/api";

import { signInWithGoogle } from "../services/googleAuth";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [session, setSession] = useState(
        () => readStoredSession()
    );

    const [loading, setLoading] = useState(true);


    const authOperationRef = useRef(0);

    const saveSession = (nextSession) => {
        setSession(nextSession);

        if (nextSession) {
            writeStoredSession(nextSession);
        } else {
            clearStoredSession();
        }
    };

    useEffect(() => {
        const controller = new AbortController();

        const operationId =
            ++authOperationRef.current;

        const handleUnauthorized = () => {

            authOperationRef.current += 1;

            clearStoredSession();
            setSession(null);
            setLoading(false);
        };

        const removeUnauthorizedHandler =
            setUnauthorizedHandler(
                handleUnauthorized
            );

        const restoreSession = async () => {
            const storedSession =
                readStoredSession();

            if (!storedSession?.token) {
                if (
                    authOperationRef.current
                    === operationId
                ) {
                    setSession(null);
                    setLoading(false);
                }

                return;
            }

            try {
                const response = await api.get(
                    "/auth/me",
                    {
                        signal: controller.signal
                    }
                );

                if (
                    authOperationRef.current
                    !== operationId
                ) {
                    return;
                }

                const restoredSession = {
                    token: storedSession.token,
                    user: response.data
                };

                writeStoredSession(restoredSession);
                setSession(restoredSession);
            } catch (error) {
                if (
                    authOperationRef.current
                    !== operationId
                    || error.code === "ERR_CANCELED"
                ) {
                    return;
                }


                if (error.response?.status === 401) {
                    clearStoredSession();
                    setSession(null);
                }
            } finally {
                if (
                    authOperationRef.current
                    === operationId
                ) {
                    setLoading(false);
                }
            }
        };

        restoreSession();

        return () => {
            controller.abort();

            removeUnauthorizedHandler();

            if (
                authOperationRef.current
                === operationId
            ) {
                authOperationRef.current += 1;
            }
        };
    }, []);

    const login = async (credentials) => {
        const operationId =
            ++authOperationRef.current;

        const response = await api.post(
            "/auth/login",
            credentials
        );

        if (
            authOperationRef.current
            === operationId
        ) {
            saveSession({
                token: response.data.token,
                user: response.data.usuario
            });
        }

        return response.data;
    };

    const loginWithGoogle = async () => {
        const operationId =
            ++authOperationRef.current;

        const idToken =
            await signInWithGoogle();

        if (
            authOperationRef.current
            !== operationId
        ) {
            return null;
        }

        const response = await api.post(
            "/auth/google",
            { idToken }
        );

        if (
            authOperationRef.current
            === operationId
        ) {
            saveSession({
                token: response.data.token,
                user: response.data.usuario
            });
        }

        return response.data;
    };

    const logout = async () => {
        const operationId =
            ++authOperationRef.current;

        const tokenToRevoke = session?.token;

        let logoutError = null;

        try {
            if (tokenToRevoke) {
                await api.post("/auth/logout");
            }
        } catch (error) {
            logoutError = error;
        } finally {

            if (
                authOperationRef.current
                === operationId
            ) {
                saveSession(null);
            }
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
