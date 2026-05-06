import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import Loader from './Loader'

const AdminRoute = () => {
  const { isAdmin, loading, isAuthenticated } = useAuth()

  if (loading) return <Loader />
  if (!isAuthenticated) return <Navigate to="/login" replace />
  if (!isAdmin) return <Navigate to="/" replace />

  return <Outlet />
}

export default AdminRoute