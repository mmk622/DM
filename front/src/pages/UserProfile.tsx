import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getPublicUser } from "../lib/boardApi";

type PublicUser = { email: string; nickname: string | null; name: string | null; avgPostRating: number; postsCount: number; };

export default function UserProfile() {
  const { email } = useParams();
  const nav = useNavigate();
  const [user, setUser] = useState<PublicUser | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        if (!email) return;
        const data = await getPublicUser(email);
        setUser(data);
      } catch (e) {
        setUser(null);
      } finally {
        setLoading(false);
      }
    })();
  }, [email]);

  if (loading) return <div className="p-4">로딩중…</div>;
  if (!user) return <div className="p-4">존재하지 않는 사용자입니다.</div>;

  const avgStr = (user.avgPostRating ?? 0).toFixed(1);

  return (
    <div className="max-w-3xl mx-auto p-4 space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-xl font-bold">프로필</h1>
      </div>

      <div className="p-4 border rounded space-y-2">
        <div><b>이메일:</b> {user.email}</div>
        <div><b>이름:</b> {user.name ?? "미설정"}</div>
        <div><b>닉네임:</b> {user.nickname ?? "미설정"}</div>
        <div><b>평점: ★</b> X {avgStr / 2} (게시글 {user.postCount ?? 0}개)</div>
      </div>
    </div>
  );
}