import api from './api';

const userService = {
  getProfile: async (username) => {
    const response = await api.get(`/users/${username}`);
    return response.data;
  },

  follow: async (userId) => {
    const response = await api.post(`/users/${userId}/follow`);
    return response.data;
  },

  unfollow: async (userId) => {
    const response = await api.delete(`/users/${userId}/follow`);
    return response.data;
  },

  getFollowers: async (userId) => {
    const response = await api.get(`/users/${userId}/followers`);
    return response.data;
  },

  getFollowing: async (userId) => {
    const response = await api.get(`/users/${userId}/following`);
    return response.data;
  },

  search: async (query) => {
    const response = await api.get('/users/search', {
      params: { q: query }
    });
    return response.data;
  },

  getStats: async (userId) => {
    const response = await api.get(`/users/${userId}/stats`);
    return response.data;
  }
};

export default userService;