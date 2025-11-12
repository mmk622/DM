import { Outlet } from "react-router-dom";
import Header from "./components/Header";

export default function App() {
  return (
    <>
      <Header />
      <main
        className="min-h-screen bg-gradient-to-b from-gray-50 to-white pt-[60px] pb-16 px-6 sm:px-8 lg:px-12"
      >
        <div className="max-w-2xl mx-auto">
          <Outlet />
        </div>
      </main>
    </>
  );
}