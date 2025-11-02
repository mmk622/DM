import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { listPosts } from "../lib/boardApi";
import { PostListItem, SpringPage, GenderPref, PartyPref } from "../types/board";
import PostFilters from "../components/PostFilters";

export default function PostsPage() {
  const nav = useNavigate();
  const [page, setPage] = useState<SpringPage<PostListItem> | null>(null);
  const [params, setParams] = useState<{ keyword?: string; date?: string; genderPref?: GenderPref; partyPref?: PartyPref; page?: number; size?: number; }>({ page: 0, size: 10 });
  const [loading, setLoading] = useState(false);

  const load = async (override?: Partial<typeof params>) => {
    setLoading(true);
    const merged = { ...params, ...override };
    const data = await listPosts(merged);
    setPage(data);
    setParams(merged);
    setLoading(false);
  };

  useEffect(() => { load(); }, []);

  return (
    <div className="max-w-3xl mx-auto p-4">
      <div className="flex justify-between items-center mb-3">
        <h1 className="text-xl font-bold"></h1>
      </div>

      <PostFilters initial={{}} onChange={(flt) => load({ ...flt, page: 0 })} />

      <div className="mt-4 border rounded">
        {loading && <div className="p-4">로딩중…</div>}
        {!loading && page?.content.map(post => (
          <button key={post.id} className="w-full text-left p-4 border-b hover:bg-gray-50" onClick={() => nav(`/posts/${post.id}`)}>
            <div className="font-medium">{post.title}</div>
            <div className="text-sm text-gray-600 flex gap-2">
              <span>날짜 {post.mealDate}</span>
              <span>| 성별 {labelGender(post.genderPref)}</span>
              <span>| 인원 {labelParty(post.partyPref)}</span>
            </div>
          </button>
        ))}
        {!loading && page && page.content.length === 0 && <div className="p-4 text-gray-500">게시글이 없습니다.</div>}
      </div>

      <button className="px-3 py-2 rounded bg-indigo-600 text-white" onClick={() => nav("/posts/new")}>글쓰기</button>

      <div className="flex gap-2 items-center justify-center mt-4">
        <button className="px-3 py-1 border rounded disabled:opacity-50" disabled={!page || page.first} onClick={() => load({ page: (page!.number - 1) })}>이전</button>
        <div>{page ? page.number + 1 : 0} / {page?.totalPages ?? 0}</div>
        <button className="px-3 py-1 border rounded disabled:opacity-50" disabled={!page || page.last} onClick={() => load({ page: (page!.number + 1) })}>다음</button>
      </div>
    </div>
  );
}

function labelGender(g: GenderPref) { return g === 'ANY' ? '상관없음' : g === 'MALE' ? '남자' : '여자'; }
function labelParty(p: PartyPref) { return p === 'ANY' ? '상관없음' : p === 'TWO' ? '2명' : p === 'THREE' ? '3명' : '4명 이상'; }