import { useState, useEffect } from 'react'
import { adminAPI } from '../../services/api'
import { formatDate } from '../../utils/helpers'

const AdminPayments = () => {
  const [payments, setPayments] = useState([])
  const [revenue, setRevenue] = useState(null)
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => { loadData() }, [page])

  const loadData = async () => {
    setLoading(true)
    try {
      const [paymentsRes, revenueRes] = await Promise.all([
        adminAPI.getAllPayments(page),
        adminAPI.getRevenue()
      ])
      setPayments(paymentsRes.data.data.content || [])
      setTotalPages(paymentsRes.data.data.totalPages || 0)
      setRevenue(revenueRes.data.data)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const statusColor = (s) => {
    const colors = {
      COMPLETED: '#28a745',
      PENDING: '#ffc107',
      FAILED: '#dc3545',
      REFUNDED: '#17a2b8'
    }
    return colors[s] || '#6c757d'
  }

  return (
    <div className="admin-payments">
      <h1>💰 Payment Management</h1>

      {/* REVENUE STATS */}
      {revenue && (
        <div className="revenue-stats">
          <div className="revenue-card">
            <span className="revenue-label">Total Revenue</span>
            <span className="revenue-value">${revenue.totalRevenue || 0}</span>
          </div>
          <div className="revenue-card">
            <span className="revenue-label">Last 7 Days</span>
            <span className="revenue-value">${revenue.last7Days || 0}</span>
          </div>
          <div className="revenue-card">
            <span className="revenue-label">Last 30 Days</span>
            <span className="revenue-value">${revenue.last30Days || 0}</span>
          </div>
          <div className="revenue-card">
            <span className="revenue-label">Completed</span>
            <span className="revenue-value">{revenue.completedPayments || 0}</span>
          </div>
          <div className="revenue-card">
            <span className="revenue-label">Pending</span>
            <span className="revenue-value">{revenue.pendingPayments || 0}</span>
          </div>
          <div className="revenue-card">
            <span className="revenue-label">Failed</span>
            <span className="revenue-value">{revenue.failedPayments || 0}</span>
          </div>
        </div>
      )}

      {/* PAYMENTS TABLE */}
      {loading ? (
        <div className="admin-loader">Loading payments...</div>
      ) : (
        <>
          <table className="admin-table">
            <thead>
              <tr>
                <th>User</th>
                <th>Content</th>
                <th>Amount</th>
                <th>Currency</th>
                <th>Status</th>
                <th>Provider</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {payments.map(p => (
                <tr key={p.id}>
                  <td>{p.userId?.substring(0, 8)}...</td>
                  <td>{p.contentTitle}</td>
                  <td><strong>${p.amount}</strong></td>
                  <td>{p.currency}</td>
                  <td>
                    <span
                      className="status-badge"
                      style={{ backgroundColor: statusColor(p.paymentStatus) }}
                    >
                      {p.paymentStatus}
                    </span>
                  </td>
                  <td>{p.paymentProvider}</td>
                  <td>{formatDate(p.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>

          {payments.length === 0 && (
            <div className="empty-state">
              <span className="empty-icon">💰</span>
              <h3>No payments yet</h3>
            </div>
          )}

          {totalPages > 1 && (
            <div className="pagination">
              <button
                className="btn btn-outline"
                disabled={page === 0}
                onClick={() => setPage(p => p - 1)}
              >
                ← Previous
              </button>
              <span className="page-info">Page {page + 1} of {totalPages}</span>
              <button
                className="btn btn-outline"
                disabled={page >= totalPages - 1}
                onClick={() => setPage(p => p + 1)}
              >
                Next →
              </button>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default AdminPayments