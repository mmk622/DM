import React from "react";
import { Navigate, useLocation } from "react-router-dom";

type Props = { children: JSX.Element };

export default function RequireAuth({ children }: Props) {
    const token = localStorage.getItem("accessToken");
    const location = useLocation();

    if (!token) {
        // 로그인 안 된 상태면 로그인 화면으로
        return <Navigate to="/login" replace state={{ from: location }} />;
    }
    return children;
}