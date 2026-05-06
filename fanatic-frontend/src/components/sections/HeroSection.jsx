import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { usePoints } from '../../hooks/usePoints';
import { formatNumber } from '../../utils/helpers';
import {
  MdMovie, MdMenuBook, MdTv, MdStar, MdTrendingUp,
  MdEmojiEvents, MdArrowForward
} from 'react-icons/md';
import './HeroSection.css';

const TAGLINES = [
  "Track what you watch. Share what you love.",
  "Your entertainment journey, all in one place.",
  "Rate. Review. Rise through the ranks.",
  "Movies. Series. Books. One obsession.",
  "Earn points. Level up. Become a Fanatic."
];

const HeroSection = ({ stats }) => {
  const { user } = useAuth();
  const { currentLevel, nextLevel, progress, points } = usePoints();
  const navigate = useNavigate();
  const [taglineIndex, setTaglineIndex] = useState(0);

  // Rotate taglines
  useEffect(() => {
    const interval = setInterval(() => {
      setTaglineIndex((prev) => (prev + 1) % TAGLINES.length);
    }, 4000);
    return () => clearInterval(interval);
  }, []);

  const quickActions = [
    {
      icon: <MdMovie />,
      label: 'Movies',
      description: 'Track & review films',
      path: '/movies',
      color: '#F59E0B',
      bg: 'rgba(245,158,11,0.1)',
      border: 'rgba(245,158,11,0.2)',
    },
    {
      icon: <MdTv />,
      label: 'Series',
      description: 'Log your binge-watches',
      path: '/series',
      color: '#06B6D4',
      bg: 'rgba(6,182,212,0.1)',
      border: 'rgba(6,182,212,0.2)',
    },
    {
      icon: <MdMenuBook />,
      label: 'Books',
      description: 'Catalog your reads',
      path: '/books',
      color: '#C2956B',
      bg: 'rgba(194,149,107,0.1)',
      border: 'rgba(194,149,107,0.2)',
    },
    {
      icon: <MdEmojiEvents />,
      label: 'Leaderboard',
      description: 'See top Fanatics',
      path: '/leaderboard',
      color: '#8B5CF6',
      bg: 'rgba(139,92,246,0.1)',
      border: 'rgba(139,92,246,0.2)',
    },
  ];

  return (
    <section className="hero-section">
      {/* Background Effects */}
      <div className="hero-bg-effects">
        <div className="hero-orb hero-orb-1" />
        <div className="hero-orb hero-orb-2" />
        <div className="hero-orb hero-orb-3" />
        <div className="hero-grid-overlay" />
      </div>

      <div className="hero-content">
        {/* ---- Left: Welcome & Level ---- */}
        <div className="hero-left">
          {/* Greeting */}
          <motion.div
            className="hero-greeting"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5 }}
          >
            <span className="hero-wave">👋</span>
            <span className="hero-greeting-text">
              Welcome back,
            </span>
          </motion.div>

          {/* User Name */}
          <motion.h1
            className="hero-username"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.1 }}
          >
            {user?.fullName || 'Fanatic'}
            {user?.verified && (
              <span className="hero-verified" title="Verified Fanatic">✓</span>
            )}
          </motion.h1>

          {/* Rotating Tagline */}
          <div className="hero-tagline-container">
            <AnimatePresence mode="wait">
              <motion.p
                key={taglineIndex}
                className="hero-tagline"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                transition={{ duration: 0.4 }}
              >
                {TAGLINES[taglineIndex]}
              </motion.p>
            </AnimatePresence>
          </div>

          {/* Level Card */}
          <motion.div
            className="hero-level-card"
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.5, delay: 0.3 }}
          >
            <div className="hero-level-icon">
              {currentLevel.icon}
            </div>
            <div className="hero-level-details">
              <div className="hero-level-top">
                <span className="hero-level-name">
                  Level {currentLevel.level} — {currentLevel.name}
                </span>
                <span className="hero-level-points">
                  {formatNumber(points)} pts
                </span>
              </div>
              <div className="hero-level-bar">
                <motion.div
                  className="hero-level-bar-fill"
                  initial={{ width: 0 }}
                  animate={{ width: `${Math.min(progress, 100)}%` }}
                  transition={{ duration: 1, delay: 0.5, ease: 'easeOut' }}
                />
              </div>
              <div className="hero-level-bottom">
                {nextLevel ? (
                  <span>
                    {nextLevel.minPoints - points} pts to{' '}
                    <strong>{nextLevel.icon} {nextLevel.name}</strong>
                  </span>
                ) : (
                  <span className="hero-max-level">🌟 Maximum level reached!</span>
                )}
              </div>
            </div>
          </motion.div>

          {/* Mini Stats Row */}
          {stats && (
            <motion.div
              className="hero-mini-stats"
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: 0.4 }}
            >
              <div className="hero-mini-stat">
                <MdMovie className="mini-stat-icon" style={{ color: '#F59E0B' }} />
                <span className="mini-stat-value">{stats.moviesCount || 0}</span>
                <span className="mini-stat-label">Movies</span>
              </div>
              <div className="hero-mini-stat-divider" />
              <div className="hero-mini-stat">
                <MdTv className="mini-stat-icon" style={{ color: '#06B6D4' }} />
                <span className="mini-stat-value">{stats.seriesCount || 0}</span>
                <span className="mini-stat-label">Series</span>
              </div>
              <div className="hero-mini-stat-divider" />
              <div className="hero-mini-stat">
                <MdMenuBook className="mini-stat-icon" style={{ color: '#C2956B' }} />
                <span className="mini-stat-value">{stats.booksCount || 0}</span>
                <span className="mini-stat-label">Books</span>
              </div>
              <div className="hero-mini-stat-divider" />
              <div className="hero-mini-stat">
                <MdStar className="mini-stat-icon" style={{ color: '#10B981' }} />
                <span className="mini-stat-value">{stats.reviewsCount || 0}</span>
                <span className="mini-stat-label">Reviews</span>
              </div>
            </motion.div>
          )}
        </div>

        {/* ---- Right: Quick Actions ---- */}
        <motion.div
          className="hero-right"
          initial={{ opacity: 0, x: 30 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.6, delay: 0.2 }}
        >
          <h3 className="hero-actions-title">
            <MdTrendingUp /> Quick Access
          </h3>
          <div className="hero-actions-grid">
            {quickActions.map((action, index) => (
              <motion.div
                key={action.label}
                className="hero-action-card"
                style={{
                  background: action.bg,
                  borderColor: action.border,
                }}
                onClick={() => navigate(action.path)}
                whileHover={{ scale: 1.03, y: -4 }}
                whileTap={{ scale: 0.98 }}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.3 + index * 0.1 }}
              >
                <div
                  className="hero-action-icon"
                  style={{ color: action.color }}
                >
                  {action.icon}
                </div>
                <div className="hero-action-info">
                  <span className="hero-action-label" style={{ color: action.color }}>
                    {action.label}
                  </span>
                  <span className="hero-action-desc">
                    {action.description}
                  </span>
                </div>
                <MdArrowForward
                  className="hero-action-arrow"
                  style={{ color: action.color }}
                />
              </motion.div>
            ))}
          </div>

          {/* Premiere Pass CTA */}
          <motion.div
            className="hero-premiere-cta"
            onClick={() => navigate('/premiere-pass')}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.7 }}
          >
            <div className="premiere-cta-glow" />
            <div className="premiere-cta-content">
              <span className="premiere-cta-badge">💎 PREMIERE PASS</span>
              <span className="premiere-cta-text">
                Access unreleased content & earn bonus points
              </span>
            </div>
            <MdArrowForward className="premiere-cta-arrow" />
          </motion.div>
        </motion.div>
      </div>
    </section>
  );
};

export default HeroSection;