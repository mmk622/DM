import React, { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";

const API = axios.create({
    baseURL: "http://localhost:8080",
});

type VerifyResponse = {
    // 백엔드에서 주는 필드 이름에 맞춰 필요 시 수정
    signupToken?: string;
    user?: {
        email: string;
        name?: string | null;
        nickname?: string | null;
    };
    // 이미 가입완료 상태라면 액세스 토큰 줄 수도 있음 (옵션)
    accessToken?: string;
};

export default function Signup() {
    const nav = useNavigate();

    // step: 1=이메일 입력, 2=OTP 입력, 3=정보 입력
    const [step, setStep] = useState<1 | 2 | 3>(1);

    // 공통 상태
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // 입력값
    const [email, setEmail] = useState("");
    const [otp, setOtp] = useState("");

    const [name, setName] = useState("");
    const [nickname, setNickname] = useState("");
    const [password, setPassword] = useState("");

    // 서버에서 받은 임시 토큰(가입 완료용)
    const [signupToken, setSignupToken] = useState<string | null>(null);

    // 1) OTP 요청
    const requestOtp = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setBusy(true);
        try {
            await API.post("/api/auth/request-otp", { email });
            setStep(2);
        } catch (err: any) {
            setError(
                err?.response?.data?.message ||
                err?.message ||
                "인증 메일 전송에 실패했습니다."
            );
        } finally {
            setBusy(false);
        }
    };

    // 2) OTP 검증
    const verifyOtp = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setBusy(true);
        try {
            const res = await API.post<VerifyResponse>("/api/auth/verify-otp", {
                email,
                code: otp,
            });

            // 이미 가입이 끝난 계정이라면 accessToken을 줄 수도 있음
            if (res.data?.accessToken) {
                localStorage.setItem("accessToken", res.data.accessToken);
                nav("/", { replace: true });
                return;
            }

            const token = res.data?.signupToken;
            if (!token) throw new Error("signupToken이 없습니다.");
            setSignupToken(token);

            // optional: 기존 일부 정보가 있으면 채워주기
            const u = res.data?.user;
            if (u?.name) setName(u.name);
            if (u?.nickname) setNickname(u.nickname);

            setStep(3);
        } catch (err: any) {
            setError(
                err?.response?.data?.message ||
                err?.message ||
                "OTP 검증에 실패했습니다."
            );
        } finally {
            setBusy(false);
        }
    };

    // 3) 정보 입력 및 회원가입 완료
    const completeSignup = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setBusy(true);
        try {
            if (!signupToken) throw new Error("signupToken이 없습니다.");
            const res = await API.post("/api/auth/complete-signup", {
                signupToken,
                name,
                nickname,
                password,
            });
            const token = res.data?.accessToken;
            if (!token) throw new Error("액세스 토큰이 응답에 없습니다.");
            localStorage.setItem("accessToken", token);
            nav("/", { replace: true });
        } catch (err: any) {
            setError(
                err?.response?.data?.message ||
                err?.message ||
                "회원가입에 실패했습니다."
            );
        } finally {
            setBusy(false);
        }
    };

    return (
        <div style={{ maxWidth: 480, margin: "64px auto" }}>
            <h1>회원가입</h1>

            {step === 1 && (
                <form onSubmit={requestOtp} style={{ display: "grid", gap: 12 }}>
                    <label>
                        이메일
                        <input
                            type="email"
                            required
                            value={email}
                            onChange={(e) => setEmail(e.currentTarget.value)}
                            placeholder="학번@dongguk.ac.kr"
                        />
                    </label>
                    <button type="submit" disabled={busy}>
                        {busy ? "인증 메일 발송 중..." : "인증 메일 보내기"}
                    </button>
                </form>
            )}

            {step === 2 && (
                <form onSubmit={verifyOtp} style={{ display: "grid", gap: 12 }}>
                    <p style={{ color: "#666" }}>
                        {email} 주소로 전송된 인증코드를 입력하세요.
                    </p>

                    {/* OTP 입력 + 타이머 */}
                    <label style={{ display: "flex", alignItems: "center", gap: 10 }}>
                        <div style={{ flexGrow: 1 }}>
                            인증코드(OTP)
                            <input
                                type="text"
                                required
                                value={otp}
                                onChange={(e) => setOtp(e.currentTarget.value)}
                                placeholder="6자리 코드"
                            />
                        </div>
                        <Timer key={email} />
                    </label>
                    <div style={{ display: "flex", gap: 8 }}>
                        <button type="button" onClick={() => setStep(1)} disabled={busy}>
                            이메일 변경
                        </button>
                        <button type="submit" disabled={busy}>
                            {busy ? "확인 중..." : "확인"}
                        </button>
                    </div>
                </form>
            )}


            {step === 3 && (
                <form onSubmit={completeSignup} style={{ display: "grid", gap: 12 }}>
                    <label>
                        이름
                        <input
                            type="text"
                            required
                            value={name}
                            onChange={(e) => setName(e.currentTarget.value)}
                            placeholder="김동국"
                        />
                    </label>

                    <label>
                        닉네임
                        <input
                            type="text"
                            required
                            value={nickname}
                            onChange={(e) => setNickname(e.currentTarget.value)}
                            placeholder="동국"
                        />
                    </label>

                    <label>
                        비밀번호
                        <input
                            type="password"
                            required
                            value={password}
                            onChange={(e) => setPassword(e.currentTarget.value)}
                            placeholder="8자 이상"
                            minLength={8}
                        />
                    </label>

                    <button type="submit" disabled={busy}>
                        {busy ? "가입 처리 중..." : "회원가입 완료"}
                    </button>
                </form>
            )}

            {error && (
                <p style={{ color: "crimson", marginTop: 12 }}>
                    {String(error)}
                </p>
            )}

            <div style={{ marginTop: 16 }}>
                이미 계정이 있으신가요? <Link to="/login">로그인</Link>
            </div>
        </div>
    );
}

function Timer() {
    const [seconds, setSeconds] = useState(300); // 5분 = 300초

    useEffect(() => {
        if (seconds <= 0) return;
        const interval = setInterval(() => {
            setSeconds((prev) => prev - 1);
        }, 1000);
        return () => clearInterval(interval);
    }, [seconds]);

    const minutes = Math.floor(seconds / 60);
    const sec = seconds % 60;

    return (
        <span
            style={{
                fontSize: "0.9rem",
                color: seconds < 60 ? "red" : "#555",
                minWidth: 60,
                textAlign: "right",
            }}
        >
            {minutes}:{sec.toString().padStart(2, "0")}
        </span>
    );
}