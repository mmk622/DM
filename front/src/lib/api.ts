import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || "http://localhost:8080",
  withCredentials: false,
});

// 요청 인터셉터: 액세스 토큰 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token && token.trim().length > 0) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    if (import.meta.env.DEV) {
      // 개발 편의 로그
      console.warn("[api] no/invalid token, skipping Authorization", { token });
    }
  }
  return config;
});

// 응답 에러 공통 처리(선택)
api.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status;
    if (status === 401) {
      // 토큰 만료/유효하지 않음 → 로그인으로 유도 (필요 시)
      // localStorage.removeItem("accessToken");
      // window.location.href = "/login";
    }
    return Promise.reject(err);
  }
);

export default api;