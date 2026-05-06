import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import { MdCheckCircle, MdArrowForward } from 'react-icons/md';
import paymentService from '../../services/paymentService';
import { toast } from 'react-toastify';

const PaymentSuccess = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [verifying, setVerifying] = useState(true);
  const [verified, setVerified] = useState(false);

  useEffect(() => {
    const sessionId = searchParams.get('session_id');
    if (sessionId) {
      verifyPayment(sessionId);
    } else {
      setVerifying(false);
    }
  }, [searchParams]);

  const verifyPayment = async (sessionId) => {
    try {
      await paymentService.verifyPayment(sessionId);
      setVerified(true);
      toast.success('Payment successful! 🎉');
    } catch (err) {
      toast.error('Payment verification failed');
    } finally {
      setVerifying(false);
    }
  };

  return (
    <div style={{
      minHeight: 'calc(100vh - 70px)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '20px'
    }}>
      <motion.div
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        style={{
          textAlign: 'center',
          background: 'var(--dark-card)',
          border: '1px solid var(--dark-border)',
          borderRadius: '16px',
          padding: '48px',
          maxWidth: '500px'
        }}
      >
        {verifying ? (
          <>
            <h2>Verifying Payment...</h2>
            <p style={{ color: 'var(--dark-text-secondary)' }}>
              Please wait while we confirm your purchase
            </p>
          </>
        ) : verified ? (
          <>
            <MdCheckCircle style={{ fontSize: '4rem', color: '#10B981', marginBottom: '16px' }} />
            <h2 style={{ marginBottom: '8px' }}>Payment Successful! 🎉</h2>
            <p style={{ color: '#a1a1aa', marginBottom: '24px' }}>
              Your Premiere Pass content is now unlocked.
            </p>
            <button onClick={() => navigate('/first-look')}
              style={{
                padding: '12px 24px', background: '#8B5CF6', color: 'white',
                border: 'none', borderRadius: '8px', cursor: 'pointer',
                fontSize: '1rem', fontWeight: '600', display: 'inline-flex',
                alignItems: 'center', gap: '8px'
              }}>
              View Content <MdArrowForward />
            </button>
          </>
        ) : (
          <>
            <h2 style={{ marginBottom: '8px' }}>Something went wrong</h2>
            <p style={{ color: '#a1a1aa', marginBottom: '24px' }}>
              We couldn't verify your payment. Please contact support.
            </p>
            <button onClick={() => navigate('/support')}
              style={{
                padding: '12px 24px', background: '#8B5CF6', color: 'white',
                border: 'none', borderRadius: '8px', cursor: 'pointer',
                fontSize: '1rem', fontWeight: '600'
              }}>
              Contact Support
            </button>
          </>
        )}
      </motion.div>
    </div>
  );
};

export default PaymentSuccess;