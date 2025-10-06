import React from "react";
import { createBrowserRouter } from "react-router-dom";
import App from "./App";
import Login from "./pages/Login";
import Profile from "./pages/Profile";
import CompleteProfile from "./pages/CompleteProfile";
// 필요 시
// import Signup from "./pages/Signup";

export const router = createBrowserRouter([
  { path: "/", element: <App /> },

  // 화면별 라우트
  { path: "/login", element: <Login /> },
  { path: "/profile", element: <Profile /> },
  { path: "/complete-profile", element: <CompleteProfile /> },
  // { path: "/signup", element: <Signup /> }, // 쓰면 활성화
]);