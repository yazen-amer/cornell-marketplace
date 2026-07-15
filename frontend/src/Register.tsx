import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

type RegisterProps = { setToken: React.Dispatch<React.SetStateAction<string | null>> };

export default function Register({ setToken }: RegisterProps) {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  return (
    <main className="auth-wrap">
      <form className="form-card auth-card" onSubmit={(e) => {
        e.preventDefault();
        fetch('http://localhost:8080/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username, email, password }),
        })
          .then((response) => {
            if (!response.ok) throw new Error('Invalid username, email or password');
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
          <button className="button button-primary" type="submit">Create account</button>
          <p className="muted">Already have an account? <Link to="/login"><strong>Log in</strong></Link></p>
        </div>
      </form>
    </main>
  );
}
