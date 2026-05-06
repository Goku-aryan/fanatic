import api from './api';

const contentService = {
  // Movies
  getMovies: async (page = 0, size = 12, search = '', genre = '') => {
    const response = await api.get('/content/movies', {
      params: { page, size, search, genre }
    });
    return response.data;
  },

  // Series
  getSeries: async (page = 0, size = 12, search = '', genre = '') => {
    const response = await api.get('/content/series', {
      params: { page, size, search, genre }
    });
    return response.data;
  },

  // Books
  getBooks: async (page = 0, size = 12, search = '', genre = '') => {
    const response = await api.get('/content/books', {
      params: { page, size, search, genre }
    });
    return response.data;
  },

  // Get single content by ID
  getById: async (id) => {
    const response = await api.get(`/content/${id}`);
    return response.data;
  },

  // Add content (movie/series/book)
  addContent: async (contentData) => {
    const response = await api.post('/content', contentData);
    return response.data;
  },

  // Mark as watched/read
  markAsConsumed: async (contentId) => {
    const response = await api.post(`/content/${contentId}/consume`);
    return response.data;
  },

  // Get user's content
  getUserContent: async (userId, type = 'ALL') => {
    const response = await api.get(`/content/user/${userId}`, {
      params: { type }
    });
    return response.data;
  },

  // Get trending
  getTrending: async (type = 'ALL') => {
    const response = await api.get('/content/trending', {
      params: { type }
    });
    return response.data;
  },

  // Delete content
  deleteContent: async (id) => {
    const response = await api.delete(`/content/${id}`);
    return response.data;
  },

  // Update content
  updateContent: async (id, contentData) => {
    const response = await api.put(`/content/${id}`, contentData);
    return response.data;
  }
};

export default contentService;