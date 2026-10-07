// Empty uses Vite locally; production needs VITE_API_URL or a host reverse proxy.
export const API_URL = (import.meta.env.VITE_API_URL || "").replace(/\/$/, "");
