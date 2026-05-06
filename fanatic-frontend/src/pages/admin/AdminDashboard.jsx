import { useState, useEffect } from 'react'
import { adminAPI } from '../../services/api'
import { FaUsers, FaFilm, FaStar, FaMoneyBill, FaTicketAlt, FaCheckCircle } from 'react-icons/fa'

const AdminDashboard = () => {
  const [stats, setStats] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    adminAPI.getDashboard().then(res => {
      setStats(res.data.data)
      setLoading(false)
    }).catch(() => setLoading(false))
  }, [])

  if (loading) return <div className="admin-loader">Loading dashboard...</div>
  if (!stats) return <div>Failed to load dashboard</div>

  const statCards = [
    { label: 'Total Users', value: stats.totalUsers, icon: <FaUsers />, color: '#e94560' },
    { label: 'Active Users', value: stats.activeUsers, icon: <FaUsers />, color: '#0f3460' },
    { label: 'Verified Users', value: stats.verifiedUsers, icon: <FaCheckCircle />, color: '#28a745' },
    { label: 'Total Content', value: stats.totalContent, icon: <FaFilm />, color: '#6c5ce7' },
    { label: 'Movies', value: stats.totalMovies, icon: <FaFilm />, color: '#e94560' },
    { label: 'Series', value: stats.totalSeries, icon: <FaFilm />, color: '#00b4d8' },
    { label: 'Books', value: stats.totalBooks, icon: <FaFilm />, color: '#8d6e63' },
    { label: 'Total Reviews', value: stats.totalReviews, icon: <FaStar />, color: '#ffc107' },
    { label: 'Revenue', value: `
$$
{stats.totalRevenue || 0}`, icon: <FaMoneyBill />, color: '#28a745' },
    { label: 'Open Tickets', value: stats.openTickets, icon: <FaTicketAlt />, color: '#fd7e14' },
  ]

  return (
    <div className="admin-dashboard">
      <h1>📊 Admin Dashboard</h1>

      <div className="stats-grid">
        {statCards.map((stat, i) => (
          <div key={i} className="stat-card" style={{ borderLeft: `4px solid ${stat.color}` }}>
            <div className="stat-icon" style={{ color: stat.color }}>{stat.icon}</div>
            <div className="stat-info">
              <span className="stat-value">{stat.value}</span>
              <span className="stat-label">{stat.label}</span>
            </div>
          </div>
        ))}
      </div>

      <div className="admin-tables">
        <div className="admin-table-section">
          <h2>Recent Users</h2>
          <table className="admin-table">
            <thead><tr><th>Username</th><th>Email</th><th>Role</th><th>Points</th></tr></thead>
            <tbody>
              {stats.recentUsers?.map((u, i) => (
                <tr key={i}><td>{u.username}</td><td>{u.email}</td><td>{u.role}</td><td>{u.points}</td></tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="admin-table-section">
          <h2>Recent Payments</h2>
          <table className="admin-table">
            <thead><tr><th>User</th><th>Content</th><th>Amount</th><th>Status</th></tr></thead>
            <tbody>
              {stats.recentPayments?.map((p, i) => (
                <tr key={i}><td>{p.username}</td><td>{p.contentTitle}</td><td>${p.amount}</td><td>{p.status}</td></tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default AdminDashboard