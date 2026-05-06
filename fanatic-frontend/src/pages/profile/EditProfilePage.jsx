import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { userAPI } from '../../services/api'
import { useAuth } from '../../hooks/useAuth'
import toast from 'react-hot-toast'

const EditProfilePage = () => {
  const { user, updateUser } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({
    fullName: user?.fullName || '',
    bio: user?.bio || '',
    avatarUrl: user?.avatarUrl || ''
  })
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)
    try {
      const res = await userAPI.updateMe(form)
      updateUser(res.data.data)
      toast.success('Profile updated! ✅')
      navigate('/profile')
    } catch (err) {
      toast.error('Update failed')
    } finally { setLoading(false) }
  }

  return (
    <div className="edit-profile-page">
      <h1>Edit Profile</h1>
      <form onSubmit={handleSubmit} className="edit-form">
        <div className="form-group">
          <label>Full Name</label>
          <input type="text" value={form.fullName} onChange={e => setForm({ ...form, fullName: e.target.value })} />
        </div>
        <div className="form-group">
          <label>Bio</label>
          <textarea value={form.bio} onChange={e => setForm({ ...form, bio: e.target.value })} rows={4} placeholder="Tell the world about yourself..." />
        </div>
        <div className="form-group">
          <label>Avatar URL</label>
          <input type="text" value={form.avatarUrl} onChange={e => setForm({ ...form, avatarUrl: e.target.value })} placeholder="https://..." />
          {form.avatarUrl && <img src={form.avatarUrl} alt="Preview" className="avatar-preview" />}
        </div>
        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={loading}>{loading ? 'Saving...' : 'Save Changes'}</button>
          <button type="button" className="btn btn-outline" onClick={() => navigate('/profile')}>Cancel</button>
        </div>
      </form>
    </div>
  )
}

export default EditProfilePage