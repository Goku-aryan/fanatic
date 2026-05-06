import api from './api';

const leaderboardService = {
  getMovieLeaderboard: async (page = 0, size = 20) => {
    const response = await api.get('/leaderboard/movies', {
      params: { page, size }
    });
    return response.data;
  },

  getSeriesLeaderboard: async (page = 0, size = 20) => {
    const response = await api.get('/leaderboard/series', {
      params: { page, size }
    });
    return response.data;
  },

  getBookLeaderboard: async (page = 0, size = 20) => {
    const response = await api.get('/leaderboard/books', {
      params: { page, size }
    });
    return response.data;
  },

  getOverallLeaderboard: async (page = 0, size = 20) => {
    const response = await api.get('/leaderboard/overall', {
      params: { page, size }
    });
    return response.data;
  }
};

export default leaderboardService;