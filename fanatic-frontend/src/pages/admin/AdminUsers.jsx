import { useState, useEffect } from 'react'
import { adminAPI } from '../../services/api'
import toast from 'react-hot-toast'

const AdminUsers = () => {
  const [users, setUsers] = useState([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => { loadUsers() }, [page])

  const loadUsers = async () => {
    setLoading(true)
    try {
      const res = await adminAPI.getAllUsers(page)
      setUsers(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 0)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const toggleActive = async (userId) => {
    try {
      await adminAPI.toggleUserActive(userId)
      toast.success('User status updated')
      loadUsers()
    } catch (err) { toast.error('Failed') }
  }

  const changeRole = async (userId, role) => {
    try {
      await adminAPI.changeUserRole(userId, role)
      toast.success('Role updated')
      loadUsers()
    } catch (err) { toast.error('Failed') }
  }

  return (
    <div className="admin-users">
      <h1>👥 User Management</h1>
      {loading ? <p>Loading...</p> : (
        <>
          <table className="admin-table">
            <thead>
              <tr><th>Username</th><th>Email</th><th>Role</th><th>Points</th><th>Level</th><th>Verified</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {users.map(u => (
                <tr key={u.id}>
                  <td>{u.username}</td>
                  <td>{u.email}</td>
                  <td>
                    <select value={u.role} onChange={e => changeRole(u.id, e.target.value)}>
                      <option value="USER">USER</option>
                      <option value="MODERATOR">MODERATOR</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  </td>
                  <td>{u.points}</td>
                  <td>Lv.{u.level}</td>
                  <td>{u.isVerified ? '✅' : '❌'}</td>
                  <td>{u.isActive ? '🟢' : '🔴'}</td>
                  <td>
                    <button className="btn btn-sm" onClick={() => toggleActive(u.id)}>
                      {u.isActive ? 'Deactivate' : 'Activate'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {totalPages > 1 && (
            <div className="pagination">
              <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>← Prev</button>
              <span>Page {page + 1} / {totalPages}</span>
              <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>Next →</button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default AdminUsers