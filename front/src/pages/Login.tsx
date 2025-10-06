import React, { useState } from "react";
import api from "../lib/api";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [sent, setSent] = useState(false);
  const [busy, setBusy] = useState(false);
  const nav = useNavigate();

  async function send() {
    try {
      setBusy(true);
      const { status } = await api.post("/api/auth/otp", { email });
      if (status === 200) setSent(true);
    } catch (e: any) {
      alert(e?.response?.data ?? "OTP 요청 실패");
    } finally {
      setBusy(false);
    }
  }

  async function verify() {
    try {
      setBusy(true);
      const { data, status } = await api.post("/api/auth/verify", { email, code });
      if (status === 200 && data?.verified && data?.signupToken) {
        localStorage.setItem("signupEmail", email);
        localStorage.setItem("signupToken", data.signupToken);
        nav("/complete-profile");
      } else {
        alert("인증에 실패했습니다.");
      }
    } catch (e: any) {
      alert(e?.response?.data ?? "인증 실패");
    } finally {
      setBusy(false);
    }
  }

  return (
    <div style={{ maxWidth: 360, margin: "40px auto" }}>
      <h1>로그인</h1>
      <input
        placeholder="email"
        value={email}
        onChange={e => setEmail(e.target.value)}
        style={{ display: "block", width: "100%", marginBottom: 8 }}
        disabled={busy}
      />
      {!sent ? (
        <button onClick={send} disabled={busy || !email.trim()}>인증 코드 받기</button>
      ) : (
        <>
          <input
            placeholder="인증코드"
            value={code}
            onChange={e => setCode(e.target.value)}
            style={{ display: "block", width: "100%", margin: "8px 0" }}
            disabled={busy}
          />
          <button onClick={verify} disabled={busy || !code.trim()}>인증하기</button>
        </>
      )}
    </div>
  );
}