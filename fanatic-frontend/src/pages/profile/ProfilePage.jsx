import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { userAPI, contentAPI, reviewAPI, followerAPI } from '../../services/api'
import { useAuth } from '../../hooks/useAuth'
import ContentCard from '../../components/common/ContentCard'
import ReviewCard from '../../components/sections/ReviewCard'
import LevelBadge from '../../components/common/LevelBadge'
import Badge from '../../components/common/Badge'
import toast from 'react-hot-toast'

const ProfilePage = () => {
  const { username } = useParams()
  const { user: currentUser, isAuthenticated } = useAuth()
  const navigate = useNavigate()
  const [profile, setProfile] = useState(null)
  const [isFollowing, setIsFollowing] = useState(false)
  const [activeTab, setActiveTab] = useState('movies')
  const [contentList, setContentList] = useState([])
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)

  const isOwnProfile = !username || currentUser?.username === username

  useEffect(() => { loadProfile() }, [username])

  const loadProfile = async () => {
    setLoading(true)
    try {
      let profileRes
      if (isOwnProfile) {
        profileRes = await userAPI.getMe()
      } else {
        profileRes = await userAPI.getByUsername(username)
      }
      setProfile(profileRes.data.data)

      const userId = profileRes.data.data.id
      const [moviesRes, reviewsRes] = await Promise.all([
        contentAPI.getUserList(userId, 'MOVIE', 0),
        reviewAPI.getByUser(userId, 0)
      ])
      setContentList(moviesRes.data.data.content || [])
      setReviews(reviewsRes.data.data.content || [])

      if (isAuthenticated && !isOwnProfile) {
        const followRes = await followerAPI.checkFollowing(userId)
        setIsFollowing(followRes.data.data.isFollowing)
      }
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const handleFollow = async () => {
    try {
      if (isFollowing) {
        await followerAPI.unfollow(profile.id)
        toast.success('Unfollowed')
      } else {
        await followerAPI.follow(profile.id)
        toast.success('Following! 🎉')
      }
      setIsFollowing(!isFollowing)
      loadProfile()
    } catch (err) {
      toast.error(err.response?.data?.message || 'Action failed')
    }
  }

  const loadTabContent = async (tab) => {
    setActiveTab(tab)
    if (!profile) return
    try {
      if (tab === 'reviews') {
        const res = await reviewAPI.getByUser(profile.id, 0)
        setReviews(res.data.data.content || [])
      } else {
        const type = tab.toUpperCase()
        const res = await contentAPI.getUserList(profile.id, type === 'MOVIES' ? 'MOVIE' : type === 'SERIES' ? 'SERIES' : 'BOOK', 0)
        setContentList(res.data.data.content || [])
      }
    } catch (err) { console.error(err) }
  }

  if (loading) return <div className="page-loader">Loading profile...</div>
  if (!profile) return <div className="empty-state"><h3>User not found</h3></div>

  return (
    <div className="profile-page">
      {/* PROFILE HEADER */}
      <div className="profile-header">
        <img
          src={profile.avatarUrl || `https://ui-avatars.com/api/?name=${profile.username}&background=e94560&color=fff&size=200`}
          alt={profile.username}
          className="profile-avatar"
        />
        <div className="profile-info">
          <h1 className="profile-name">
            {profile.fullName || profile.username}
            {profile.isVerified && <span className="verified-tick">✅</span>}
          </h1>
          <p className="profile-username">@{profile.username}</p>
          {profile.bio && <p className="profile-bio">{profile.bio}</p>}

          <LevelBadge level={profile.level} points={profile.points} />

          <div className="profile-stats">
            <div className="stat">
              <strong>{profile.followersCount}</strong>
              <span>Followers</span>
            </div>
            <div className="stat">
              <strong>{profile.followingCount}</strong>
              <span>Following</span>
            </div>
            <div className="stat">
              <strong>{profile.points}</strong>
              <span>Points</span>
            </div>
          </div>

          {/* Badges */}
          {profile.badges?.length > 0 && (
            <div className="profile-badges">
              {profile.badges.map((b, i) => (
                <Badge key={i} type={b.badgeType} name={b.badgeName} icon={b.badgeIcon} />
              ))}
            </div>
          )}

          {/* Actions */}
          <div className="profile-actions">
            {isOwnProfile ? (
              <button className="btn btn-outline" onClick={() => navigate('/profile/edit')}>✏️ Edit Profile</button>
            ) : isAuthenticated && (
              <button className={`btn ${isFollowing ? 'btn-outline' : 'btn-primary'}`} onClick={handleFollow}>
                {isFollowing ? 'Unfollow' : 'Follow'}
              </button>
            )}
          </div>
        </div>
      </div>

      {/* TABS */}
      <div className="profile-tabs">
        {['movies', 'series', 'books', 'reviews'].map(tab => (
          <button
            key={tab}
            className={`tab-btn ${activeTab === tab ? 'active' : ''}`}
            onClick={() => loadTabContent(tab)}
          >
            {tab === 'movies' && '🎬'} {tab === 'series' && '📺'}
            {tab === 'books' && '📚'} {tab === 'reviews' && '✍️'}
            {tab.charAt(0).toUpperCase() + tab.slice(1)}
          </button>
        ))}
      </div>

      {/* TAB CONTENT */}
      <div className="tab-content">
        {activeTab === 'reviews' ? (
          reviews.length > 0 ? (
            reviews.map(r => <ReviewCard key={r.id} review={r} />)
          ) : <div className="empty-state"><p>No reviews yet</p></div>
        ) : (
          contentList.length > 0 ? (
            <div className="content-grid">
              {contentList.map(c => <ContentCard key={c.id} content={c} theme={activeTab.slice(0, -1)} />)}
            </div>
          ) : <div className="empty-state"><p>Nothing here yet</p></div>
        )}
      </div>
    </div>
  )
}

export default ProfilePage