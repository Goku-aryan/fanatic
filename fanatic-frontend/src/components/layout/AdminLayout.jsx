import { Outlet, Link, useLocation } from 'react-router-dom'
import { FaChartBar, FaUsers, FaFilm, FaTicketAlt, FaMoneyBill, FaCog, FaArrowLeft } from 'react-icons/fa'
import '../../styles/admin.css'

const AdminLayout = () => {
  const location = useLocation()

  const menuItems = [
    { path: '/admin', label: 'Dashboard', icon: <FaChartBar /> },
    { path: '/admin/users', label: 'Users', icon: <FaUsers /> },
    { path: '/admin/content', label: 'Content', icon: <FaFilm /> },
    { path: '/admin/tickets', label: 'Support Tickets', icon: <FaTicketAlt /> },
    { path: '/admin/payments', label: 'Payments', icon: <FaMoneyBill /> },
    { path: '/admin/settings', label: 'Settings', icon: <FaCog /> }
  ]

  return (
    <div className="admin-layout">
      <aside className="admin-sidebar">
        <div className="admin-logo">
          <span>🎬</span> FANATIC
          <small>Admin Panel</small>
        </div>
        <nav className="admin-nav">
          {menuItems.map(item => (
            <Link
              key={item.path}
              to={item.path}
              className={`admin-nav-item ${location.pathname === item.path ? 'active' : ''}`}
            >
              {item.icon} {item.label}
            </Link>
          ))}
        </nav>
        <Link to="/" className="admin-back-link">
          <FaArrowLeft /> Back to Site
        </Link>
      </aside>
      <main className="admin-main">
        <Outlet />
      </main>
    </div>
  )
}

export default AdminLayout