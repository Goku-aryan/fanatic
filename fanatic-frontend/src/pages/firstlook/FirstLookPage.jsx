import { useState, useEffect } from 'react'
import { contentAPI } from '../../services/api'
import ContentCard from '../../components/common/ContentCard'

const FirstLookPage = () => {
  const [content, setContent] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    contentAPI.getEarlyAccess(0).then(res => {
      setContent(res.data.data.content || [])
      setLoading(false)
    }).catch(() => setLoading(false))
  }, [])

  return (
    <div className="firstlook-page">
      <div className="firstlook-hero">
        <h1>🔥 First Look Pass</h1>
        <p>Get exclusive early access to unreleased movies, series & books. Review before anyone else and earn bonus points!</p>
      </div>

      {loading ? <div className="page-loader">Loading...</div> : (
        <div className="content-grid">
          {content.map(c => <ContentCard key={c.id} content={c} theme="movie" />)}
        </div>
      )}
      {!loading && content.length === 0 && (
        <div className="empty-state"><span className="empty-icon">🔥</span><h3>No early access content available right now</h3><p>Check back soon!</p></div>
      )}
    </div>
  )
}

export default FirstLookPage