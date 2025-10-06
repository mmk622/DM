import React, { useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";

const API = axios.create({
  baseURL: "http://localhost:8080",
});

export default function Login() {
  const nav = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const onSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setBusy(true);
    try {
      const res = await API.post("/api/auth/login", { email, password });
      const token = res.data?.accessToken;
      if (!token) throw new Error("토큰이 응답에 없습니다.");
      localStorage.setItem("accessToken", token);
      nav("/", { replace: true });
    } catch (err: any) {
      setError(
        err?.response?.data?.message ||
        err?.message ||
        "로그인에 실패했습니다."
      );
    } finally {
      setBusy(false);
    }
  };

  return (
    <div style={{ maxWidth: 420, margin: "64px auto" }}>
      <h1>로그인</h1>
      <form onSubmit={onSubmit} style={{ display: "grid", gap: 12 }}>
        <label>
          이메일
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.currentTarget.value)}
            required
            placeholder="you@example.com"
          />
        </label>
        <label>
          비밀번호
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.currentTarget.value)}
            required
            placeholder="••••••••"
          />
        </label>
        <button type="submit" disabled={busy}>
          {busy ? "로그인 중..." : "로그인"}
        </button>
      </form>

      {error && (
        <p style={{ color: "crimson", marginTop: 12 }}>
          {String(error)}
        </p>
      )}

      <div style={{ marginTop: 16 }}>
        회원이 아니신가요? <Link to="/signup">회원가입</Link>
      </div>
    </div>
  );
}