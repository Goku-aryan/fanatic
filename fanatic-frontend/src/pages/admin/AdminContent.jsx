import { useState, useEffect } from 'react'
import { adminAPI, contentAPI } from '../../services/api'
import toast from 'react-hot-toast'

const AdminContent = () => {
  const [content, setContent] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({
    title: '', description: '', contentType: 'MOVIE', genre: '', coverImageUrl: '',
    authorDirector: '', isEarlyAccess: false, earlyAccessPrice: ''
  })
  const [loading, setLoading] = useState(true)

  useEffect(() => { loadContent() }, [])

  const loadContent = async () => {
    try {
      const res = await contentAPI.getAll(0, 50)
      setContent(res.data.data.content || [])
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await adminAPI.createContent(form)
      toast.success('Content created! 🎉')
      setShowForm(false)
      setForm({ title: '', description: '', contentType: 'MOVIE', genre: '', coverImageUrl: '', authorDirector: '', isEarlyAccess: false, earlyAccessPrice: '' })
      loadContent()
    } catch (err) { toast.error(err.response?.data?.message || 'Failed') }
  }

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this content?')) return
    try {
      await adminAPI.deleteContent(id)
      toast.success('Deleted')
      loadContent()
    } catch (err) { toast.error('Failed') }
  }

  return (
    <div className="admin-content">
      <div className="admin-header-row">
        <h1>🎬 Content Management</h1>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancel' : '+ Add Content'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="admin-form">
          <div className="form-row">
            <div className="form-group">
              <label>Title *</label>
              <input type="text" value={form.title} onChange={e => setForm({ ...form, title: e.target.value })} required />
            </div>
            <div className="form-group">
              <label>Type *</label>
              <select value={form.contentType} onChange={e => setForm({ ...form, contentType: e.target.value })}>
                <option value="MOVIE">Movie</option>
                <option value="SERIES">Series</option>
                <option value="BOOK">Book</option>
              </select>
            </div>
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} rows={3} />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Genre</label>
              <input type="text" value={form.genre} onChange={e => setForm({ ...form, genre: e.target.value })} />
            </div>
            <div className="form-group">
              <label>Author/Director</label>
              <input type="text" value={form.authorDirector} onChange={e => setForm({ ...form, authorDirector: e.target.value })} />
            </div>
          </div>
          <div className="form-group">
            <label>Cover Image URL</label>
            <input type="text" value={form.coverImageUrl} onChange={e => setForm({ ...form, coverImageUrl: e.target.value })} />
          </div>
          <div className="form-row">
            <div className="form-group checkbox-group">
              <label><input type="checkbox" checked={form.isEarlyAccess} onChange={e => setForm({ ...form, isEarlyAccess: e.target.checked })} /> Early Access (First Look)</label>
            </div>
            {form.isEarlyAccess && (
              <div className="form-group">
                <label>Price ($)</label>
                <input type="number" step="0.01" value={form.earlyAccessPrice} onChange={e => setForm({ ...form, earlyAccessPrice: e.target.value })} />
              </div>
            )}
          </div>
          <button type="submit" className="btn btn-primary">Create Content</button>
        </form>
      )}

      <table className="admin-table">
        <thead><tr><th>Title</th><th>Type</th><th>Genre</th><th>Rating</th><th>Early Access</th><th>Actions</th></tr></thead>
        <tbody>
          {content.map(c => (
            <tr key={c.id}>
              <td>{c.title}</td>
              <td>{c.contentType}</td>
              <td>{c.genre}</td>
              <td>⭐ {Number(c.avgRating).toFixed(1)} ({c.totalRatings})</td>
              <td>{c.isEarlyAccess ? `🔥
$$
{c.earlyAccessPrice}` : '—'}</td>
              <td><button className="btn btn-sm btn-danger" onClick={() => handleDelete(c.id)}>Delete</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export default AdminContent