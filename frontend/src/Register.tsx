import { API_URL } from './config';
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

type RegisterProps = { setToken: React.Dispatch<React.SetStateAction<string | null>> };

const CORNELL_EMAIL_SUFFIX = '@cornell.edu';

export default function Register({ setToken }: RegisterProps) {
  void setToken; // no longer set on register — account must be verified first
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  return (
    <main className="auth-wrap">
      <form className="form-card auth-card" onSubmit={(e) => {
        e.preventDefault();
        setError('');

        if (!email.toLowerCase().endsWith(CORNELL_EMAIL_SUFFIX)) {
          setError('Please use your @cornell.edu email to register.');
          return;
        }

        fetch(`${API_URL}/auth/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username, email, password }),
        })
          .then(async (response) => {
            const data = await response.json();
            if (!response.ok) throw new Error(data.error || 'Could not create your account.');
            return data;
          })
          .then((data) => {
            navigate('/verify', { state: { email: data.email } });
          })
          .catch((err) => setError(err.message));
      }}>
        <div className="form-header">
          <p className="eyebrow">Join the marketplace</p>
          <h1>Create account</h1>
          <p className="page-subtitle">Start buying and selling with the Cornell community.</p>
        </div>
        <div className="form-grid">
          <div className="form-field">
            <label htmlFor="Username">Username</label>
            <input type="text" id="Username" autoComplete="username" value={username} onChange={(e) => setUsername(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="Email">Email</label>
            <input type="email" id="Email" autoComplete="email" placeholder="you@cornell.edu" value={email} onChange={(e) => setEmail(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="Password">Password</label>
            <input type="password" id="Password" autoComplete="new-password" value={password} onChange={(e) => setPassword(e.target.value)} />
          </div>
          {error && <p className="muted" style={{ color: 'crimson' }}>{error}</p>}
          <button className="button button-primary" type="submit">Create account</button>
          <p className="muted">Already have an account? <Link to="/login"><strong>Log in</strong></Link></p>
        </div>
      </form>
    </main>
  );
}
