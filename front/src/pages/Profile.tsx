// front/src/pages/Profile.tsx
import { useEffect, useState } from "react";
import api from "../lib/api";
export default function Profile() {
  const [me, setMe] = useState<any>(null);
  useEffect(() => { api.get("/api/users/me").then(r => setMe(r.data)); }, []);
  if (!me) return <p>Loading...</p>;
  return <pre className="p-4">{JSON.stringify(me, null, 2)}</pre>;
}