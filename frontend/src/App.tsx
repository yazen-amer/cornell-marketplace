import { readJsonResponse } from './api';
import { API_URL } from './config';
import { BrowserRouter as Router, Route, Routes, Navigate, Outlet } from 'react-router-dom';
import ListingDetails from './ListingDetails.tsx';
import { HomePage } from './Homepage.tsx';
import CreateListing from './CreateListing.tsx';
import Login from './Login.tsx';
import Register from './Register.tsx';
import Verify from './Verify.tsx';
import { useState, useEffect } from 'react';
import Navbar from './Navbar.tsx';
import EditListing from './EditListing.tsx';
import './App.css';

export type UserDto = { id: number; username: string; };

function PrivateRoutes({ loading, user, error }: { loading: boolean; user: UserDto | null; error: string | null }) {
  if (loading) return <div className="loading-state">Checking your session?</div>;
  if (error) return <div role="alert" className="loading-state">{error} <button onClick={() => window.location.reload()}>Retry</button></div>;
  return user ? <Outlet /> : <Navigate to="/login" />;
}

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [session, setSession] = useState<{ token: string; user: UserDto | null; error: string | null } | null>(null);
  const currentUser = token && session?.token === token ? session.user : null;
  const authLoading = Boolean(token && session?.token !== token);
  const authError = token && session?.token === token ? session.error : null;

  useEffect(() => {
    if (!token) return;
    const controller = new AbortController();
    fetch(`${API_URL}/users/me`, {
      headers: { Authorization: `Bearer ${token}` },
      signal: controller.signal,
    })
      .then(async (response) => {
        if (response.status === 401 || response.status === 403) {
          if (controller.signal.aborted) return;
          localStorage.removeItem('token');
          setToken(null);
          return;
        }
        const user = await readJsonResponse<UserDto>(response, 'Could not check your session');
        if (!controller.signal.aborted) setSession({ token, user, error: null });
      })
      .catch((err: unknown) => {
        if (!controller.signal.aborted) setSession({ token, user: null, error: err instanceof Error ? err.message : 'Could not check your session.' });
      });
    return () => controller.abort();
  }, [token]);

  return (
    <Router>
      <div className="app-shell">
        <Navbar token={token} setToken={setToken} />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/listings/:id" element={<ListingDetails currentUser={currentUser} token={token} />} />
          <Route element={<PrivateRoutes loading={authLoading} user={currentUser} error={authError} />}>
            <Route path="/create-listing" element={<CreateListing token={token} />} />
            <Route path="/listings/:id/edit" element={<EditListing token={token} />} />
          </Route>
          <Route path="/login" element={<Login setToken={setToken} />} />
          <Route path="/register" element={<Register setToken={setToken} />} />
          <Route path="/verify" element={<Verify setToken={setToken} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
