import { useEffect, useState } from "react";
import api from "../lib/api";

type Me = {
  email: string;
  nickname?: string | null;
  name?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
};

export default function Profile() {
  const [me, setMe] = useState<Me | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        const { data } = await api.get("/api/users/me");
        setMe(data);
      } catch (e: any) {
        if (e?.response?.status === 401) {
          setErr("로그인이 필요합니다. 인증 토큰이 없거나 만료되었습니다.");
        } else {
          setErr(e?.response?.data || "프로필 로드 실패");
        }
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const onDelete = async () => {
    if (!confirm("정말 탈퇴하시겠습니까?")) return;
    try {
      setDeleting(true);
      await api.delete("/api/users/me");
      // 토큰/임시 값 제거
      localStorage.removeItem("accessToken");
      localStorage.removeItem("signupToken");
      localStorage.removeItem("signupEmail");
      // 홈(또는 로그인)으로 이동
      window.location.href = "/";
    } catch (e: any) {
      alert(e?.response?.data?.message ?? "탈퇴에 실패했습니다.");
    } finally {
      setDeleting(false);
    }
  };

  if (loading) return <div className="p-6">불러오는 중…</div>;
  if (err) return <div className="p-6 text-red-600">{err}</div>;
  if (!me) return <div className="p-6">데이터가 없습니다.</div>;

  const safeEmail = me.email ?? "-";
  const safeName = me.name ?? "-";
  const safeNick = me.nickname ?? "-";
  const created = me.createdAt ? new Date(me.createdAt).toLocaleString() : "-";
  const updated = me.updatedAt ? new Date(me.updatedAt).toLocaleString() : "-";

  return (
    <div className="p-6 space-y-2 relative bg-white shadow-md rounded-lg">
      <button
        onClick={onDelete}
        disabled={deleting}
        className="fixed top-[80px] right-6 text-sm px-3 py-1 border border-red-500 text-red-600 rounded hover:bg-red-50 shadow-sm transition"
        title="회원 탈퇴"
      >
        {deleting ? "탈퇴 중…" : "탈퇴"}
      </button>

      <h1 className="text-2xl font-bold mb-2">내 프로필</h1>
      <div><b>이메일:</b> {safeEmail}</div>
      <div><b>이름:</b> {safeName}</div>
      <div><b>닉네임:</b> {safeNick}</div>
      <div><b>생성일:</b> {created}</div>
      <div><b>수정일:</b> {updated}</div>
    </div>
  );
}