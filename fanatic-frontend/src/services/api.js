import axios from 'axios'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "https://fanatic-backend.onrender.com/api"

console.log("API URL:", API_BASE_URL)

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' }
})

// Add JWT token to every request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Handle token expiry
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const res = await axios.post(`${API_BASE_URL}/auth/refresh`, { refreshToken })
          localStorage.setItem('accessToken', res.data.data.accessToken)
          localStorage.setItem('refreshToken', res.data.data.refreshToken)
          error.config.headers.Authorization = `Bearer ${res.data.data.accessToken}`
          return api(error.config)
        } catch (refreshError) {
          localStorage.clear()
          window.location.href = '/login'
        }
      } else {
        localStorage.clear()
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

// ==================== AUTH ====================
export const authAPI = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  refresh: (refreshToken) => api.post('/auth/refresh', { refreshToken })
}

// ==================== USERS ====================
export const userAPI = {
  getMe: () => api.get('/users/me'),
  updateMe: (data) => api.put('/users/me', data),
  getProfile: (userId) => api.get(`/users/${userId}/profile`),
  getByUsername: (username) => api.get(`/users/username/${username}`),
  search: (query, page = 0) => api.get(`/users/search?query=${query}&page=${page}`),
  getPointHistory: (page = 0) => api.get(`/users/me/points/history?page=${page}`),
  getLevelInfo: () => api.get('/users/me/level'),
  getAllLevels: () => api.get('/users/levels')
}

// ==================== CONTENT ====================
export const contentAPI = {
  getAll: (page = 0, size = 20) => api.get(`/content?page=${page}&size=${size}`),
  getById: (id) => api.get(`/content/${id}`),
  getByType: (type, page = 0) => api.get(`/content/type/${type}?page=${page}`),
  search: (query, type, page = 0) => api.get(`/content/search?query=${query}&type=${type || ''}&page=${page}`),
  getTopRated: (type, page = 0) => api.get(`/content/top-rated?type=${type || ''}&page=${page}`),
  getEarlyAccess: (page = 0) => api.get(`/content/early-access?page=${page}`),
  addToList: (contentId) => api.post(`/content/add/${contentId}`),
  removeFromList: (contentId) => api.delete(`/content/remove/${contentId}`),
  getMyList: (type, page = 0) => api.get(`/content/my-list?type=${type || ''}&page=${page}`),
  getUserList: (userId, type, page = 0) => api.get(`/content/user/${userId}/list?type=${type || ''}&page=${page}`),
  // Admin
  create: (data) => api.post('/content', data),
  update: (id, data) => api.put(`/content/${id}`, data),
  delete: (id) => api.delete(`/content/${id}`)
}

// ==================== REVIEWS ====================
export const reviewAPI = {
  create: (data) => api.post('/reviews', data),
  update: (reviewId, data) => api.put(`/reviews/${reviewId}`, data),
  delete: (reviewId) => api.delete(`/reviews/${reviewId}`),
  getByContent: (contentId, page = 0) => api.get(`/reviews/content/${contentId}?page=${page}`),
  getByUser: (userId, page = 0) => api.get(`/reviews/user/${userId}?page=${page}`),
  getFeed: (page = 0) => api.get(`/reviews/feed?page=${page}`),
  toggleLike: (reviewId) => api.post(`/reviews/${reviewId}/like`),
  getEarlyAccess: (page = 0) => api.get(`/reviews/early-access?page=${page}`)
}

// ==================== FOLLOWERS ====================
export const followerAPI = {
  follow: (userId) => api.post(`/follow/${userId}`),
  unfollow: (userId) => api.delete(`/follow/${userId}`),
  checkFollowing: (userId) => api.get(`/follow/check/${userId}`),
  getFollowers: (userId, page = 0) => api.get(`/follow/${userId}/followers?page=${page}`),
  getFollowing: (userId, page = 0) => api.get(`/follow/${userId}/following?page=${page}`),
  getMutual: (userId) => api.get(`/follow/${userId}/mutual`)
}

// ==================== LEADERBOARD ====================
export const leaderboardAPI = {
  getOverall: (limit = 50) => api.get(`/leaderboard?limit=${limit}`),
  getByType: (type, limit = 50) => api.get(`/leaderboard/type/${type}?limit=${limit}`),
  getReviewers: (type, limit = 50) => api.get(`/leaderboard/reviewers/${type}?limit=${limit}`)
}

// ==================== PAYMENTS ====================
export const paymentAPI = {
  createCheckout: (contentId) => api.post('/payments/checkout', { contentId }),
  confirmPayment: (sessionId, paymentId) => api.post(`/payments/confirm?sessionId=${sessionId}&paymentId=${paymentId}`),
  getMyPayments: (page = 0) => api.get(`/payments/my-payments?page=${page}`),
  getById: (paymentId) => api.get(`/payments/${paymentId}`)
}

// ==================== SUPPORT ====================
export const supportAPI = {
  createTicket: (data) => api.post('/support/tickets', data),
  getMyTickets: (page = 0) => api.get(`/support/tickets?page=${page}`),
  getTicketById: (id) => api.get(`/support/tickets/${id}`)
}

// ==================== ADMIN ====================
export const adminAPI = {
  getDashboard: () => api.get('/admin/dashboard'),
  getRevenue: () => api.get('/admin/revenue'),
  // Users
  getAllUsers: (page = 0) => api.get(`/admin/users?page=${page}`),
  toggleUserActive: (userId) => api.patch(`/admin/users/${userId}/toggle-active`),
  changeUserRole: (userId, role) => api.patch(`/admin/users/${userId}/role?role=${role}`),
  // Content
  createContent: (data) => api.post('/admin/content', data),
  updateContent: (id, data) => api.put(`/admin/content/${id}`, data),
  deleteContent: (id) => api.delete(`/admin/content/${id}`),
  // Tickets
  getAllTickets: (status, page = 0) => api.get(`/admin/tickets?status=${status || ''}&page=${page}`),
  respondToTicket: (id, data) => api.post(`/admin/tickets/${id}/respond`, data),
  updateTicketStatus: (id, status) => api.patch(`/admin/tickets/${id}/status?status=${status}`),
  // Payments
  getAllPayments: (page = 0) => api.get(`/admin/payments?page=${page}`),
  // Settings
  getSettings: () => api.get('/admin/settings'),
  updateSetting: (key, value) => api.put(`/admin/settings?key=${key}&value=${value}`)
}

export default api