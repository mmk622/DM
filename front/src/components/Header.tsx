import { useNavigate, useLocation } from "react-router-dom";
import { useEffect, useState } from "react";

export default function Header() {
    const navigate = useNavigate();
    const location = useLocation();
    const [isLoggedIn, setIsLoggedIn] = useState(
        !!localStorage.getItem("accessToken")
    );

    const refreshAuth = () => {
        setIsLoggedIn(!!localStorage.getItem("accessToken"));
    };

    useEffect(() => {
        // 커스텀 이벤트 구독
        const onAuthChanged = () => refreshAuth();
        window.addEventListener("auth-changed", onAuthChanged);

        // 다른 탭 변경 감지
        const onStorage = (e: StorageEvent) => {
            if (e.key === "accessToken") refreshAuth();
        };
        window.addEventListener("storage", onStorage);

        // 라우트 바뀔 때도 재평가
        refreshAuth();

        return () => {
            window.removeEventListener("auth-changed", onAuthChanged);
            window.removeEventListener("storage", onStorage);
        };
    }, [location.pathname]);

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        window.dispatchEvent(new Event("auth-changed")); // ← 추가
        setIsLoggedIn(false);
        navigate("/login");
    };

    return (
        <header
            style={{
                width: "100%",
                height: "60px",
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
                padding: "0 24px",
                backgroundColor: "#f8f9fa",
                borderBottom: "1px solid #ddd",
                position: "fixed",
                top: 0,
                left: 0,
                zIndex: 100,
            }}
        >
            <h2
                style={{
                    cursor: "pointer",
                    color: "#007bff",
                    fontWeight: "bold",
                    fontSize: "18px",
                    margin: 0,
                }}
                onClick={() => navigate("/")}
            >
                DM: Dongguk Mealmate
            </h2>

            <div style={{ display: "flex", gap: "12px", marginRight: "40px" }}>
                {isLoggedIn ? (
                    <>
                        <button
                            onClick={() => navigate("/profile")}
                            style={{
                                backgroundColor: "#007bff",
                                color: "white",
                                border: "none",
                                borderRadius: "6px",
                                padding: "8px 14px",
                                cursor: "pointer",
                                fontSize: "14px",
                            }}
                        >
                            마이페이지
                        </button>
                        <button
                            onClick={handleLogout}
                            style={{
                                backgroundColor: "#dc3545",
                                color: "white",
                                border: "none",
                                borderRadius: "6px",
                                padding: "8px 14px",
                                cursor: "pointer",
                                fontSize: "14px",
                            }}
                        >
                            로그아웃
                        </button>
                    </>
                ) : (
                    <button
                        onClick={() => navigate("/login")}
                        style={{
                            backgroundColor: "#007bff",
                            color: "white",
                            border: "none",
                            borderRadius: "6px",
                            padding: "8px 14px",
                            cursor: "pointer",
                            fontSize: "14px",
                        }}
                    >
                        로그인
                    </button>
                )}
            </div>
        </header>
    );
}