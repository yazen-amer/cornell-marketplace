import { BrowserRouter as Router, Route, Routes, Navigate, Outlet } from 'react-router-dom';
import ListingDetails from './ListingDetails.tsx';
import { HomePage } from './Homepage.tsx';
import CreateListing from './CreateListing.tsx';
import Login from './Login.tsx';
import Register from './Register.tsx';
import { useState, useEffect } from 'react';
import Navbar from './Navbar.tsx';
import EditListing from './EditListing.tsx';
import './App.css';

export type UserDto = { id: number; username: string; };

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [currentUser, setCurrentUser] = useState<UserDto | null>(null);
  const [authLoading, setAuthLoading] = useState(true);

  useEffect(() => {
    if (!token) {
      setCurrentUser(null);
      setAuthLoading(false);
      return;
    }

    fetch('http://localhost:8080/users/me', {
      method: 'GET',
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((response) => {
        if (!response.ok) {
          setCurrentUser(null);
          setToken(null);
          localStorage.removeItem('token');
          setAuthLoading(false);
          return;
        }
        return response.json() as Promise<UserDto>;
      })
      .then((data) => {
        if (data) {
          setCurrentUser(data);
          setAuthLoading(false);
        }
      });
  }, [token]);

  const PrivateRoutes = () => {
    if (authLoading) return <div className="loading-state">Checking your session…</div>;
    return currentUser ? <Outlet /> : <Navigate to="/login" />;
  };

  return (
    <Router>
      <div className="app-shell">
        <Navbar token={token} setToken={setToken} />
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/listings/:id" element={<ListingDetails currentUser={currentUser} token={token} />} />
          <Route element={<PrivateRoutes />}>
            <Route path="/create-listing" element={<CreateListing token={token} />} />
            <Route path="/listings/:id/edit" element={<EditListing token={token} />} />
          </Route>
          <Route path="/login" element={<Login setToken={setToken} />} />
          <Route path="/register" element={<Register setToken={setToken} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
