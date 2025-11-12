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
    <div className="min-h-screen bg-gradient-to-b from-gray-50 to-white pt-[76px] pb-10 px-4 sm:px-6 lg:px-8">
      {/* 원래 비어 있던 제목 유지 */}
      <div className="mb-5 flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-gray-800 tracking-tight">
          글 목록
        </h1>
      </div>

      {/* 필터 카드: 밝은 톤 + 미세 그림자 */}
      <div className="rounded-2xl border border-gray-200 bg-white/95 p-4 shadow-sm hover:shadow-md transition-shadow">
        <PostFilters initial={{}} onChange={(flt) => load({ ...flt, page: 0 })} />
      </div>

      {/* 목록 */}
      <div className="mt-6 space-y-3">
        {loading && (
          <div className="rounded-2xl border border-gray-200 bg-white p-6 text-center text-sm text-gray-500 animate-pulse">
            로딩중…
          </div>
        )}

        {!loading && page?.content.map((post) => (
          <button
            key={post.id}
            onClick={() => nav(`/posts/${post.id}`)}
            className="
              group w-full text-left rounded-2xl border border-gray-200 bg-white p-5
              shadow-sm transition-all duration-200
              hover:shadow-md hover:border-indigo-200
              focus:outline-none focus-visible:ring-2 focus-visible:ring-indigo-300 focus-visible:ring-offset-2
              active:scale-[0.998]
            "
          >
            {/* 제목 */}
            <div className="text-base font-semibold text-gray-900 truncate group-hover:text-indigo-600 transition-colors">
              {post.title}
            </div>

            {/* 메타: 라벨+칩 조합(텍스트는 동일) */}
            <div className="mt-2 flex flex-wrap items-center gap-2 text-[13px]">
              <span className="inline-flex items-center gap-1 rounded-md bg-gray-100 px-2 py-1 text-gray-700">
                <span className="opacity-70">📅날짜: </span>
                <span className="font-medium tabular-nums">{post.mealDate}</span>
              </span>
              <span className="inline-flex items-center gap-1 rounded-md bg-gray-100 px-2 py-1 text-gray-700">
                <span className="opacity-70">🚻성별: </span>
                <span className="font-medium">{labelGender(post.genderPref)}</span>
              </span>
              <span className="inline-flex items-center gap-1 rounded-md bg-gray-100 px-2 py-1 text-gray-700">
                <span className="opacity-70">👥인원: </span>
                <span className="font-medium">{labelParty(post.partyPref)}</span>
              </span>
            </div>

            {/* 하이라이트 바(순수 스타일) */}
            <div className="mt-3 h-0.5 w-0 bg-gradient-to-r from-indigo-500 to-sky-500 transition-all duration-300 group-hover:w-full" aria-hidden />
          </button>
        ))}

        {!loading && page && page.content.length === 0 && (
          <div className="rounded-2xl border border-dashed border-gray-300 bg-white py-10 text-center text-sm text-gray-500">
            게시글이 없습니다.
          </div>
        )}
      </div>

      {/* 글쓰기: 위치/동작 동일, 현대적 대비만 강화 */}
      <div className="mt-8 flex justify-end">
        <button
          onClick={() => nav("/posts/new")}
          className="
            inline-flex h-11 items-center justify-center rounded-xl px-5 text-sm font-semibold text-white
            bg-gradient-to-r from-indigo-600 to-sky-500
            shadow-md hover:shadow-lg active:shadow-sm transition-shadow
            focus:outline-none focus-visible:ring-2 focus-visible:ring-indigo-300 focus-visible:ring-offset-2
          "
        >
          ✏️ 글쓰기
        </button>
      </div>

      {/* 페이지네이션: 버튼은 그대로, 톤/간격만 업그레이드 */}
      <div className="mt-8 flex items-center justify-center gap-3">
        <button
          disabled={!page || page.first}
          onClick={() => load({ page: (page!.number - 1) })}
          className="
            inline-flex h-9 items-center justify-center rounded-lg px-3 text-sm font-medium
            border border-gray-300 bg-white text-gray-700
            hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50
            focus:outline-none focus-visible:ring-2 focus-visible:ring-indigo-300 focus-visible:ring-offset-2
          "
        >
          ← 이전
        </button>
        <div className="rounded-md border border-gray-200 bg-white px-3 py-1 text-sm tabular-nums text-gray-700 shadow-sm">
          {page ? page.number + 1 : 0} / {page?.totalPages ?? 0}
        </div>
        <button
          disabled={!page || page.last}
          onClick={() => load({ page: (page!.number + 1) })}
          className="
            inline-flex h-9 items-center justify-center rounded-lg px-3 text-sm font-medium
            border border-gray-300 bg-white text-gray-700
            hover:bg-gray-50 disabled:cursor-not-allowed disabled:opacity-50
            focus:outline-none focus-visible:ring-2 focus-visible:ring-indigo-300 focus-visible:ring-offset-2
          "
        >
          다음 →
        </button>
      </div>
    </div>
  );
}

function labelGender(g: GenderPref) { return g === 'ANY' ? '상관없음' : g === 'MALE' ? '남자' : '여자'; }
function labelParty(p: PartyPref) { return p === 'ANY' ? '상관없음' : p === 'TWO' ? '2명' : p === 'THREE' ? '3명' : '4명 이상'; }