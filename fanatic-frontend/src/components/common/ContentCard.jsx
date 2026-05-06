import { useNavigate } from 'react-router-dom'
import StarRating from './StarRating'
import { truncate } from '../../utils/helpers'

const ContentCard = ({ content, theme = 'movie' }) => {
  const navigate = useNavigate()

  return (
    <div className={`content-card ${theme}-card`} onClick={() => navigate(`/content/${content.id}`)}>
      <div className="card-image">
        <img
          src={content.coverImageUrl || `https://via.placeholder.com/300x450?text=${content.title}`}
          alt={content.title}
          loading="lazy"
        />
        {content.isEarlyAccess && (
          <span className="early-access-badge">🔥 First Look</span>
        )}
        <div className="card-overlay">
          <span className="card-type">{content.contentType}</span>
        </div>
      </div>
      <div className="card-body">
        <h3 className="card-title">{truncate(content.title, 40)}</h3>
        <p className="card-meta">{content.authorDirector}</p>
        <div className="card-footer">
          <StarRating rating={content.avgRating} readonly size={14} />
          <span className="card-genre">{content.genre}</span>
        </div>
      </div>
    </div>
  )
}

export default ContentCard