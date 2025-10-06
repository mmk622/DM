import { useNavigate } from "react-router-dom";

export default function Main() {
    const navigate = useNavigate();

    return (
        <div
            style={{
                minHeight: "100vh",
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
                alignItems: "center",
                fontFamily: "sans-serif",
                position: "relative",
            }}
        >
            {/* 중앙 텍스트 */}
            <h1>이것은 메인 화면입니다.</h1>
        </div>
    );
}