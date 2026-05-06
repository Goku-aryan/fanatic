import { useState } from 'react'
import { FaStar, FaStarHalfAlt, FaRegStar } from 'react-icons/fa'

const StarRating = ({ rating, onRate, size = 20, readonly = false }) => {
  const [hover, setHover] = useState(0)

  if (readonly) {
    return (
      <div className="star-rating">
        {[1, 2, 3, 4, 5].map(star => (
          <span key={star}>
            {rating >= star ? (
              <FaStar color="#ffc107" size={size} />
            ) : rating >= star - 0.5 ? (
              <FaStarHalfAlt color="#ffc107" size={size} />
            ) : (
              <FaRegStar color="#ffc107" size={size} />
            )}
          </span>
        ))}
        <span className="rating-number">{Number(rating).toFixed(1)}</span>
      </div>
    )
  }

  return (
    <div className="star-rating interactive">
      {[1, 2, 3, 4, 5].map(star => (
        <span
          key={star}
          onClick={() => onRate(star)}
          onMouseEnter={() => setHover(star)}
          onMouseLeave={() => setHover(0)}
          style={{ cursor: 'pointer' }}
        >
          {(hover || rating) >= star ? (
            <FaStar color="#ffc107" size={size} />
          ) : (
            <FaRegStar color="#ffc107" size={size} />
          )}
        </span>
      ))}
    </div>
  )
}

export default StarRating