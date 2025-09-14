import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token && token !== "undefined" && token !== "null" && token.length > 10) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;