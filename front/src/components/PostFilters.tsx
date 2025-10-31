import { useState } from "react";
import { GenderPref, PartyPref } from "../types/board";

type Props = {
  initial?: { keyword?: string; date?: string; genderPref?: GenderPref; partyPref?: PartyPref; };
  onChange: (next: Props["initial"]) => void;
};

export default function PostFilters({ initial, onChange }: Props) {
  const [keyword, setKeyword] = useState(initial?.keyword ?? "");
  const [date, setDate] = useState(initial?.date ?? "");
  const [genderPref, setGenderPref] = useState<GenderPref | "">(initial?.genderPref ?? "");
  const [partyPref, setPartyPref] = useState<PartyPref | "">(initial?.partyPref ?? "");

  const apply = () => onChange({
    keyword: keyword || undefined,
    date: date || undefined,
    genderPref: (genderPref as GenderPref) || undefined,
    partyPref: (partyPref as PartyPref) || undefined
  });

  return (
    <div className="flex flex-wrap gap-2 items-end">
      <div className="flex flex-col">
        <label className="text-sm text-gray-500">키워드</label>
        <input className="border rounded px-2 py-1" value={keyword} onChange={e => setKeyword(e.target.value)} placeholder="제목 검색" />
      </div>
      <div className="flex flex-col">
        <label className="text-sm text-gray-500">밥 먹을 날짜</label>
        <input type="date" className="border rounded px-2 py-1" value={date} onChange={e => setDate(e.target.value)} />
      </div>
      <div className="flex flex-col">
        <label className="text-sm text-gray-500">선호 성별</label>
        <select className="border rounded px-2 py-1" value={genderPref} onChange={e => setGenderPref(e.target.value as any)}>
          <option value="">전체</option>
          <option value="ANY">상관없음</option>
          <option value="MALE">남자</option>
          <option value="FEMALE">여자</option>
        </select>
      </div>
      <div className="flex flex-col">
        <label className="text-sm text-gray-500">선호 인원수</label>
        <select className="border rounded px-2 py-1" value={partyPref} onChange={e => setPartyPref(e.target.value as any)}>
          <option value="">전체</option>
          <option value="ANY">상관없음</option>
          <option value="TWO">2명</option>
          <option value="THREE">3명</option>
          <option value="FOUR_PLUS">4명 이상</option>
        </select>
      </div>
      <button className="ml-2 px-3 py-2 rounded bg-black text-white" onClick={apply}>검색</button>
    </div>
  );
}