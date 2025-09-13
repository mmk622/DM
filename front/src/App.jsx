import { useEffect, useState } from 'react';
import api from './lib/api';

export default function App() {
  const [msg, setMsg] = useState('');
  useEffect(() => { api.get('/api/hello').then(r => setMsg(r.data.message)); }, []);
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="p-8 rounded-2xl shadow bg-white">
        <h1 className="text-2xl font-bold">Dongguk Mealmate</h1>
        <p className="mt-2 text-gray-600">Backend says: <b>{msg}</b></p>
      </div>
    </div>
  );
}