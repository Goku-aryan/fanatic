import { useState, useEffect } from 'react'
import { adminAPI } from '../../services/api'
import toast from 'react-hot-toast'

const AdminSettings = () => {
  const [settings, setSettings] = useState({})
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => { loadSettings() }, [])

  const loadSettings = async () => {
    try {
      const res = await adminAPI.getSettings()
      setSettings(res.data.data || {})
    } catch (err) { console.error(err) }
    finally { setLoading(false) }
  }

  const handleUpdate = async (key, value) => {
    setSaving(true)
    try {
      const res = await adminAPI.updateSetting(key, value)
      setSettings(res.data.data || {})
      toast.success(`Setting "${key}" updated! ✅`)
    } catch (err) {
      toast.error('Failed to update setting')
    } finally { setSaving(false) }
  }

  const settingsConfig = [
    {
      key: 'site_name',
      label: 'Site Name',
      description: 'The name displayed across the platform',
      type: 'text'
    },
    {
      key: 'points_per_add',
      label: 'Points per Content Add',
      description: 'Points awarded when user adds a movie/series/book',
      type: 'number'
    },
    {
      key: 'points_per_review',
      label: 'Points per Review',
      description: 'Points awarded when user writes a review',
      type: 'number'
    },
    {
      key: 'points_per_early_review',
      label: 'Points per Early Access Review',
      description: 'Points awarded for First Look reviews',
      type: 'number'
    },
    {
      key: 'verified_follower_threshold',
      label: 'Verified Badge Threshold',
      description: 'Number of followers needed for verified badge',
      type: 'number'
    },
    {
      key: 'maintenance_mode',
      label: 'Maintenance Mode',
      description: 'Enable/disable maintenance mode',
      type: 'toggle'
    }
  ]

  if (loading) return <div className="admin-loader">Loading settings...</div>

  return (
    <div className="admin-settings">
      <h1>⚙️ Site Settings</h1>
      <p className="settings-description">
        Configure your Fanatic platform settings below.
      </p>

      <div className="settings-list">
        {settingsConfig.map(config => (
          <div key={config.key} className="setting-card">
            <div className="setting-info">
              <h3>{config.label}</h3>
              <p>{config.description}</p>
            </div>
            <div className="setting-control">
              {config.type === 'toggle' ? (
                <label className="toggle-switch">
                  <input
                    type="checkbox"
                    checked={settings[config.key] === 'true'}
                    onChange={(e) => handleUpdate(config.key, e.target.checked ? 'true' : 'false')}
                  />
                  <span className="toggle-slider"></span>
                  <span className="toggle-label">
                    {settings[config.key] === 'true' ? 'ON' : 'OFF'}
                  </span>
                </label>
              ) : (
                <div className="setting-input-group">
                  <input
                    type={config.type}
                    value={settings[config.key] || ''}
                    onChange={(e) => setSettings({ ...settings, [config.key]: e.target.value })}
                    className="setting-input"
                  />
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={() => handleUpdate(config.key, settings[config.key])}
                    disabled={saving}
                  >
                    Save
                  </button>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>

      {/* LEVEL SYSTEM INFO */}
      <div className="settings-section">
        <h2>🎮 Level System</h2>
        <div className="levels-table">
          <table className="admin-table">
            <thead>
              <tr><th>Level</th><th>Icon</th><th>Title</th><th>Points Required</th></tr>
            </thead>
            <tbody>
              <tr><td>1</td><td>🌱</td><td>Newbie</td><td>0</td></tr>
              <tr><td>2</td><td>👀</td><td>Casual Viewer</td><td>100</td></tr>
              <tr><td>3</td><td>🔥</td><td>Enthusiast</td><td>300</td></tr>
              <tr><td>4</td><td>⭐</td><td>Devoted Fan</td><td>600</td></tr>
              <tr><td>5</td><td>💎</td><td>Super Fanatic</td><td>1,000</td></tr>
              <tr><td>6</td><td>🏆</td><td>Elite Critic</td><td>1,500</td></tr>
              <tr><td>7</td><td>👑</td><td>Master Reviewer</td><td>2,200</td></tr>
              <tr><td>8</td><td>🌟</td><td>Legend</td><td>3,000</td></tr>
              <tr><td>9</td><td>⚡</td><td>Mythic</td><td>4,000</td></tr>
              <tr><td>10</td><td>🔱</td><td>Immortal Fanatic</td><td>5,000</td></tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  )
}

export default AdminSettings