import { useState } from "react";

export default function CommentForm({ onSubmit }: { onSubmit: (content: string, secret: boolean) => Promise<void> | void }) {
  const [content, setContent] = useState("");
  const [secret, setSecret] = useState(false);
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!content.trim()) return;
    setBusy(true);
    try {
      await onSubmit(content.trim(), secret); // 비밀댓글 여부 함께 전달
      setContent("");
      setSecret(false); // 등록 후 체크박스 초기화
    } finally {
      setBusy(false);
    }
  };

  return (
    <form onSubmit={submit} className="flex items-center gap-2 flex-wrap">
      {/* 비밀댓글 체크박스 */}
      <label className="flex items-center gap-1 text-sm text-gray-700">
        <input
          type="checkbox"
          checked={secret}
          onChange={(e) => setSecret(e.target.checked)}
          disabled={busy}
        />
        익명
      </label>

      {/* 댓글 입력창 */}
      <input
        className="flex-1 border rounded px-2 py-2"
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder="댓글을 입력하세요"
        disabled={busy}
      />

      {/* 등록 버튼 */}
      <button
        disabled={busy}
        className="px-3 py-2 rounded bg-black text-white disabled:opacity-50"
      >
        {busy ? "등록 중…" : "등록"}
      </button>
    </form>
  );
}