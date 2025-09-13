import { createBrowserRouter } from 'react-router-dom';
import App from './App';
import Login from './pages/Auth/Login';
import VerifyOtp from './pages/Auth/VerifyOtp';

export const router = createBrowserRouter([
  { path: '/', element: <App /> },
  { path: '/login', element: <Login /> },
  { path: '/verify', element: <VerifyOtp /> },
]);