import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { MdCancel, MdArrowBack } from 'react-icons/md';

const PaymentCancel = () => {
  const navigate = useNavigate();

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
        <MdCancel style={{ fontSize: '4rem', color: '#F59E0B', marginBottom: '16px' }} />
        <h2 style={{ marginBottom: '8px' }}>Payment Cancelled</h2>
        <p style={{ color: '#a1a1aa', marginBottom: '24px', lineHeight: '1.6' }}>
          No worries! You can try again anytime from the First Look section.
        </p>
        <div style={{ display: 'flex', gap: '12px', justifyContent: 'center' }}>
          <button onClick={() => navigate('/')}
            style={{
              padding: '12px 24px', background: '#1a1a2e', color: '#e4e4e7',
              border: '1px solid #2a2a3e', borderRadius: '8px', cursor: 'pointer',
              fontSize: '0.9rem', fontWeight: '600', display: 'inline-flex',
              alignItems: 'center', gap: '8px'
            }}>
            <MdArrowBack /> Home
          </button>
          <button onClick={() => navigate('/first-look')}
            style={{
              padding: '12px 24px', background: '#8B5CF6', color: 'white',
              border: 'none', borderRadius: '8px', cursor: 'pointer',
              fontSize: '0.9rem', fontWeight: '600'
            }}>
            Try Again
          </button>
        </div>
      </motion.div>
    </div>
  );
};

export default PaymentCancel;