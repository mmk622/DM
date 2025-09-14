import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../lib/api";

export default function Login() {
  const nav = useNavigate();

  const [email, setEmail] = useState("");
  const [sent, setSent] = useState(false);
  const [code, setCode] = useState("");
  const [name, setName] = useState("");
  const [timer, setTimer] = useState(0); // 초 단위 (예: 180초=3분)
  const [loading, setLoading] = useState(false);

  // 카운트다운
  useEffect(() => {
    if (timer <= 0) return;
    const id = setInterval(() => setTimer((t) => t - 1), 1000);
    return () => clearInterval(id);
  }, [timer]);

  const send = async () => {
    if (!email) return;
    setLoading(true);
    try {
      await api.post("/api/auth/otp", { email });
      setSent(true);
      setTimer(300); // 5분
    } catch (e: any) {
      alert(e?.response?.data || "코드 요청 실패");
    } finally {
      setLoading(false);
    }
  };

  const resend = async () => {
    if (timer > 0) return;
    await send();
  };

  const verify = async () => {
    try {
      setLoading(true);
      const { data } = await api.post("/api/auth/verify", { email, code, name });
      console.log("[verify] response", data);

      const at = data?.accessToken;
      const rt = data?.refreshToken;
      if (typeof at !== "string" || at.length < 20) {
        alert("로그인 토큰을 받지 못했습니다. 서버 응답 키 이름을 확인하세요.");
        return;
      }

      localStorage.setItem("accessToken", at);
      if (typeof rt === "string") localStorage.setItem("refreshToken", rt);

      if (data?.profileIncomplete) {
        nav("/signup");
      } else {
        nav("/profile");
      }
    } catch (e: any) {
      console.error(e);
      alert(e?.response?.data || "인증 실패");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-md mx-auto">
      <h1 className="text-2xl font-bold">이메일 인증</h1>

      <input
        className="mt-4 border p-2 w-full"
        placeholder="학교 이메일(@dongguk.ac.kr)"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        disabled={sent && timer > 0} // 타이머 도는 동안 이메일 수정 잠금
      />

      {!sent && (
        <button
          className="btn mt-3 border px-4 py-2"
          onClick={send}
          disabled={!email || loading}
        >
          {loading ? "요청 중..." : "코드 받기"}
        </button>
      )}

      {sent && (
        <div className="mt-4">
          <div className="flex gap-2">
            <input
              className="border p-2 w-full"
              placeholder="6자리 인증번호"
              value={code}
              onChange={(e) => setCode(e.target.value)}
            />
            <button
              className="border px-4 py-2"
              onClick={resend}
              disabled={timer > 0 || loading}
              title={timer > 0 ? "타이머 종료 후 재전송 가능" : ""}
            >
              재전송
            </button>
          </div>

          <input
            className="border p-2 w-full mt-3"
            placeholder="이름"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />

          <button
            className="btn mt-3 border px-4 py-2"
            onClick={verify}
            disabled={!code || !name || loading}
          >
            {loading ? "확인 중..." : "확인"}
          </button>

          {/* 카운트다운 */}
          <p className="mt-2 text-sm text-gray-600">
            남은 시간: {Math.floor(timer / 60)}:{String(timer % 60).padStart(2, "0")}
          </p>
        </div>
      )}
    </div>
  );
}