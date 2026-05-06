import { useState, useEffect } from 'react'
import { contentAPI } from '../../services/api'
import ContentCard from '../../components/common/ContentCard'
import { FaSearch, FaFire, FaStar, FaClock } from 'react-icons/fa'
import '../../styles/movies.css'

const MoviesPage = () => {
  const [movies, setMovies] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [filter, setFilter] = useState('latest')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => {
    loadMovies()
  }, [filter, page])

  const loadMovies = async () => {
    setLoading(true)
    try {
      let res
      if (filter === 'top-rated') {
        res = await contentAPI.getTopRated('MOVIE', page)
      } else {
        res = await contentAPI.getByType('MOVIE', page)
      }
      setMovies(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 0)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = async (e) => {
    e.preventDefault()
    if (!search.trim()) return loadMovies()
    setLoading(true)
    try {
      const res = await contentAPI.search(search, 'MOVIE', 0)
      setMovies(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 0)
    } catch (err) {
      console.error(err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="movies-page">
      {/* HEADER */}
      <div className="movies-hero">
        <div className="movies-hero-content">
          <h1>🎬 Movies</h1>
          <p>Discover, rate & review your favorite movies</p>
        </div>
        <div className="movies-hero-bg"></div>
      </div>

      {/* CONTROLS */}
      <div className="content-controls">
        <form onSubmit={handleSearch} className="search-bar">
          <FaSearch className="search-icon" />
          <input
            type="text"
            placeholder="Search movies..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <button type="submit" className="btn btn-primary">Search</button>
        </form>

        <div className="filter-tabs">
          <button
            className={`filter-tab ${filter === 'latest' ? 'active' : ''}`}
            onClick={() => { setFilter('latest'); setPage(0); }}
          >
            <FaClock /> Latest
          </button>
          <button
            className={`filter-tab ${filter === 'top-rated' ? 'active' : ''}`}
            onClick={() => { setFilter('top-rated'); setPage(0); }}
          >
            <FaStar /> Top Rated
          </button>
        </div>
      </div>

      {/* GRID */}
      {loading ? (
        <div className="page-loader">Loading movies...</div>
      ) : (
        <>
          <div className="content-grid movies-grid">
            {movies.map(movie => (
              <ContentCard key={movie.id} content={movie} theme="movie" />
            ))}
          </div>

          {movies.length === 0 && (
            <div className="empty-state">
              <span className="empty-icon">🎬</span>
              <h3>No movies found</h3>
              <p>Try a different search term</p>
            </div>
          )}

          {/* PAGINATION */}
          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="btn btn-outline"
                disabled={page === 0}
                onClick={() => setPage(p => p - 1)}
              >
                ← Previous
              </button>
              <span className="page-info">Page {page + 1} of {totalPages}</span>
              <button
                className="btn btn-outline"
                disabled={page >= totalPages - 1}
                onClick={() => setPage(p => p + 1)}
              >
                Next →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default MoviesPage