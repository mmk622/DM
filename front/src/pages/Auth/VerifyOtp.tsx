import { useState } from 'react'; import api from '../../lib/api';
export default function VerifyOtp() {
  const [email, setEmail] = useState(''); const [code, setCode] = useState('');
  const [name, setName] = useState(''); const [gender, setGender] = useState('');
  const verify = async () => {
    const { data } = await api.post('/api/auth/verify', { email, code, name, gender });
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    alert('로그인 성공');
  };
  return (
    <div className="p-6 max-w-md mx-auto">
      <h1 className="text-2xl font-bold">코드 확인</h1>
      <input className="mt-3 border p-2 w-full" placeholder="email" value={email} onChange={e => setEmail(e.target.value)} />
      <input className="mt-3 border p-2 w-full" placeholder="6자리 코드" value={code} onChange={e => setCode(e.target.value)} />
      <input className="mt-3 border p-2 w-full" placeholder="이름" value={name} onChange={e => setName(e.target.value)} />
      <input className="mt-3 border p-2 w-full" placeholder="성별(optional)" value={gender} onChange={e => setGender(e.target.value)} />
      <button className="btn mt-4 border px-4 py-2" onClick={verify}>확인</button>
    </div>
  );
}