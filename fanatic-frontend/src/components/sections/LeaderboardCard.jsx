import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { MdVerified, MdEmojiEvents } from 'react-icons/md';
import { getInitials, generateAvatarColor, formatNumber, getLevelInfo } from '../../utils/helpers';
import './LeaderboardCard.css';

const RANK_CONFIG = {
  1: {
    medal: '🥇',
    gradient: 'linear-gradient(135deg, rgba(255,215,0,0.15), rgba(255,215,0,0.05))',
    border: 'rgba(255,215,0,0.3)',
    glow: '0 0 30px rgba(255,215,0,0.15)',
    color: '#FFD700',
  },
  2: {
    medal: '🥈',
    gradient: 'linear-gradient(135deg, rgba(192,192,192,0.15), rgba(192,192,192,0.05))',
    border: 'rgba(192,192,192,0.3)',
    glow: '0 0 20px rgba(192,192,192,0.1)',
    color: '#C0C0C0',
  },
  3: {
    medal: '🥉',
    gradient: 'linear-gradient(135deg, rgba(205,127,50,0.15), rgba(205,127,50,0.05))',
    border: 'rgba(205,127,50,0.3)',
    glow: '0 0 20px rgba(205,127,50,0.1)',
    color: '#CD7F32',
  },
};

const LeaderboardCard = ({
  user,
  rank,
  points,
  category = 'overall', // 'movies', 'series', 'books', 'overall'
  count,
  variant = 'list', // 'list' | 'podium'
  animationDelay = 0,
}) => {
  const navigate = useNavigate();
  const rankConfig = RANK_CONFIG[rank];
  const isTopThree = rank <= 3;
  const levelInfo = getLevelInfo(user?.totalPoints || points || 0);
  const avatarColor = generateAvatarColor(user?.fullName || '');

  const categoryIcons = {
    movies: '🎬',
    series: '📺',
    books: '📚',
    overall: '🏆',
  };

  const handleClick = () => {
    navigate(`/user/${user?.id}`);
  };

  /* ============ PODIUM VARIANT ============ */
  if (variant === 'podium') {
    return (
      <motion.div
        className={`lb-podium-card lb-podium-rank-${rank}`}
        onClick={handleClick}
        initial={{ opacity: 0, y: 40 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: animationDelay, duration: 0.6, type: 'spring' }}
        whileHover={{ y: -6 }}
      >
        {/* Crown for #1 */}
        {rank === 1 && (
          <motion.div
            className="lb-podium-crown"
            initial={{ scale: 0, rotate: -30 }}
            animate={{ scale: 1, rotate: 0 }}
            transition={{ delay: animationDelay + 0.3, type: 'spring', stiffness: 300 }}
          >
            👑
          </motion.div>
        )}

        {/* Avatar */}
        <div
          className="lb-podium-avatar"
          style={{
            background: avatarColor,
            borderColor: rankConfig?.color || 'var(--primary)',
            boxShadow: rankConfig?.glow || 'none',
          }}
        >
          {user?.avatarUrl ? (
            <img src={user.avatarUrl} alt={user.fullName} />
          ) : (
            getInitials(user?.fullName)
          )}
        </div>

        {/* Medal */}
        <div className="lb-podium-medal">{rankConfig?.medal}</div>

        {/* Name */}
        <div className="lb-podium-name">
          <span>{user?.fullName || 'Unknown'}</span>
          {user?.verified && <MdVerified className="lb-verified-icon" />}
        </div>

        {/* Level */}
        <div className="lb-podium-level">
          {levelInfo.currentLevel.icon} {levelInfo.currentLevel.name}
        </div>

        {/* Points */}
        <div
          className="lb-podium-points"
          style={{ color: rankConfig?.color || 'var(--primary-light)' }}
        >
          {formatNumber(points)} pts
        </div>

        {/* Count */}
        {count !== undefined && (
          <div className="lb-podium-count">
            {categoryIcons[category]} {count} {category === 'books' ? 'read' : 'watched'}
          </div>
        )}

        {/* Podium Bar */}
        <div
          className="lb-podium-bar"
          style={{
            background: rankConfig?.gradient,
            borderColor: rankConfig?.border,
          }}
        />
      </motion.div>
    );
  }

  /* ============ LIST VARIANT (default) ============ */
  return (
    <motion.div
      className={`lb-list-card ${isTopThree ? 'lb-list-top' : ''}`}
      onClick={handleClick}
      style={
        isTopThree
          ? { background: rankConfig.gradient, borderColor: rankConfig.border }
          : {}
      }
      initial={{ opacity: 0, x: -20 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ delay: animationDelay, duration: 0.3 }}
      whileHover={{ x: 6, borderColor: 'var(--primary)' }}
    >
      {/* Rank */}
      <div
        className="lb-list-rank"
        style={isTopThree ? { color: rankConfig.color } : {}}
      >
        {isTopThree ? rankConfig.medal : `#${rank}`}
      </div>

      {/* Avatar */}
      <div
        className="lb-list-avatar"
        style={{
          background: avatarColor,
          borderColor: isTopThree ? rankConfig.color : 'transparent',
        }}
      >
        {user?.avatarUrl ? (
          <img src={user.avatarUrl} alt={user.fullName} />
        ) : (
          getInitials(user?.fullName)
        )}
      </div>

      {/* Info */}
      <div className="lb-list-info">
        <div className="lb-list-name">
          <span>{user?.fullName || 'Unknown'}</span>
          {user?.verified && <MdVerified className="lb-verified-icon" />}
        </div>
        <div className="lb-list-meta">
          <span className="lb-list-level">
            {levelInfo.currentLevel.icon} {levelInfo.currentLevel.name}
          </span>
          {count !== undefined && (
            <>
              <span className="lb-list-dot">·</span>
              <span className="lb-list-count">
                {categoryIcons[category]} {count}
              </span>
            </>
          )}
        </div>
      </div>

      {/* Points */}
      <div className="lb-list-points">
        <span
          className="lb-list-points-value"
          style={isTopThree ? { color: rankConfig.color } : {}}
        >
          {formatNumber(points)}
        </span>
        <span className="lb-list-points-label">points</span>
      </div>

      {/* Rank change indicator (optional future feature) */}
      {user?.rankChange !== undefined && user.rankChange !== 0 && (
        <div className={`lb-list-change ${user.rankChange > 0 ? 'up' : 'down'}`}>
          {user.rankChange > 0 ? '▲' : '▼'} {Math.abs(user.rankChange)}
        </div>
      )}
    </motion.div>
  );
};

export default LeaderboardCard;