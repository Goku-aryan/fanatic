import { useState, useEffect } from 'react'
import { supportAPI } from '../../services/api'
import { formatDate } from '../../utils/helpers'
import toast from 'react-hot-toast'

const SupportPage = () => {
  const [tickets, setTickets] = useState([])
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ subject: '', message: '' })
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => { loadTickets() }, [])

  const loadTickets = async () => {
    try {
      const res = await supportAPI.getMyTickets(0)
      setTickets(res.data.data.content || [])
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setSubmitting(true)
    try {
      await supportAPI.createTicket(form)
      toast.success('Ticket submitted! We\'ll get back to you soon. 💬')
      setShowForm(false)
      setForm({ subject: '', message: '' })
      loadTickets()
    } catch (err) { toast.error('Failed to submit ticket') }
    finally { setSubmitting(false) }
  }

  const statusColor = (s) => {
    const colors = { OPEN: '#ffc107', IN_PROGRESS: '#17a2b8', RESOLVED: '#28a745', CLOSED: '#6c757d' }
    return colors[s] || '#6c757d'
  }

  return (
    <div className="support-page">
      <h1>💬 Help & Support</h1>
      <p>Have a question or issue? Drop your query below.</p>

      <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
        {showForm ? 'Cancel' : '+ New Ticket'}
      </button>

      {showForm && (
        <form onSubmit={handleSubmit} className="support-form">
          <div className="form-group">
            <label>Subject</label>
            <input type="text" value={form.subject} onChange={e => setForm({ ...form, subject: e.target.value })} required placeholder="Brief description of your issue" />
          </div>
          <div className="form-group">
            <label>Message</label>
            <textarea value={form.message} onChange={e => setForm({ ...form, message: e.target.value })} required rows={5} placeholder="Describe your issue in detail..." />
          </div>
          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Submitting...' : 'Submit Ticket'}
          </button>
        </form>
      )}

      <div className="tickets-list">
        <h2>Your Tickets</h2>
        {loading ? <p>Loading...</p> : tickets.length === 0 ? (
          <div className="empty-state"><p>No tickets yet</p></div>
        ) : tickets.map(ticket => (
          <div key={ticket.id} className="ticket-card">
            <div className="ticket-header">
              <h3>{ticket.subject}</h3>
              <span className="ticket-status" style={{ backgroundColor: statusColor(ticket.status) }}>{ticket.status}</span>
            </div>
            <p className="ticket-message">{ticket.message}</p>
            {ticket.adminResponse && (
              <div className="ticket-response">
                <strong>Admin Response:</strong>
                <p>{ticket.adminResponse}</p>
              </div>
            )}
            <span className="ticket-date">{formatDate(ticket.createdAt)}</span>
          </div>
        ))}
      </div>
    </div>
  )
}

export default SupportPage