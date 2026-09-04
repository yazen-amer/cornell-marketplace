import { API_URL } from './config';
import { useState } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';

type VerifyProps = { setToken: React.Dispatch<React.SetStateAction<string | null>> };

export default function Verify({ setToken }: VerifyProps) {
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState((location.state as { email?: string } | null)?.email ?? '');
  const [code, setCode] = useState('');
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');

  const submitCode = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    fetch(`${API_URL}/auth/verify`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, code }),
    })
      .then(async (response) => {
        const data = await response.json();
        if (!response.ok) throw new Error(data.error || 'That code is invalid or has expired.');
        return data;
      })
      .then((data) => {
        localStorage.setItem('token', data.token);
        setToken(data.token);
        navigate('/');
      })
      .catch((err) => setError(err.message));
  };

  const resendCode = () => {
    setError('');
    setInfo('');
    fetch(`${API_URL}/auth/resend-verification`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email }),
    })
      .then(async (response) => {
        const data = await response.json();
        if (!response.ok) throw new Error(data.error || 'Could not resend code.');
        return data;
      })
      .then((data) => setInfo(data.message))
      .catch((err) => setError(err.message));
  };

  return (
    <main className="auth-wrap">
      <form className="form-card auth-card" onSubmit={submitCode}>
        <div className="form-header">
          <p className="eyebrow">Almost there</p>
          <h1>Verify your Cornell email</h1>
          <p className="page-subtitle">We sent a 6-digit code to your @cornell.edu inbox.</p>
        </div>
        <div className="form-grid">
          <div className="form-field">
            <label htmlFor="Email">Email</label>
            <input type="email" id="Email" autoComplete="email" placeholder="you@cornell.edu" value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="Code">Verification code</label>
            <input type="text" id="Code" inputMode="numeric" maxLength={6} placeholder="123456" value={code} onChange={(e) => setCode(e.target.value)} />
          </div>
          {error && <p className="muted" style={{ color: 'crimson' }}>{error}</p>}
          {info && <p className="muted">{info}</p>}
          <button className="button button-primary" type="submit">Verify account</button>
          <p className="muted">
            Didn't get a code?{' '}
            <button type="button" onClick={resendCode} className="button-link">Resend it</button>
          </p>
          <p className="muted">Already verified? <Link to="/login"><strong>Log in</strong></Link></p>
        </div>
      </form>
    </main>
  );
}
