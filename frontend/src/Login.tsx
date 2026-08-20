import { API_URL } from './config';
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

type LoginProps = { setToken: React.Dispatch<React.SetStateAction<string | null>> };

export default function Login({ setToken }: LoginProps) {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  return (
    <main className="auth-wrap">
      <form className="form-card auth-card" onSubmit={(e) => {
        e.preventDefault();
        fetch(`${API_URL}/auth/login`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ email, password }),
        })
          .then((response) => {
            if (!response.ok) throw new Error('Invalid email or password');
            return response.json();
          })
          .then((data) => {
            localStorage.setItem('token', data.token);
            setToken(data.token);
            navigate('/');
          })
          .catch((error) => console.log(error.message));
      }}>
        <div className="form-header">
          <p className="eyebrow">Welcome back</p>
          <h1>Log in</h1>
          <p className="page-subtitle">Access your listings and post new items.</p>
        </div>
        <div className="form-grid">
          <div className="form-field">
            <label htmlFor="Email">Email</label>
            <input type="email" id="Email" autoComplete="email" placeholder="you@cornell.edu" value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="Password">Password</label>
            <input type="password" id="Password" autoComplete="current-password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </div>
          <button className="button button-primary" type="submit">Log in</button>
          <p className="muted">New here? <Link to="/register"><strong>Create an account</strong></Link></p>
        </div>
      </form>
    </main>
  );
}
