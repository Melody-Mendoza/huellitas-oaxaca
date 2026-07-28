import { signInWithPopup } from "firebase/auth";
import { auth, googleProvider } from "./firebase";

export const signInWithGoogle = async () => {

    try {

        const result = await signInWithPopup(auth, googleProvider);

        const user = result.user;

        return {
            success: true,
            user: {
                uid: user.uid,
                nombre: user.displayName,
                correo: user.email,
                foto: user.photoURL
            }
        };

    } catch (error) {

        console.error("Error al iniciar sesión con Google:", error);

        return {
            success: false,
            error: error.message
        };

    }

};