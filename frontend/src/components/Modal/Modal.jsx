import Swal from "sweetalert2";
const Modal = {
    success(title, text) {
        return Swal.fire({
            icon: "success",
            title,
            text,
            confirmButtonColor: "#4CAF50"
        });
    },

    error(title, text) {
        return Swal.fire({
            icon: "error",
            title,
            text,
            confirmButtonColor: "#E53935"
        });

    },

    confirm(title, text) {
        return Swal.fire({
            icon: "warning",
            title,
            text,
            showCancelButton: true,
            confirmButtonText: "Aceptar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#4CAF50"
        });
    }
};

export default Modal;