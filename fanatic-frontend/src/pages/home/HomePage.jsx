import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { contentAPI, leaderboardAPI, reviewAPI } from '../../services/api'
import ContentCard from '../../components/common/ContentCard'
import ReviewCard from '../../components/sections/ReviewCard'
import { useAuth } from '../../hooks/useAuth'
import '../../styles/global.css'

const HomePage = () => {
  const { isAuthenticated } = useAuth()
  const [topMovies, setTopMovies] = useState([])
  const [topSeries, setTopSeries] = useState([])
  const [topBooks, setTopBooks] = useState([])
  const [leaders, setLeaders] = useState([])
  const [feed, setFeed] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      const [moviesRes, seriesRes, booksRes, leaderRes] = await Promise.all([
        contentAPI.getTopRated('MOVIE', 0),
        contentAPI.getTopRated('SERIES', 0),
        contentAPI.getTopRated('BOOK', 0),
        leaderboardAPI.getOverall(5)
      ])
      setTopMovies(moviesRes.data.data.content?.slice(0, 6) || [])
      setTopSeries(seriesRes.data.data.content?.slice(0, 6) || [])
      setTopBooks(booksRes.data.data.content?.slice(0, 6) || [])
      setLeaders(leaderRes.data.data || [])

      if (isAuthenticated) {
        try {
          const feedRes = await reviewAPI.getFeed(0)
          setFeed(feedRes.data.data.content?.slice(0, 5) || [])
        } catch (e) {}
      }
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <div className="page-loader">Loading...</div>

  return (
    <div className="home-page">
      {/* HERO */}
      <section className="hero">
        <div className="hero-content">
          <h1 className="hero-title">
            Share Your <span className="gradient-text">Obsession</span>
          </h1>
          <p className="hero-subtitle">
            Track movies, binge-worthy series & must-read books.
            Write reviews, earn points & climb the leaderboard.
          </p>
          <div className="hero-actions">
            <Link to="/movies" className="btn btn-primary btn-lg">🎬 Explore Movies</Link>
            <Link to="/series" className="btn btn-secondary btn-lg">📺 Web Series</Link>
            <Link to="/books" className="btn btn-outline btn-lg">📚 Books</Link>
          </div>
          <div className="hero-stats">
            <div className="stat-item">
              <span className="stat-number">🌱→🔱</span>
              <span className="stat-label">10 Levels</span>
            </div>
            <div className="stat-item">
              <span className="stat-number">⭐</span>
              <span className="stat-label">Rate & Review</span>
            </div>
            <div className="stat-item">
              <span className="stat-number">🔥</span>
              <span className="stat-label">First Look Access</span>
            </div>
          </div>
        </div>
      </section>

      {/* TOP MOVIES */}
      <section className="home-section movies-section">
        <div className="section-header">
          <h2>🎬 Top Rated Movies</h2>
          <Link to="/movies" className="see-all">See All →</Link>
        </div>
        <div className="content-grid">
          {topMovies.map(movie => (
            <ContentCard key={movie.id} content={movie} theme="movie" />
          ))}
        </div>
      </section>

      {/* TOP SERIES */}
      <section className="home-section series-section">
        <div className="section-header">
          <h2>📺 Trending Series</h2>
          <Link to="/series" className="see-all">See All →</Link>
        </div>
        <div className="content-grid">
          {topSeries.map(s => (
            <ContentCard key={s.id} content={s} theme="series" />
          ))}
        </div>
      </section>

      {/* TOP BOOKS */}
      <section className="home-section books-section">
        <div className="section-header">
          <h2>📚 Must-Read Books</h2>
          <Link to="/books" className="see-all">See All →</Link>
        </div>
        <div className="content-grid">
          {topBooks.map(book => (
            <ContentCard key={book.id} content={book} theme="book" />
          ))}
        </div>
      </section>

      {/* LEADERBOARD PREVIEW */}
      <section className="home-section leaderboard-preview">
        <div className="section-header">
          <h2>🏆 Top Fanatics</h2>
          <Link to="/leaderboard" className="see-all">Full Leaderboard →</Link>
        </div>
        <div className="leaders-list">
          {leaders.map((leader, idx) => (
            <div key={leader.userId} className="leader-item">
              <span className="leader-rank">#{idx + 1}</span>
              <img
                src={leader.avatarUrl || `https://ui-avatars.com/api/?name=${leader.username}&background=e94560&color=fff`}
                alt={leader.username}
                className="leader-avatar"
              />
              <div className="leader-info">
                <span className="leader-name">
                  {leader.username}
                  {leader.isVerified && ' ✅'}
                </span>
                <span className="leader-points">{leader.points} pts • Lv.{leader.level}</span>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* FEED */}
      {isAuthenticated && feed.length > 0 && (
        <section className="home-section feed-section">
          <div className="section-header">
            <h2>📰 Your Feed</h2>
          </div>
          {feed.map(review => (
            <ReviewCard key={review.id} review={review} />
          ))}
        </section>
      )}
    </div>
  )
}

export default HomePage