import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../lib/api"; // 기존 axios 인스턴스(api.ts)를 쓰는 경우

export default function Home() {
    const nav = useNavigate();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [busy, setBusy] = useState(false);
    const [err, setErr] = useState<string | null>(null);

    const onSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErr(null);
        setBusy(true);
        try {
            // 백엔드에 비밀번호 로그인 엔드포인트가 있다면 사용하세요: /api/auth/login
            // 응답은 { token, user } 형태라고 가정
            const { data } = await api.post("/api/auth/login", { email, password });
            if (data?.token) {
                localStorage.setItem("token", data.token);
            }
            nav("/profile");
        } catch (e: any) {
            setErr(e?.response?.data?.message || "로그인에 실패했어요.");
        } finally {
            setBusy(false);
        }
    };

    return (
        <main className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
            <div className="w-full max-w-md bg-white rounded-2xl shadow p-6">
                <h1 className="text-2xl font-semibold text-gray-900 mb-6">로그인</h1>

                <form onSubmit={onSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm text-gray-700 mb-1">이메일</label>
                        <input
                            type="email"
                            className="w-full rounded-lg border px-3 py-2 focus:outline-none focus:ring"
                            placeholder="you@example.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            autoComplete="email"
                        />
                    </div>
                    <div>
                        <label className="block text-sm text-gray-700 mb-1">비밀번호</label>
                        <input
                            type="password"
                            className="w-full rounded-lg border px-3 py-2 focus:outline-none focus:ring"
                            placeholder="••••••••"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            autoComplete="current-password"
                        />
                    </div>

                    {err && <p className="text-sm text-red-600">{err}</p>}

                    <button
                        type="submit"
                        disabled={busy}
                        className="w-full rounded-lg bg-black text-white py-2.5 disabled:opacity-60"
                    >
                        {busy ? "로그인 중..." : "로그인"}
                    </button>
                </form>

                <div className="mt-6 text-sm text-gray-600 text-center">
                    회원이 아니신가요?{" "}
                    <Link to="/signup" className="text-black font-medium underline underline-offset-4">
                        회원가입
                    </Link>
                </div>
            </div>
        </main>
    );
}