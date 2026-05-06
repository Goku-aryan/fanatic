import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { FaEnvelope, FaLock, FaUser, FaIdCard, FaEye, FaEyeSlash } from 'react-icons/fa'
import '../../styles/auth.css'

const RegisterPage = () => {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    email: '', username: '', password: '', fullName: ''
  })
  const [showPass, setShowPass] = useState(false)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      await register(form)
      navigate('/')
    } catch (err) {
      // handled in context
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page register-page">
      <div className="auth-bg">
        <div className="auth-particles">
          {[...Array(20)].map((_, i) => (
            <div key={i} className="particle" style={{
              left: `${Math.random() * 100}%`,
              animationDelay: `${Math.random() * 5}s`,
              animationDuration: `${3 + Math.random() * 4}s`
            }} />
          ))}
        </div>
      </div>

      <div className="auth-container">
        <div className="auth-info register-info">
          <h2>🎬 Join the Community</h2>
          <div className="auth-features">
            <div className="auth-feature">🌱 Start as a Newbie</div>
            <div className="auth-feature">📊 Level up to Immortal Fanatic 🔱</div>
            <div className="auth-feature">👥 Follow & get recommendations</div>
            <div className="auth-feature">✅ Get verified at 100 followers</div>
          </div>
          <div className="level-preview">
            <h3>Level System</h3>
            <div className="levels-list">
              <span>🌱 Newbie</span> → <span>👀 Casual</span> → <span>🔥 Enthusiast</span> →
              <span>⭐ Fan</span> → <span>💎 Super</span> → <span>🏆 Elite</span> →
              <span>👑 Master</span> → <span>🌟 Legend</span> → <span>⚡ Mythic</span> →
              <span>🔱 Immortal</span>
            </div>
          </div>
        </div>

        <div className="auth-card">
          <div className="auth-header">
            <div className="auth-logo">🎬</div>
            <h1>Create Account</h1>
            <p>Join <span className="brand">FANATIC</span> today</p>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="input-group">
              <FaIdCard className="input-icon" />
              <input
                type="text"
                placeholder="Full Name"
                value={form.fullName}
                onChange={e => setForm({ ...form, fullName: e.target.value })}
              />
            </div>

            <div className="input-group">
              <FaUser className="input-icon" />
              <input
                type="text"
                placeholder="Username"
                value={form.username}
                onChange={e => setForm({ ...form, username: e.target.value })}
                required
              />
            </div>

            <div className="input-group">
              <FaEnvelope className="input-icon" />
              <input
                type="email"
                placeholder="Email"
                value={form.email}
                onChange={e => setForm({ ...form, email: e.target.value })}
                required
              />
            </div>

            <div className="input-group">
              <FaLock className="input-icon" />
              <input
                type={showPass ? 'text' : 'password'}
                placeholder="Password (min 6 characters)"
                value={form.password}
                onChange={e => setForm({ ...form, password: e.target.value })}
                required
                minLength={6}
              />
              <button type="button" className="toggle-pass" onClick={() => setShowPass(!showPass)}>
                {showPass ? <FaEyeSlash /> : <FaEye />}
              </button>
            </div>

            <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
              {loading ? 'Creating Account...' : 'Create Account'}
            </button>
          </form>

          <div className="auth-footer">
            <p>Already have an account? <Link to="/login">Login</Link></p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default RegisterPage