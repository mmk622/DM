import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createPost } from "../lib/boardApi";
import { GenderPref, PartyPref } from "../types/board";

export default function PostCreatePage() {
  const nav = useNavigate();
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [mealDate, setMealDate] = useState("");
  const [genderPref, setGenderPref] = useState<GenderPref>("ANY");
  const [partyPref, setPartyPref] = useState<PartyPref>("ANY");
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    const created = await createPost({ title, content, mealDate, genderPref, partyPref });
    setBusy(false);
    nav(`/posts/${created.id}`);
  };

  return (
    <div className="max-w-2xl mx-auto p-4 space-y-4">
      <div className="mx-auto" style={{ width: "640px", maxWidth: "100%" }}>
        <h1 className="text-xl font-bold">글 작성</h1>
        <form onSubmit={submit} className="space-y-3">
          <div>
            <label className="block text-sm text-gray-600">제목</label>
            <input className="w-full border rounded px-3 py-2" value={title} onChange={e => setTitle(e.target.value)} required />
          </div>
          <div>
            <label className="block text-sm text-gray-600">내용</label>
            <textarea className="w-full border rounded px-3 py-2 h-40" value={content} onChange={e => setContent(e.target.value)} required />
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
            <div>
              <label className="block text-sm text-gray-600">밥 먹을 날짜</label>
              <input type="date" className="w-full border rounded px-3 py-2" value={mealDate} onChange={e => setMealDate(e.target.value)} required />
            </div>
            <div>
              <label className="block text-sm text-gray-600">선호 성별</label>
              <select className="w-full border rounded px-3 py-2" value={genderPref} onChange={e => setGenderPref(e.target.value as any)}>
                <option value="ANY">상관없음</option>
                <option value="MALE">남자</option>
                <option value="FEMALE">여자</option>
              </select>
            </div>
            <div>
              <label className="block text-sm text-gray-600">선호 인원수</label>
              <select className="w-full border rounded px-3 py-2" value={partyPref} onChange={e => setPartyPref(e.target.value as any)}>
                <option value="ANY">상관없음</option>
                <option value="TWO">2명</option>
                <option value="THREE">3명</option>
                <option value="FOUR_PLUS">4명 이상</option>
              </select>
            </div>
          </div>
          <div className="flex gap-2">
            <button type="button" className="px-3 py-2 rounded border" onClick={() => nav(-1)}>취소</button>
            <button disabled={busy} className="px-3 py-2 rounded bg-indigo-600 text-white disabled:opacity-50">{busy ? "등록 중…" : "등록"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}