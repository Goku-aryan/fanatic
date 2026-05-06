import api from './api';

const supportService = {
  createTicket: async (ticketData) => {
    const response = await api.post('/support/tickets', ticketData);
    return response.data;
  },

  getMyTickets: async () => {
    const response = await api.get('/support/tickets/mine');
    return response.data;
  },

  getTicketById: async (ticketId) => {
    const response = await api.get(`/support/tickets/${ticketId}`);
    return response.data;
  },

  replyToTicket: async (ticketId, message) => {
    const response = await api.post(`/support/tickets/${ticketId}/reply`, { message });
    return response.data;
  },

  getAllTickets: async (status = 'ALL', page = 0, size = 20) => {
    const response = await api.get('/support/tickets', {
      params: { status, page, size }
    });
    return response.data;
  },

  updateTicketStatus: async (ticketId, status) => {
    const response = await api.put(`/support/tickets/${ticketId}/status`, { status });
    return response.data;
  }
};

export default supportService;