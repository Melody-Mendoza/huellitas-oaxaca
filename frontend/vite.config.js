import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  base: "/huellitas-oaxaca/",
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:1929",
        changeOrigin: true
      },
      "/media": {
        target: "http://localhost:1929",
        changeOrigin: true
      }
    }
  }

});