export const getLevelIcon = (level) => {
  const icons = {
    1: '🌱', 2: '👀', 3: '🔥', 4: '⭐', 5: '💎',
    6: '🏆', 7: '👑', 8: '🌟', 9: '⚡', 10: '🔱'
  }
  return icons[level] || '🌱'
}

export const getLevelTitle = (level) => {
  const titles = {
    1: 'Newbie', 2: 'Casual Viewer', 3: 'Enthusiast', 4: 'Devoted Fan',
    5: 'Super Fanatic', 6: 'Elite Critic', 7: 'Master Reviewer',
    8: 'Legend', 9: 'Mythic', 10: 'Immortal Fanatic'
  }
  return titles[level] || 'Newbie'
}

export const formatDate = (date) => {
  return new Date(date).toLocaleDateString('en-US', {
    year: 'numeric', month: 'short', day: 'numeric'
  })
}

export const truncate = (str, len = 100) => {
  if (!str) return ''
  return str.length > len ? str.substring(0, len) + '...' : str
}