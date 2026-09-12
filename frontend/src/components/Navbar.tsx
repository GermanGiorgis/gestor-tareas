import { useAuth } from "../context/AuthContext";

export function Navbar() {
  const { usuario, logout } = useAuth();

  return (
    <nav className="navbar">
      <span className="navbar-brand">Gestor de Tareas</span>
      <div className="navbar-user">
        <span>{usuario?.nombre}</span>
        <button type="button" className="btn-secondary" onClick={logout}>
          Salir
        </button>
      </div>
    </nav>
  );
}
