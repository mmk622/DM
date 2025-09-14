import { useEffect, useState } from "react";
import api from "../lib/api";

type Me = {
  email: string;
  nickname?: string | null;
  name?: string | null;
  // 서버가 roles를 안 줄 수도 있으니 선택 + 기본값 처리
  roles?: string[] | null;
  createdAt?: string | null;
  updatedAt?: string | null;
};

export default function Profile() {
  const [me, setMe] = useState<Me | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        const { data } = await api.get("/api/users/me");
        setMe(data);
      } catch (e: any) {
        // 401이면 토큰 없음/만료. 로그인 페이지로 안내하거나 메시지만 표시
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

  if (loading) return <div className="p-6">불러오는 중…</div>;
  if (err) return <div className="p-6 text-red-600">{err}</div>;
  if (!me) return <div className="p-6">데이터가 없습니다.</div>;

  // 안전하게 표기
  const safeEmail = me.email ?? "-";
  const safeName = me.name ?? "-";
  const safeNick = me.nickname ?? "-";
  const safeRoles = (me.roles && me.roles.length > 0) ? me.roles.join(", ") : "-";
  const created = me.createdAt ? new Date(me.createdAt).toLocaleString() : "-";
  const updated = me.updatedAt ? new Date(me.updatedAt).toLocaleString() : "-";

  return (
    <div className="p-6 space-y-2">
      <h1 className="text-2xl font-bold">내 프로필</h1>
      <div><b>이메일:</b> {safeEmail}</div>
      <div><b>이름:</b> {safeName}</div>
      <div><b>닉네임:</b> {safeNick}</div>
      <div><b>권한:</b> {safeRoles}</div>
      <div><b>생성일:</b> {created}</div>
      <div><b>수정일:</b> {updated}</div>
    </div>
  );
}