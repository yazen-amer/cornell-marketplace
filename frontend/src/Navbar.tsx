import './Navbar.css';
import { Link, useNavigate } from 'react-router-dom';

type NavbarProps = {
  token: string | null;
  setToken: React.Dispatch<React.SetStateAction<string | null>>;
};

const Navbar = ({ token, setToken }: NavbarProps) => {
  const navigate = useNavigate();

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <Link to="/" aria-label="Cornell Marketplace home">
          <span className="brand-mark">CM</span>
          <span className="brand-name">Cornell Marketplace</span>
        </Link>
      </div>
      <div className="navbar-right">
        {token ? (
          <>
            <button className="nav-primary" onClick={() => navigate('/create-listing')}>
              Sell an item
            </button>
            <button
              className="nav-secondary"
              onClick={() => {
                setToken(null);
                localStorage.removeItem('token');
                navigate('/');
              }}
            >
              Log out
            </button>
          </>
        ) : (
          <>
            <Link className="nav-link" to="/login">Log in</Link>
            <Link className="nav-link" to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
