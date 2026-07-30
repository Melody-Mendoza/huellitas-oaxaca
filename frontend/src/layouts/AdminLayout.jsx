import { Outlet } from "react-router-dom";

import Navbar from "../components/Navbar/Navbar";
import Footer from "../components/Footer/Footer";

function AdminLayout() {
    return (
        <div className="app-shell">
            <Navbar />

            <main className="admin-layout app-content">
                <Outlet />
            </main>
            <Footer />
        </div>
    );
}

export default AdminLayout;
