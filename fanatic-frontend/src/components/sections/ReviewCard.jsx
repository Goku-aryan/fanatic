import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { FaHeart, FaRegHeart, FaEyeSlash } from 'react-icons/fa'
import { reviewAPI } from '../../services/api'
import StarRating from '../common/StarRating'
import { formatDate } from '../../utils/helpers'
import toast from 'react-hot-toast'

const ReviewCard = ({ review, onUpdate }) => {
  const navigate = useNavigate()
  const [liked, setLiked] = useState(review.userHasLiked)
  const [likes, setLikes] = useState(review.likesCount)
  const [showSpoiler, setShowSpoiler] = useState(false)

  const handleLike = async (e) => {
    e.stopPropagation()
    try {
      const res = await reviewAPI.toggleLike(review.id)
      setLiked(res.data.data.userHasLiked)
      setLikes(res.data.data.likesCount)
    } catch (err) {
      toast.error('Login to like reviews')
    }
  }

  return (
    <div className={`review-card ${review.isEarlyAccessReview ? 'early-access' : ''}`}>
      <div className="review-header">
        <div className="review-user" onClick={() => navigate(`/user/${review.username}`)}>
          <img
            src={review.userAvatarUrl || `https://ui-avatars.com/api/?name=${review.username}&background=e94560&color=fff`}
            alt={review.username}
            className="review-avatar"
          />
          <div>
            <span className="review-username">
              {review.username}
              {review.userIsVerified && <span className="verified-tick">✅</span>}
            </span>
            {review.isEarlyAccessReview && (
              <span className="first-look-tag">🔥 First Look Review</span>
            )}
          </div>
        </div>
        <StarRating rating={review.rating} readonly size={16} />
      </div>

      <h4 className="review-title">{review.title}</h4>

      {review.isSpoiler && !showSpoiler ? (
        <div className="spoiler-warning" onClick={() => setShowSpoiler(true)}>
          <FaEyeSlash /> <span>Contains spoilers — Click to reveal</span>
        </div>
      ) : (
        <p className="review-body">{review.body}</p>
      )}

      <div className="review-footer">
        <button className={`like-btn ${liked ? 'liked' : ''}`} onClick={handleLike}>
          {liked ? <FaHeart /> : <FaRegHeart />} {likes}
        </button>
        <span className="review-date">{formatDate(review.createdAt)}</span>
        {review.pointsEarned > 0 && (
          <span className="points-earned">+{review.pointsEarned} pts</span>
        )}
      </div>
    </div>
  )
}

export default ReviewCard