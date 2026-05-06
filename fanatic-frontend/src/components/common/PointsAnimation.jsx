import React, { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import './PointsAnimation.css';

const PointsAnimation = ({ points, trigger, message = '', type = 'default' }) => {
  const [particles, setParticles] = useState([]);
  const [showMain, setShowMain] = useState(false);

  // Color schemes per type
  const colorMap = {
    default: { primary: '#8B5CF6', secondary: '#A78BFA', emoji: '✨' },
    movie: { primary: '#F59E0B', secondary: '#FBBF24', emoji: '🎬' },
    series: { primary: '#06B6D4', secondary: '#22D3EE', emoji: '📺' },
    book: { primary: '#C2956B', secondary: '#D4A574', emoji: '📚' },
    review: { primary: '#10B981', secondary: '#34D399', emoji: '⭐' },
    premiere: { primary: '#EC4899', secondary: '#F472B6', emoji: '💎' },
    levelup: { primary: '#FFD700', secondary: '#FDE68A', emoji: '🏆' },
  };

  const colors = colorMap[type] || colorMap.default;

  const generateParticles = useCallback(() => {
    const newParticles = [];
    const count = type === 'levelup' ? 20 : 12;

    for (let i = 0; i < count; i++) {
      newParticles.push({
        id: Date.now() + i,
        x: Math.random() * 200 - 100,
        y: -(Math.random() * 150 + 50),
        rotation: Math.random() * 360,
        scale: Math.random() * 0.5 + 0.5,
        delay: Math.random() * 0.3,
        emoji: ['✨', '⭐', '🌟', '💫', '🔥'][Math.floor(Math.random() * 5)],
      });
    }
    return newParticles;
  }, [type]);

  useEffect(() => {
    if (trigger && points > 0) {
      setShowMain(true);
      setParticles(generateParticles());

      const timer = setTimeout(() => {
        setShowMain(false);
        setParticles([]);
      }, 2500);

      return () => clearTimeout(timer);
    }
  }, [trigger, points, generateParticles]);

  if (!showMain) return null;

  return (
    <div className="points-animation-container">
      <AnimatePresence>
        {/* Main Points Display */}
        <motion.div
          className="points-main"
          initial={{ scale: 0, opacity: 0, y: 20 }}
          animate={{ scale: 1, opacity: 1, y: 0 }}
          exit={{ scale: 0, opacity: 0, y: -30 }}
          transition={{
            type: 'spring',
            stiffness: 400,
            damping: 15,
          }}
        >
          {/* Glow Ring */}
          <motion.div
            className="points-glow-ring"
            style={{
              borderColor: colors.primary,
              boxShadow: `0 0 40px ${colors.primary}40, 0 0 80px ${colors.primary}20`,
            }}
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: [0.8, 1.3, 1], opacity: [0, 1, 0.6] }}
            transition={{ duration: 0.6 }}
          />

          {/* Icon */}
          <motion.div
            className="points-emoji"
            initial={{ scale: 0, rotate: -180 }}
            animate={{ scale: [0, 1.4, 1], rotate: 0 }}
            transition={{ delay: 0.1, duration: 0.5, type: 'spring' }}
          >
            {colors.emoji}
          </motion.div>

          {/* Points Value */}
          <motion.div
            className="points-value"
            style={{ color: colors.primary }}
            initial={{ scale: 0 }}
            animate={{ scale: [0, 1.2, 1] }}
            transition={{ delay: 0.2, duration: 0.4, type: 'spring' }}
          >
            +{points}
          </motion.div>

          {/* Label */}
          <motion.div
            className="points-label"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.35 }}
          >
            {message || 'Points Earned!'}
          </motion.div>

          {/* Progress shimmer */}
          <motion.div
            className="points-shimmer"
            style={{
              background: `linear-gradient(90deg, transparent, ${colors.primary}30, transparent)`,
            }}
            initial={{ x: '-100%' }}
            animate={{ x: '200%' }}
            transition={{ delay: 0.5, duration: 0.8 }}
          />
        </motion.div>

        {/* Floating Particles */}
        {particles.map((particle) => (
          <motion.div
            key={particle.id}
            className="points-particle"
            initial={{
              x: 0,
              y: 0,
              scale: 0,
              opacity: 1,
              rotate: 0,
            }}
            animate={{
              x: particle.x,
              y: particle.y,
              scale: [0, particle.scale, 0],
              opacity: [0, 1, 0],
              rotate: particle.rotation,
            }}
            transition={{
              duration: 1.5,
              delay: particle.delay,
              ease: 'easeOut',
            }}
          >
            {particle.emoji}
          </motion.div>
        ))}

        {/* Level Up Special Effect */}
        {type === 'levelup' && (
          <>
            <motion.div
              className="levelup-burst"
              style={{ borderColor: colors.primary }}
              initial={{ scale: 0, opacity: 1 }}
              animate={{ scale: 3, opacity: 0 }}
              transition={{ duration: 1, ease: 'easeOut' }}
            />
            <motion.div
              className="levelup-burst"
              style={{ borderColor: colors.secondary }}
              initial={{ scale: 0, opacity: 1 }}
              animate={{ scale: 2.5, opacity: 0 }}
              transition={{ duration: 1, delay: 0.15, ease: 'easeOut' }}
            />
          </>
        )}
      </AnimatePresence>
    </div>
  );
};

/* ============================================
   Reusable hook to trigger the animation
   ============================================ */
export const usePointsAnimation = () => {
  const [animationState, setAnimationState] = useState({
    points: 0,
    trigger: 0,
    message: '',
    type: 'default',
  });

  const showPointsAnimation = useCallback((points, message = '', type = 'default') => {
    setAnimationState({
      points,
      trigger: Date.now(), // unique trigger value
      message,
      type,
    });
  }, []);

  const PointsAnimationComponent = (
    <PointsAnimation
      points={animationState.points}
      trigger={animationState.trigger}
      message={animationState.message}
      type={animationState.type}
    />
  );

  return { showPointsAnimation, PointsAnimationComponent };
};

export default PointsAnimation;