import { useState, useEffect } from 'react'
import { adminAPI } from '../../services/api'
import { formatDate } from '../../utils/helpers'
import toast from 'react-hot-toast'

const AdminTickets = () => {
  const [tickets, setTickets] = useState([])
  const [filter, setFilter] = useState('')
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [respondingTo, setRespondingTo] = useState(null)
  const [response, setResponse] = useState('')
  const [responseStatus, setResponseStatus] = useState('RESOLVED')

  useEffect(() => { loadTickets() }, [filter, page])

  const loadTickets = async () => {
    setLoading(true)
    try {
      const res = await adminAPI.getAllTickets(filter, page)
      setTickets(res.data.data.content || [])
      setTotalPages(res.data.data.totalPages || 0)
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const handleRespond = async (ticketId) => {
    if (!response.trim()) return toast.error('Please enter a response')
    try {
      await adminAPI.respondToTicket(ticketId, {
        response: response,
        status: responseStatus
      })
      toast.success('Response sent! ✅')
      setRespondingTo(null)
      setResponse('')
      loadTickets()
    } catch (err) {
      toast.error('Failed to respond')
    }
  }

  const handleStatusChange = async (ticketId, status) => {
    try {
      await adminAPI.updateTicketStatus(ticketId, status)
      toast.success('Status updated')
      loadTickets()
    } catch (err) {
      toast.error('Failed to update status')
    }
  }

  const statusColor = (s) => {
    const colors = {
      OPEN: '#ffc107',
      IN_PROGRESS: '#17a2b8',
      RESOLVED: '#28a745',
      CLOSED: '#6c757d'
    }
    return colors[s] || '#6c757d'
  }

  return (
    <div className="admin-tickets">
      <h1>🎫 Support Tickets</h1>

      {/* FILTER TABS */}
      <div className="filter-tabs">
        {['', 'OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'].map(f => (
          <button
            key={f}
            className={`filter-tab ${filter === f ? 'active' : ''}`}
            onClick={() => { setFilter(f); setPage(0); }}
          >
            {f === '' ? 'All' : f.replace('_', ' ')}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="admin-loader">Loading tickets...</div>
      ) : tickets.length === 0 ? (
        <div className="empty-state">
          <span className="empty-icon">🎫</span>
          <h3>No tickets found</h3>
        </div>
      ) : (
        <div className="tickets-container">
          {tickets.map(ticket => (
            <div key={ticket.id} className="admin-ticket-card">
              {/* TICKET HEADER */}
              <div className="ticket-card-header">
                <div className="ticket-card-left">
                  <h3>{ticket.subject}</h3>
                  <span className="ticket-user">
                    👤 {ticket.username} • {formatDate(ticket.createdAt)}
                  </span>
                </div>
                <div className="ticket-card-right">
                  <span
                    className="ticket-status-badge"
                    style={{ backgroundColor: statusColor(ticket.status) }}
                  >
                    {ticket.status.replace('_', ' ')}
                  </span>
                  <select
                    value={ticket.status}
                    onChange={(e) => handleStatusChange(ticket.id, e.target.value)}
                    className="status-select"
                  >
                    <option value="OPEN">OPEN</option>
                    <option value="IN_PROGRESS">IN PROGRESS</option>
                    <option value="RESOLVED">RESOLVED</option>
                    <option value="CLOSED">CLOSED</option>
                  </select>
                </div>
              </div>

              {/* TICKET MESSAGE */}
              <div className="ticket-card-body">
                <p className="ticket-message">{ticket.message}</p>
              </div>

              {/* EXISTING ADMIN RESPONSE */}
              {ticket.adminResponse && (
                <div className="ticket-existing-response">
                  <strong>📩 Admin Response ({ticket.respondedByUsername}):</strong>
                  <p>{ticket.adminResponse}</p>
                </div>
              )}

              {/* RESPOND FORM */}
              {respondingTo === ticket.id ? (
                <div className="ticket-respond-form">
                  <textarea
                    placeholder="Type your response..."
                    value={response}
                    onChange={e => setResponse(e.target.value)}
                    rows={3}
                  />
                  <div className="respond-actions">
                    <select
                      value={responseStatus}
                      onChange={e => setResponseStatus(e.target.value)}
                    >
                      <option value="IN_PROGRESS">Mark as In Progress</option>
                      <option value="RESOLVED">Mark as Resolved</option>
                      <option value="CLOSED">Mark as Closed</option>
                    </select>
                    <button
                      className="btn btn-primary btn-sm"
                      onClick={() => handleRespond(ticket.id)}
                    >
                      Send Response
                    </button>
                    <button
                      className="btn btn-outline btn-sm"
                      onClick={() => { setRespondingTo(null); setResponse(''); }}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <button
                  className="btn btn-outline btn-sm respond-btn"
                  onClick={() => setRespondingTo(ticket.id)}
                >
                  ✍️ Respond
                </button>
              )}
            </div>
          ))}

          {/* PAGINATION */}
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
        </div>
      )}
    </div>
  )
}

export default AdminTickets