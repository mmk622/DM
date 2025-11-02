export type GenderPref = 'ANY' | 'MALE' | 'FEMALE';
export type PartyPref = 'ANY' | 'TWO' | 'THREE' | 'FOUR_PLUS';

export interface PostListItem {
  id: number;
  authorId: number;
  authorNickname: string;
  title: string;
  mealDate: string;     // yyyy-MM-dd
  genderPref: GenderPref;
  partyPref: PartyPref;
  createdAt: string;    // ISO
}

export interface Post extends PostListItem {
  content: string;
  updatedAt: string;
}

export interface Comment {
  id: number;
  postId: number;
  authorId: number;
  authorNickname: string;
  content: string;
  createdAt: string;
}

export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;   // 0-based
  first: boolean;
  last: boolean;
}

export interface PostCreateRequest {
  title: string;
  content: string;
  mealDate: string;     // yyyy-MM-dd
  genderPref: GenderPref;
  partyPref: PartyPref;
}

export interface CommentCreateRequest { content: string; }