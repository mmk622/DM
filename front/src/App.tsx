import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function App() {
  const nav = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      nav("/profile", { replace: true });
    } else {
      nav("/login", { replace: true });
    }
  }, [nav]);

  // 라우터 이동만 담당하므로 화면은 비움
  return null;
}