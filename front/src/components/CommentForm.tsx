import { useState } from "react";

export default function CommentForm({ onSubmit }: { onSubmit: (content: string) => Promise<void> | void }) {
  const [content, setContent] = useState("");
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!content.trim()) return;
    setBusy(true);
    await onSubmit(content.trim());
    setContent("");
    setBusy(false);
  };

  return (
    <form onSubmit={submit} className="flex gap-2">
      <input className="flex-1 border rounded px-2 py-2" value={content} onChange={e => setContent(e.target.value)} placeholder="댓글을 입력하세요" />
      <button disabled={busy} className="px-3 py-2 rounded bg-black text-white disabled:opacity-50">등록</button>
    </form>
  );
}