import api from './api';

const reviewService = {
  getByContent: async (contentId) => {
    const response = await api.get(`/reviews/content/${contentId}`);
    return response.data;
  },

  getById: async (reviewId) => {
    const response = await api.get(`/reviews/${reviewId}`);
    return response.data;
  },

  getUserReviews: async (userId) => {
    const response = await api.get(`/reviews/user/${userId}`);
    return response.data;
  },

  create: async (reviewData) => {
    const response = await api.post('/reviews', reviewData);
    return response.data;
  },

  update: async (reviewId, reviewData) => {
    const response = await api.put(`/reviews/${reviewId}`, reviewData);
    return response.data;
  },

  delete: async (reviewId) => {
    const response = await api.delete(`/reviews/${reviewId}`);
    return response.data;
  },

  like: async (reviewId) => {
    const response = await api.post(`/reviews/${reviewId}/like`);
    return response.data;
  },

  getLatest: async (page = 0, size = 10) => {
    const response = await api.get('/reviews/latest', {
      params: { page, size }
    });
    return response.data;
  }
};

export default reviewService;