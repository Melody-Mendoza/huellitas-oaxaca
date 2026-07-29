import { Route, Routes } from "react-router-dom";

import PublicLayout from "../layouts/PublicLayout";
import AdminLayout from "../layouts/AdminLayout";

import ProtectedRoute from "./ProtectedRoute";

import Home from "../pages/Home/Home";
import Login from "../pages/Login/Login";
import Register from "../pages/Register/Register";
import Catalogo from "../pages/Catalogo/Catalogo";
import DetalleMascota from "../pages/DetalleMascota/DetalleMascota";
import Donaciones from "../pages/Donaciones/Donaciones";
import SolicitudAdopcion from "../pages/SolicitudAdopcion/SolicitudAdopcion";
import NotFound from "../pages/NotFound/NotFound";
import Perfil from "../pages/Perfil/Perfil";
import ComoAdoptar from "../pages/Adopcion/ComoAdoptar";
import Historias from "../pages/Historias/Historias";
import Nosotros from "../pages/Nosotros/Nosotros";
import Privacidad from "../pages/Privacidad/Privacidad";
import Terminos from "../pages/Terminos/Terminos";
import Contacto from "../pages/Contacto/Contacto";
import Admin from "../pages/Admin/Admin";

import { USER_ROLES } from "../utils/constants";

function AppRouter() {
    return (
        <Routes>
            <Route element={<PublicLayout />}>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/catalogo" element={<Catalogo />} />
                <Route
                    path="/mascota/:id"
                    element={<DetalleMascota />}
                />
                <Route
                    path="/donaciones"
                    element={
                        <ProtectedRoute>
                            <Donaciones />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/solicitud"
                    element={
                        <ProtectedRoute>
                            <SolicitudAdopcion />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/adopcion"
                    element={<ComoAdoptar />}
                />
                <Route path="/historias" element={<Historias />} />
                <Route path="/nosotros" element={<Nosotros />} />
                <Route path="/privacidad" element={<Privacidad />} />
                <Route path="/terminos" element={<Terminos />} />
                <Route path="/contacto" element={<Contacto />} />
                <Route
                    path="/perfil"
                    element={
                        <ProtectedRoute>
                            <Perfil />
                        </ProtectedRoute>
                    }
                />
            </Route>

            <Route
                path="/admin"
                element={
                    <ProtectedRoute
                        allowedRoles={[USER_ROLES.ADMIN]}
                    >
                        <AdminLayout />
                    </ProtectedRoute>
                }
            >
                <Route index element={<Admin />} />
            </Route>

            <Route path="*" element={<NotFound />} />
        </Routes>
    );
}

export default AppRouter;