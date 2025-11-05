import api from "./api";
import {
  SpringPage, PostListItem, Post, Comment,
  PostCreateRequest, CommentCreateRequest,
  GenderPref, PartyPref
} from "../types/board";

export type ListParams = {
  keyword?: string;
  date?: string; // yyyy-MM-dd
  genderPref?: GenderPref;
  partyPref?: PartyPref;
  page?: number;
  size?: number;
};

export async function listPosts(params: ListParams) {
  const { data } = await api.get<SpringPage<PostListItem>>("/api/posts", { params });
  return data;
}

export async function getPost(id: number) {
  const { data } = await api.get<Post>(`/api/posts/${id}`);
  return data;
}

export async function createPost(body: PostCreateRequest) {
  const { data } = await api.post<Post>("/api/posts", body);
  return data;
}

export async function listComments(postId: number, page = 0, size = 50) {
  const { data } = await api.get<SpringPage<Comment>>(`/api/posts/${postId}/comments`, { params: { page, size } });
  return data;
}

export async function addComment(postId: number, body: CommentCreateRequest) {
  const { data } = await api.post<Comment>(`/api/posts/${postId}/comments`, body);
  return data;
}

export async function deletePost(id: number) {
  return api.delete(`/api/posts/${id}`);
}

export async function deleteComment(postId: number, commentId: number) {
  return api.delete(`/api/posts/${postId}/comments/${commentId}`);
}

export async function getMe() {
  // MeResponse 예: { id: number, email: string, name?: string, nickname?: string ... }
  return api.get("/api/users/me").then(res => res.data);
}

// 🔹 공개 프로필 조회 (/api/users/{email})
export async function getPublicUser(email: string) {
  const res = await api.get(`/api/users/${encodeURIComponent(email)}`);
  return res.data as {
    email: string;
    nickname: string | null;
    name: string | null;
  };
}

export async function ratePost(postId: number, score: number) {
  const { data } = await api.post(`/api/posts/${postId}/rating`, { score });
  return data as { postId: number; myScore: number | null };
}