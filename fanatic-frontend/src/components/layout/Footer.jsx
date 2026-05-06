import { Link } from 'react-router-dom'

const Footer = () => (
  <footer className="footer">
    <div className="footer-container">
      <div className="footer-section">
        <h3>🎬 FANATIC</h3>
        <p>Share what you watch. Review what you love.</p>
      </div>
      <div className="footer-section">
        <h4>Explore</h4>
        <Link to="/movies">Movies</Link>
        <Link to="/series">Web Series</Link>
        <Link to="/books">Books</Link>
      </div>
      <div className="footer-section">
        <h4>Community</h4>
        <Link to="/leaderboard">Leaderboard</Link>
        <Link to="/first-look">First Look</Link>
        <Link to="/support">Help & Support</Link>
      </div>
      <div className="footer-section">
        <h4>Levels</h4>
        <p>🌱 Newbie → 🔱 Immortal</p>
        <p>Earn points by adding & reviewing!</p>
      </div>
    </div>
    <div className="footer-bottom">
      <p>© 2024 Fanatic. Built with ❤️ for movie, series & book lovers.</p>
    </div>
  </footer>
)

export default Footer