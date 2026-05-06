import api from './api';

const paymentService = {
  createCheckoutSession: async (contentId, contentType) => {
    const response = await api.post('/payments/create-checkout-session', {
      contentId,
      contentType
    });
    return response.data;
  },

  verifyPayment: async (sessionId) => {
    const response = await api.post('/payments/verify', { sessionId });
    return response.data;
  },

  getMyPurchases: async () => {
    const response = await api.get('/payments/my-purchases');
    return response.data;
  },

  getAllPayments: async (page = 0, size = 20) => {
    const response = await api.get('/payments', {
      params: { page, size }
    });
    return response.data;
  },

  getPaymentStats: async () => {
    const response = await api.get('/payments/stats');
    return response.data;
  },

  redirectToCheckout: async (contentId, contentType) => {
    const session = await paymentService.createCheckoutSession(contentId, contentType);
    window.location.href = session.checkoutUrl;
  }
};

export default paymentService;