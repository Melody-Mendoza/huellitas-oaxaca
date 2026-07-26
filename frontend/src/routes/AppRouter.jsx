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