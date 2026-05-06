import { useState, useEffect } from 'react'
import { contentAPI } from '../../services/api'
import ContentCard from '../../components/common/ContentCard'
import { FaSearch, FaStar, FaClock } from 'react-icons/fa'
import '../../styles/books.css'

const BooksPage = () => {
  const [books, setBooks] = useState([])
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [filter, setFilter] = useState('latest')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  useEffect(() => { loadBooks() }, [filter, page])

  const loadBooks = async () => {
    setLoading(true)
    try {
      let res
      if (filter === 'top-rated') {
        res = await contentAPI.getTopRated('BOOK', page)
      } else {
        res = await contentAPI.getByType('BOOK', page)
      }
      setBooks(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 0)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const handleSearch = async (e) => {
    e.preventDefault()
    if (!search.trim()) return loadBooks()
    setLoading(true)
    try {
      const res = await contentAPI.search(search, 'BOOK', 0)
      setBooks(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 0)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  return (
    <div className="books-page">
      {/* Light, calming header for books */}
      <div className="books-hero">
        <div className="books-hero-content">
          <h1>📚 Books</h1>
          <p>Discover stories that change perspectives</p>
        </div>
      </div>

      <div className="content-controls books-controls">
        <form onSubmit={handleSearch} className="search-bar books-search">
          <FaSearch className="search-icon" />
          <input
            type="text"
            placeholder="Search books by title or author..."
            value={search}
            onChange={e => setSearch(e.target.value)}
          />
          <button type="submit" className="btn btn-book">Search</button>
        </form>

        <div className="filter-tabs books-filters">
          <button className={`filter-tab book-tab ${filter === 'latest' ? 'active' : ''}`}
            onClick={() => { setFilter('latest'); setPage(0); }}>
            <FaClock /> Latest
          </button>
          <button className={`filter-tab book-tab ${filter === 'top-rated' ? 'active' : ''}`}
            onClick={() => { setFilter('top-rated'); setPage(0); }}>
            <FaStar /> Top Rated
          </button>
        </div>
      </div>

      {loading ? (
        <div className="page-loader books-loader">Loading books...</div>
      ) : (
        <>
          <div className="content-grid books-grid">
            {books.map(book => (
              <ContentCard key={book.id} content={book} theme="book" />
            ))}
          </div>
          {books.length === 0 && (
            <div className="empty-state books-empty">
              <span className="empty-icon">📚</span>
              <h3>No books found</h3>
              <p>Try searching for a different title</p>
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

export default BooksPage