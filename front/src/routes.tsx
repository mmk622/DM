import { createBrowserRouter } from "react-router-dom";
import App from "./App";
import Main from "./pages/Main";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Profile from "./pages/Profile";
import CompleteProfile from "./pages/CompleteProfile";
import PostsPage from "./pages/PostsPage";
import PostDetailPage from "./pages/PostDetailPage";
import PostCreatePage from "./pages/PostCreatePage";
import RequireAuth from "./components/RequireAuth";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      { index: true, element: <Main /> }, // ← 루트(/)에서 메인 화면
      { path: "login", element: <Login /> },
      { path: "signup", element: <Signup /> },
      { path: "profile", element: <Profile /> },
      { path: "complete-profile", element: <CompleteProfile /> },
      { path: "posts", element: <PostsPage /> }, // 목록/검색
      { path: "posts/:id", element: <PostDetailPage /> }, // 상세
      {
        path: "posts/new", element: (
          <RequireAuth>
            <PostCreatePage />
          </RequireAuth>
        )
      },
    ],
  },
]);