import React, { useEffect, useState } from "react";
import api from "../lib/api";
import { useNavigate } from "react-router-dom";

export default function CompleteProfile() {
    const nav = useNavigate();
    const [name, setName] = useState("");
    const [nickname, setNickname] = useState("");
    const [password, setPassword] = useState("");
    const [busy, setBusy] = useState(false);

    useEffect(() => {
        const st = localStorage.getItem("signupToken");
        if (!st) nav("/login");
    }, [nav]);

    async function submit() {
        const signupToken = localStorage.getItem("signupToken");
        if (!signupToken) return alert("인증 단계가 먼저 필요합니다.");

        try {
            setBusy(true);
            const { data, status } = await api.post("/api/auth/complete-signup", {
                signupToken, name, nickname, password
            });

            if (status === 200 && data?.accessToken) {
                localStorage.removeItem("signupToken");
                localStorage.removeItem("signupEmail");
                localStorage.setItem("accessToken", data.accessToken);
                nav("/profile"); // 프로필 화면으로 이동
            } else {
                alert("회원가입 완료에 실패했습니다.");
            }
        } catch (e: any) {
            alert(e?.response?.data ?? "회원가입 완료 실패");
        } finally {
            setBusy(false);
        }
    }

    return (
        <div style={{ maxWidth: 360, margin: "40px auto" }}>
            <h1>프로필 입력</h1>
            <input
                placeholder="이름"
                value={name}
                onChange={e => setName(e.target.value)}
                style={{ display: "block", width: "100%", marginBottom: 8 }}
                disabled={busy}
            />
            <input
                placeholder="닉네임"
                value={nickname}
                onChange={e => setNickname(e.target.value)}
                style={{ display: "block", width: "100%", marginBottom: 8 }}
                disabled={busy}
            />
            <input
                placeholder="비밀번호"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                style={{ display: "block", width: "100%", marginBottom: 12 }}
                disabled={busy}
            />
            <button onClick={submit} disabled={busy || !name.trim() || !nickname.trim() || !password.trim()}>
                완료
            </button>
        </div>
    );
}