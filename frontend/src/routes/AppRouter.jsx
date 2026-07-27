import { Routes, Route } from "react-router-dom";

import PublicLayout from "../layouts/PublicLayout";

import ProtectedRoute from "./ProtectedRoute";

import Home from "../pages/Home/Home";
import Login from "../pages/Login/Login";
import Register from "../pages/Register/Register";
import Catalogo from "../pages/Catalogo/Catalogo";
import DetalleMascota from "../pages/DetalleMascota/DetalleMascota";
import Donaciones from "../pages/Donaciones/Donaciones";
import Solicitud from "../pages/Solicitud/Solicitud";
import NotFound from "../pages/NotFound/NotFound";
import Perfil from "../pages/Perfil/Perfil";

import Adopcion from "../pages/Adopcion/Adopcion";
import Historias from "../pages/Historias/Historias";
import Nosotros from "../pages/Nosotros/Nosotros";

import Privacidad from "../pages/Privacidad/Privacidad";
import Terminos from "../pages/Terminos/Terminos";
import Contacto from "../pages/Contacto/Contacto";
import RecuperarPassword from "../pages/RecuperarPassword/RecuperarPassword";


function AppRouter() {
    return (
        <Routes>

            <Route element={<PublicLayout />}>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/catalogo" element={<Catalogo />} />
                <Route path="/mascota/:id" element={<DetalleMascota />} />
                <Route path="/donaciones" element={<Donaciones />} />
                <Route path="/solicitud" element={<Solicitud />} />
                <Route path="/adopcion" element={<Adopcion />} />
                <Route path="/historias" element={<Historias />} />
                <Route path="/nosotros" element={<Nosotros />} />
                <Route path="/privacidad" element={<Privacidad />} />
                <Route path="/terminos" element={<Terminos />} />
                <Route path="/contacto" element={<Contacto />} />
                <Route
                    path="/recuperar-password"
                    element={<RecuperarPassword />}
                />
                <Route
                    path="/perfil"
                    element={
                        <ProtectedRoute>
                            <Perfil />
                        </ProtectedRoute>
                    }
                />
            </Route>

            <Route path="*" element={<NotFound />} />

        </Routes>

    );
}

export default AppRouter;