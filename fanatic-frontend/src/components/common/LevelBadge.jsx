import { getLevelIcon, getLevelTitle } from '../../utils/helpers'

const LevelBadge = ({ level, points }) => (
  <div className="level-badge">
    <span className="level-icon">{getLevelIcon(level)}</span>
    <div className="level-info">
      <span className="level-title">Lv.{level} {getLevelTitle(level)}</span>
      {points !== undefined && <span className="level-points">{points} pts</span>}
    </div>
  </div>
)

export default LevelBadge