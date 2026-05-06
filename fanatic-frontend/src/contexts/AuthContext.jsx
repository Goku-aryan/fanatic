import { createContext, useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { authAPI, userAPI } from '../services/api'
import toast from 'react-hot-toast'

export const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // Load user on mount
  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      loadUser()
    } else {
      setLoading(false)
    }
  }, [])

  const loadUser = async () => {
    try {
      const res = await userAPI.getMe()
      setUser(res.data.data)
    } catch (error) {
      localStorage.clear()
    } finally {
      setLoading(false)
    }
  }

  const login = async (credentials) => {
    try {
      const res = await authAPI.login(credentials)
      const { accessToken, refreshToken, user: userData } = res.data.data
      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
      setUser(userData)
      toast.success(`Welcome back, ${userData.username}! 🎬`)
      return userData
    } catch (error) {
      toast.error(error.response?.data?.message || 'Login failed')
      throw error
    }
  }

  const register = async (data) => {
    try {
      const res = await authAPI.register(data)
      const { accessToken, refreshToken, user: userData } = res.data.data
      localStorage.setItem('accessToken', accessToken)
      localStorage.setItem('refreshToken', refreshToken)
      setUser(userData)
      toast.success(`Welcome to Fanatic, ${userData.username}! 🎉`)
      return userData
    } catch (error) {
      toast.error(error.response?.data?.message || 'Registration failed')
      throw error
    }
  }

  const logout = () => {
    localStorage.clear()
    setUser(null)
    toast.success('Logged out successfully!')
  }

  const updateUser = (updatedData) => {
    setUser(prev => ({ ...prev, ...updatedData }))
  }

  const isAdmin = user?.role === 'ADMIN'
  const isAuthenticated = !!user

  return (
    <AuthContext.Provider value={{
      user, loading, isAdmin, isAuthenticated,
      login, register, logout, loadUser, updateUser
    }}>
      {children}
    </AuthContext.Provider>
  )
}