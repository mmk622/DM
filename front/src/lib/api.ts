import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: false,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (!config.headers) config.headers = {};
  if (token && token !== "undefined" && token !== "null" && token.length > 20) {
    (config.headers as any).Authorization = `Bearer ${token}`;
  } else {
    // 디버깅용: 당분간만 남겨두고 확인 끝나면 지워도 됨
    console.warn("[api] no/invalid token, skipping Authorization", { token });
  }
  return config;
});

export default api;