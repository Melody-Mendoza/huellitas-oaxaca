import { initializeApp } from "firebase/app";
import { getAuth, GoogleAuthProvider } from "firebase/auth";

const firebaseConfig = {
    apiKey: "AIzaSyA-O_pLbxDwmyD3txuatcL0a4_zk-N7SEs",
    authDomain: "huellitas-oaxaca.firebaseapp.com",
    projectId: "huellitas-oaxaca",
    storageBucket: "huellitas-oaxaca.firebasestorage.app",
    messagingSenderId: "101966070620",
    appId: "1:101966070620:web:4232558a69472d943af799",
    measurementId: "G-ECCJXBNNLM"
};

const app = initializeApp(firebaseConfig);

export const auth = getAuth(app);

export const googleProvider = new GoogleAuthProvider();