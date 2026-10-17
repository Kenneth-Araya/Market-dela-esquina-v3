// Tarjeta de estadistica reutilizable del panel de administracion
function StatCard({ etiqueta, valor }) {
  return (
    <div className="col-6 col-lg-3">
      <div className="admin-stat-card">
        <div className="admin-stat-label">{etiqueta}</div>
        <div className="admin-stat-num">{valor}</div>
      </div>
    </div>
  )
}

export default StatCard
