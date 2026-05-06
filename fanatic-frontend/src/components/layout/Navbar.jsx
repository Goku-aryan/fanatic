import { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { FaFilm, FaTv, FaBook, FaTrophy, FaBars, FaTimes, FaUser, FaSignOutAlt, FaCog } from 'react-icons/fa'
import LevelBadge from '../common/LevelBadge'
import '../../styles/components.css'

const Navbar = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [mobileOpen, setMobileOpen] = useState(false)
  const [dropdownOpen, setDropdownOpen] = useState(false)

  const navLinks = [
    { path: '/movies', label: 'Movies', icon: <FaFilm /> },
    { path: '/series', label: 'Series', icon: <FaTv /> },
    { path: '/books', label: 'Books', icon: <FaBook /> },
    { path: '/leaderboard', label: 'Leaderboard', icon: <FaTrophy /> },
    { path: '/first-look', label: '🔥 First Look', icon: null }
  ]

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <nav className="navbar">
      <div className="nav-container">
        {/* Logo */}
        <Link to="/" className="nav-logo">
          <span className="logo-icon">🎬</span>
          <span className="logo-text">FANATIC</span>
        </Link>

        {/* Desktop Links */}
        <div className={`nav-links ${mobileOpen ? 'active' : ''}`}>
          {navLinks.map(link => (
            <Link
              key={link.path}
              to={link.path}
              className={`nav-link ${location.pathname === link.path ? 'active' : ''}`}
              onClick={() => setMobileOpen(false)}
            >
              {link.icon} {link.label}
            </Link>
          ))}
        </div>

        {/* Right Section */}
        <div className="nav-right">
          {isAuthenticated ? (
            <div className="nav-user" onClick={() => setDropdownOpen(!dropdownOpen)}>
              <img
                src={user.avatarUrl || `https://ui-avatars.com/api/?name=${user.username}&background=e94560&color=fff`}
                alt={user.username}
                className="nav-avatar"
              />
              <span className="nav-username">{user.username}</span>
              <LevelBadge level={user.level} />

              {/* Dropdown */}
              {dropdownOpen && (
                <div className="nav-dropdown">
                  <Link to="/profile" className="dropdown-item" onClick={() => setDropdownOpen(false)}>
                    <FaUser /> My Profile
                  </Link>
                  <Link to="/support" className="dropdown-item" onClick={() => setDropdownOpen(false)}>
                    💬 Help & Support
                  </Link>
                  {isAdmin && (
                    <Link to="/admin" className="dropdown-item admin-link" onClick={() => setDropdownOpen(false)}>
                      <FaCog /> Admin Panel
                    </Link>
                  )}
                  <button className="dropdown-item logout-btn" onClick={handleLogout}>
                    <FaSignOutAlt /> Logout
                  </button>
                </div>
              )}
            </div>
          ) : (
            <div className="nav-auth">
              <Link to="/login" className="btn btn-outline">Login</Link>
              <Link to="/register" className="btn btn-primary">Join Fanatic</Link>
            </div>
          )}

          {/* Mobile Toggle */}
          <button className="mobile-toggle" onClick={() => setMobileOpen(!mobileOpen)}>
            {mobileOpen ? <FaTimes /> : <FaBars />}
          </button>
        </div>
      </div>
    </nav>
  )
}

export default Navbar