import { Outlet } from "react-router-dom";

import Navbar from "../components/Navbar/Navbar";

function AdminLayout() {
    return (
        <>
            <Navbar />

            <main className="admin-layout">
                <Outlet />
            </main>
        </>
    );
}

export default AdminLayout;