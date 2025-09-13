import { useState } from 'react'; import api from '../../lib/api';
export default function Login() {
  const [email, setEmail] = useState(''); const [sent, setSent] = useState(false);
  const send = async () => { await api.post('/api/auth/otp', { email }); setSent(true); };
  return (
    <div className="p-6 max-w-md mx-auto">
      <h1 className="text-2xl font-bold">이메일 로그인</h1>
      <input className="mt-4 input input-bordered w-full border p-2" placeholder="@dongguk.ac.kr" value={email} onChange={e => setEmail(e.target.value)} />
      <button className="btn mt-3 border px-4 py-2" onClick={send}>코드 받기</button>
      {sent && <p className="mt-2 text-green-600">메일함( http://localhost:8025 )에서 코드를 확인하세요.</p>}
    </div>
  );
}