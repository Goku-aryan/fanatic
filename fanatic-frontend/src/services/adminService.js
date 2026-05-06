import api from './api';

const adminService = {
  getDashboardStats: async () => {
    const response = await api.get('/admin/stats');
    return response.data;
  },

  getAnalytics: async (period = '30d') => {
    const response = await api.get('/admin/analytics', {
      params: { period }
    });
    return response.data;
  },

  getAllUsers: async (page = 0, size = 20, search = '') => {
    const response = await api.get('/admin/users', {
      params: { page, size, search }
    });
    return response.data;
  },

  updateUserRole: async (userId, role) => {
    const response = await api.put(`/admin/users/${userId}/role`, { role });
    return response.data;
  },

  banUser: async (userId) => {
    const response = await api.put(`/admin/users/${userId}/ban`);
    return response.data;
  },

  unbanUser: async (userId) => {
    const response = await api.put(`/admin/users/${userId}/unban`);
    return response.data;
  },

  verifyUser: async (userId) => {
    const response = await api.put(`/admin/users/${userId}/verify`);
    return response.data;
  },

  getAllContent: async (type = 'ALL', page = 0, size = 20) => {
    const response = await api.get('/admin/content', {
      params: { type, page, size }
    });
    return response.data;
  },

  deleteContent: async (contentId) => {
    const response = await api.delete(`/admin/content/${contentId}`);
    return response.data;
  },

  addPremiereContent: async (contentData) => {
    const response = await api.post('/admin/premiere-content', contentData);
    return response.data;
  },

  getPremiereContent: async () => {
    const response = await api.get('/admin/premiere-content');
    return response.data;
  },

  updateSettings: async (settings) => {
    const response = await api.put('/admin/settings', settings);
    return response.data;
  },

  getSettings: async () => {
    const response = await api.get('/admin/settings');
    return response.data;
  }
};

export default adminService;