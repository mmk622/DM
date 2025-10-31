import { Comment } from "../types/board";

export default function CommentList({ items }: { items: Comment[] }) {
  return (
    <div className="border rounded">
      {items.map(c => (
        <div key={c.id} className="p-3 border-b">
          <div className="text-sm text-gray-500">작성자 #{c.authorId} • {new Date(c.createdAt).toLocaleString()}</div>
          <div>{c.content}</div>
        </div>
      ))}
      {items.length === 0 && <div className="p-3 text-gray-500">댓글이 없습니다.</div>}
    </div>
  );
}