import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { leaderboardAPI } from '../../services/api'
import LevelBadge from '../../components/common/LevelBadge'

const LeaderboardPage = () => {
  const navigate = useNavigate()
  const [leaders, setLeaders] = useState([])
  const [tab, setTab] = useState('overall')
  const [loading, setLoading] = useState(true)

  useEffect(() => { loadLeaderboard() }, [tab])

  const loadLeaderboard = async () => {
    setLoading(true)
    try {
      let res
      if (tab === 'overall') res = await leaderboardAPI.getOverall(50)
      else res = await leaderboardAPI.getByType(tab, 50)
      setLeaders(res.data.data || [])
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const getRankStyle = (rank) => {
    if (rank === 1) return 'rank-gold'
    if (rank === 2) return 'rank-silver'
    if (rank === 3) return 'rank-bronze'
    return ''
  }

  return (
    <div className="leaderboard-page">
      <h1>🏆 Leaderboard</h1>

      <div className="leaderboard-tabs">
        {[
          { key: 'overall', label: '🏆 Overall' },
          { key: 'MOVIE', label: '🎬 Movies' },
          { key: 'SERIES', label: '📺 Series' },
          { key: 'BOOK', label: '📚 Books' }
        ].map(t => (
          <button key={t.key} className={`tab-btn ${tab === t.key ? 'active' : ''}`}
            onClick={() => setTab(t.key)}>
            {t.label}
          </button>
        ))}
      </div>

      {loading ? <div className="page-loader">Loading...</div> : (
        <div className="leaderboard-list">
          {leaders.map(leader => (
            <div key={leader.userId} className={`leaderboard-item ${getRankStyle(leader.rank)}`}
              onClick={() => navigate(`/user/${leader.username}`)}>
              <span className="lb-rank">#{leader.rank}</span>
              <img src={leader.avatarUrl || `https://ui-avatars.com/api/?name=${leader.username}&background=e94560&color=fff`}
                alt={leader.username} className="lb-avatar" />
              <div className="lb-info">
                <span className="lb-name">{leader.username} {leader.isVerified && '✅'}</span>
                <LevelBadge level={leader.level} points={leader.points} />
              </div>
              <div className="lb-stats">
                <span>{leader.contentCount} items</span>
                <span>{leader.reviewCount} reviews</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default LeaderboardPage