const Badge = ({ type, name, icon }) => (
  <span className={`badge badge-${type?.toLowerCase()}`} title={name}>
    {icon || '✅'} {name}
  </span>
)

export default Badge