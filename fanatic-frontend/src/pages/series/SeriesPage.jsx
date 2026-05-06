import { useState, useEffect } from 'react'
import { contentAPI } from '../../services/api'
import ContentCard from '../../components/common/ContentCard'
import { FaSearch, FaStar, FaClock } from 'react-icons/fa'
import '../../styles/series.css'

const SeriesPage = () => {
  const [series, setSeries] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [filter, setFilter] = useState('latest')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => { loadSeries() }, [filter, page])

  const loadSeries = async () => {
    setLoading(true)
    try {
      let res
      if (filter === 'top-rated') {
        res = await contentAPI.getTopRated('SERIES', page)
      } else {
        res = await contentAPI.getByType('SERIES', page)
      }
      setSeries(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 0)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const handleSearch = async (e) => {
    e.preventDefault()
    if (!search.trim()) return loadSeries()
    setLoading(true)
    try {
      const res = await contentAPI.search(search, 'SERIES', 0)
      setSeries(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 0)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  return (
    <div className="series-page">
      <div className="series-hero">
        <div className="series-hero-content">
          <h1>📺 Web Series</h1>
          <p>Binge-worthy shows you can't stop watching</p>
        </div>
        <div className="series-hero-bg"></div>
      </div>

      <div className="content-controls">
        <form onSubmit={handleSearch} className="search-bar">
          <FaSearch className="search-icon" />
          <input
            type="text"
            placeholder="Search series..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <button type="submit" className="btn btn-primary">Search</button>
        </form>

        <div className="filter-tabs">
          <button className={`filter-tab ${filter === 'latest' ? 'active' : ''}`}
            onClick={() => { setFilter('latest'); setPage(0); }}>
            <FaClock /> Latest
          </button>
          <button className={`filter-tab ${filter === 'top-rated' ? 'active' : ''}`}
            onClick={() => { setFilter('top-rated'); setPage(0); }}>
            <FaStar /> Top Rated
          </button>
        </div>
      </div>

      {loading ? (
        <div className="page-loader">Loading series...</div>
      ) : (
        <>
          <div className="content-grid series-grid">
            {series.map(s => (
              <ContentCard key={s.id} content={s} theme="series" />
            ))}
          </div>
          {series.length === 0 && (
            <div className="empty-state">
              <span className="empty-icon">📺</span>
              <h3>No series found</h3>
            </div>
          )}
          {totalPages > 1 && (
            <div className="pagination">
              <button className="btn btn-outline" disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Previous</button>
              <span className="page-info">Page {page + 1} of {totalPages}</span>
              <button className="btn btn-outline" disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>Next →</button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default SeriesPage