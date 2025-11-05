import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  getPost,
  listComments,
  addComment,
  deletePost as apiDeletePost,
  deleteComment as apiDeleteComment,
  getMe,
  ratePost
} from "../lib/boardApi";
import { Post, Comment } from "../types/board";
import CommentForm from "../components/CommentForm";

export default function PostDetailPage() {
  const { id } = useParams();
  const postId = Number(id);
  const nav = useNavigate();

  const [post, setPost] = useState<Post | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);
  const [meEmail, setMeEmail] = useState<string | null>(null);
  const [myRating, setMyRating] = useState<number | null>(null);

  const isAuthor = useMemo(() => {
    if (!post || !meEmail) return false;
    // 혹시 대소문자 섞임 방지
    return String(post.authorId).toLowerCase() === String(meEmail).toLowerCase();
  }, [post, meEmail]);

  const isMyComment = (c: Comment) => !!meEmail && String(c.authorId).toLowerCase() === String(meEmail).toLowerCase();

  const load = async () => {
    setLoading(true);
    const [me, p, c] = await Promise.all([
      getMe().catch(() => null),
      getPost(postId),
      listComments(postId, 0, 100),
    ]);
    setMeEmail(me?.email || null);
    setPost(p);
    setComments(c.content);
    setMyRating(p.myRating ?? null);
    setLoading(false);
  };

  // 평점 선택 핸들러
  const onRate = async (v: number) => {
    const score = Math.round(v * 2); // 0~5 → 0~10
    await ratePost(postId, score);
    await load();
  };

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [postId]);

  const onChat = () => nav(`/chat/${post?.authorId}`); // 프로젝트 규칙
  const onAddComment = async (content: string, secret: boolean) => {
    await addComment(postId, { content, secret });
    await load();
  };

  const onDeletePost = async () => {
    if (!post) return;
    if (!isAuthor) {
      alert("본인 글만 삭제할 수 있습니다.");
      return;
    }
    if (!confirm("정말 이 게시글을 삭제하시겠습니까?")) return;

    await apiDeletePost(post.id);
    alert("삭제되었습니다.");
    nav(-1); // 목록으로
  };

  const onDeleteComment = async (comment: Comment) => {
    if (!isAuthor) {
      alert("본인 댓글만 삭제할 수 있습니다.");
      return;
    }
    if (!confirm("이 댓글을 삭제할까요?")) return;

    await apiDeleteComment(postId, comment.id);
    await load();
  };

  if (loading) return <div className="p-4">로딩중…</div>;
  if (!post) return <div className="p-4">존재하지 않는 게시글입니다.</div>;

  return (
    <div className="max-w-3xl mx-auto p-4 space-y-4">
      <div className="flex justify-between items-start">
        <h1 className="text-xl font-bold">{post.title}</h1>
        <div className="flex gap-2">
          <button className="px-3 py-2 rounded border" onClick={() => nav(-1)}>
            목록
          </button>

          {/* 작성자만 삭제 버튼 노출 */}
          {isAuthor && (
            <button
              className="px-3 py-2 rounded bg-red-600 text-white"
              onClick={onDeletePost}
            >
              삭제
            </button>
          )}
        </div>
      </div>

      <div className="text-sm text-gray-700">
        글쓴이:
        <button
          className="underline underline-offset-2 hover:opacity-80"
          onClick={() => post.authorId && nav(`/u/${encodeURIComponent(String(post.authorId))}`)}
        >
          {post.authorNickname ?? post.authorId ?? "알 수 없음"}
        </button>
      </div>

      <div className="text-sm text-gray-600">
        날짜 {post.mealDate} · 성별 {post.genderPref} · 인원 {post.partyPref} · 평점 ★ {(post.avgRating ?? 0) / 2} / 5 ({post.ratingsCount ?? 0}명)
      </div>
      <div className="p-4 border rounded whitespace-pre-wrap">{post.content}</div>

      <div className="flex items-center gap-2">
        <span className="text-sm text-gray-600">내 평점</span>
        <select
          className="border rounded px-2 py-1"
          value={myRating != null ? (myRating / 2).toString() : ""}
          onChange={(e) => {
            const v = Number(e.target.value);
            if (!Number.isNaN(v)) onRate(v);
          }}
        >
          <option value="">선택</option>
          {Array.from({ length: 11 }).map((_, i) => {
            const v = i * 0.5;
            return (
              <option key={v} value={v}>
                {v} / 5
              </option>
            );
          })}
        </select>
      </div>

      <div className="space-y-2">
        <h2 className="font-semibold">댓글</h2>
        <CommentForm onSubmit={onAddComment} />

        {/* 댓글 목록 (삭제 버튼은 본인에게만) */}
        <ul className="space-y-2">
          {comments.map((c) => {
            const myComment =
              meEmail && String(c.authorId).toLowerCase() === meEmail.toLowerCase();
            return (
              <li
                key={c.id}
                className="p-3 border rounded flex justify-between items-start gap-2"
              >
                <div className="whitespace-pre-wrap">
                  <div className="text-xs text-gray-500">
                    <button
                      className="underline underline-offset-2 hover:opacity-80 mr-1"
                      onClick={() => c.authorId && nav(`/u/${encodeURIComponent(String(c.authorId))}`)}
                    >
                      {c.authorNickname ?? c.authorId ?? "알 수 없음"}
                    </button>
                    · {c.createdAt}
                  </div>
                  <div>{c.content}</div>
                </div>
                {myComment && (
                  <button
                    className="px-2 py-1 text-sm rounded bg-red-500 text-white shrink-0"
                    onClick={() => onDeleteComment(c)}
                  >
                    삭제
                  </button>
                )}
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  );
}