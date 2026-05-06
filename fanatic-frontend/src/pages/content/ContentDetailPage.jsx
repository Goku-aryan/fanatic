import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { contentAPI, reviewAPI, paymentAPI } from '../../services/api'
import { useAuth } from '../../hooks/useAuth'
import StarRating from '../../components/common/StarRating'
import ReviewCard from '../../components/sections/ReviewCard'
import { formatDate } from '../../utils/helpers'
import toast from 'react-hot-toast'

const ContentDetailPage = () => {
  const { id } = useParams()
  const { isAuthenticated, user } = useAuth()
  const [content, setContent] = useState(null)
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [showReviewForm, setShowReviewForm] = useState(false)
  const [reviewForm, setReviewForm] = useState({
    title: '', body: '', rating: 0, isSpoiler: false
  })
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => { loadContent() }, [id])

  const loadContent = async () => {
    setLoading(true)
    try {
      const [contentRes, reviewsRes] = await Promise.all([
        contentAPI.getById(id),
        reviewAPI.getByContent(id, 0)
      ])
      setContent(contentRes.data.data)
      setReviews(reviewsRes.data.data.content || [])
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const handleAddToList = async () => {
    try {
      await contentAPI.addToList(id)
      toast.success('Added to your list! +10 points 🎉')
      loadContent()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to add')
    }
  }

  const handleSubmitReview = async (e) => {
    e.preventDefault()
    if (reviewForm.rating === 0) return toast.error('Please select a rating')
    setSubmitting(true)
    try {
      await reviewAPI.create({ ...reviewForm, contentId: id })
      toast.success('Review posted! Points earned! 🎉')
      setShowReviewForm(false)
      setReviewForm({ title: '', body: '', rating: 0, isSpoiler: false })
      loadContent()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Failed to post review')
    } finally { setSubmitting(false) }
  }

  const handleBuyAccess = async () => {
    try {
      const res = await paymentAPI.createCheckout(id)
      window.location.href = res.data.data.sessionUrl
    } catch (err) {
      toast.error(err.response?.data?.message || 'Payment failed')
    }
  }

  if (loading) return <div className="page-loader">Loading...</div>
  if (!content) return <div className="empty-state"><h3>Content not found</h3></div>

  const themeClass = content.contentType === 'BOOK' ? 'book-theme' :
    content.contentType === 'SERIES' ? 'series-theme' : 'movie-theme'

  return (
    <div className={`content-detail ${themeClass}`}>
      {/* HEADER */}
      <div className="detail-header">
        <div className="detail-cover">
          <img
            src={content.coverImageUrl || `https://via.placeholder.com/300x450?text=${content.title}`}
            alt={content.title}
          />
          {content.isEarlyAccess && <span className="early-badge">🔥 First Look</span>}
        </div>

        <div className="detail-info">
          <span className="detail-type">{content.contentType}</span>
          <h1 className="detail-title">{content.title}</h1>
          <p className="detail-meta">
            {content.contentType === 'BOOK' ? '✍️ Author' : '🎬 Director'}: <strong>{content.authorDirector}</strong>
          </p>
          {content.genre && <span className="detail-genre">{content.genre}</span>}
          {content.releaseDate && <p className="detail-date">📅 {formatDate(content.releaseDate)}</p>}

          <div className="detail-rating">
            <StarRating rating={content.avgRating} readonly size={24} />
            <span className="rating-count">({content.totalRatings} reviews)</span>
          </div>

          <p className="detail-description">{content.description}</p>

          {/* ACTIONS */}
          {isAuthenticated && (
            <div className="detail-actions">
              {!content.userHasAdded && !content.isEarlyAccess && (
                <button className="btn btn-primary btn-lg" onClick={handleAddToList}>
                  ✅ Add to My List (+10 pts)
                </button>
              )}
              {content.userHasAdded && <span className="added-badge">✅ In Your List</span>}

              {content.isEarlyAccess && !content.userHasEarlyAccess && (
                <button className="btn btn-gold btn-lg" onClick={handleBuyAccess}>
                  🔥 Get First Look Pass — ${content.earlyAccessPrice}
                </button>
              )}
              {content.userHasEarlyAccess && (
                <span className="access-badge">🔥 You have First Look Access!</span>
              )}

              {(content.userHasAdded || content.userHasEarlyAccess) && !content.userHasReviewed && (
                <button className="btn btn-secondary btn-lg" onClick={() => setShowReviewForm(true)}>
                  ✍️ Write Review (+25 pts)
                </button>
              )}
              {content.userHasReviewed && <span className="reviewed-badge">✍️ You've reviewed this</span>}
            </div>
          )}
        </div>
      </div>

      {/* REVIEW FORM */}
      {showReviewForm && (
        <div className="review-form-container">
          <h3>Write Your Review</h3>
          <form onSubmit={handleSubmitReview} className="review-form">
            <div className="form-group">
              <label>Your Rating *</label>
              <StarRating
                rating={reviewForm.rating}
                onRate={(r) => setReviewForm({ ...reviewForm, rating: r })}
                size={32}
              />
            </div>
            <div className="form-group">
              <label>Review Title</label>
              <input
                type="text"
                placeholder="Give your review a title"
                value={reviewForm.title}
                onChange={e => setReviewForm({ ...reviewForm, title: e.target.value })}
              />
            </div>
            <div className="form-group">
              <label>Your Review *</label>
              <textarea
                placeholder="Share your thoughts..."
                value={reviewForm.body}
                onChange={e => setReviewForm({ ...reviewForm, body: e.target.value })}
                required
                rows={5}
              />
            </div>
            <div className="form-group checkbox-group">
              <label>
                <input
                  type="checkbox"
                  checked={reviewForm.isSpoiler}
                  onChange={e => setReviewForm({ ...reviewForm, isSpoiler: e.target.checked })}
                />
                Contains spoilers
              </label>
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary" disabled={submitting}>
                {submitting ? 'Posting...' : 'Post Review'}
              </button>
              <button type="button" className="btn btn-outline" onClick={() => setShowReviewForm(false)}>Cancel</button>
            </div>
          </form>
        </div>
      )}

      {/* REVIEWS */}
      <div className="reviews-section">
        <h2>Reviews ({reviews.length})</h2>
        {reviews.length > 0 ? (
          reviews.map(review => <ReviewCard key={review.id} review={review} />)
        ) : (
          <div className="empty-state">
            <p>No reviews yet. Be the first to review!</p>
          </div>
        )}
      </div>
    </div>
  )
}

export default ContentDetailPage