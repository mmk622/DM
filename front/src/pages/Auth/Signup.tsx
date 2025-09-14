import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../lib/api";

export default function Signup() {
  const nav = useNavigate();
  const [nickname, setNickname] = useState("");
  const [password, setPassword] = useState("");
  const [checking, setChecking] = useState(false);
  const [available, setAvailable] = useState<boolean | null>(null);

  const checkNickname = async () => {
    setChecking(true);
    try {
      const { data } = await api.get("/api/auth/check-nickname", { params: { nickname } });
      setAvailable(Boolean(data));
    } finally {
      setChecking(false);
    }
  };

  const submit = async () => {
    await api.post("/api/auth/complete-signup", { nickname, password });
    alert("회원가입이 완료되었습니다!");
    nav("/profile");
  };

  return (
    <div className="p-6 max-w-md mx-auto">
      <h1 className="text-2xl font-bold">프로필 설정</h1>

      <label className="block mt-4 text-sm">닉네임</label>
      <div className="flex gap-2">
        <input className="border p-2 w-full" value={nickname} onChange={e => setNickname(e.target.value)} placeholder="2~20자, 영문/숫자/한글/_" />
        <button className="border px-3" onClick={checkNickname} disabled={!nickname || checking}>
          중복확인
        </button>
      </div>
      {available === true && <p className="text-green-600 text-sm mt-1">사용 가능</p>}
      {available === false && <p className="text-red-600 text-sm mt-1">이미 사용 중</p>}

      <label className="block mt-4 text-sm">비밀번호</label>
      <input className="border p-2 w-full" type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="8자 이상" />

      <button className="btn mt-4 border px-4 py-2" onClick={submit} disabled={!nickname || !password}>
        완료
      </button>
    </div>
  );
}