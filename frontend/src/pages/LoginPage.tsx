import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { parseError, type FormError } from "../utils/errors";

const SIN_ERRORES: FormError = { general: null, campos: {} };

export function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errores, setErrores] = useState<FormError>(SIN_ERRORES);
  const [cargando, setCargando] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErrores(SIN_ERRORES);
    setCargando(true);
    try {
      await login(email, password);
      navigate("/");
    } catch (err) {
      setErrores(parseError(err, "No se pudo iniciar sesión. Verificá tus credenciales."));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Iniciar sesión</h1>
        {errores.general && <p className="auth-error">{errores.general}</p>}
        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          {errores.campos.email && <span className="field-error">{errores.campos.email}</span>}
        </label>
        <label>
          Contraseña
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          {errores.campos.password && <span className="field-error">{errores.campos.password}</span>}
        </label>
        <button type="submit" className="btn-primary" disabled={cargando}>
          {cargando ? "Ingresando..." : "Ingresar"}
        </button>
        <p className="auth-switch">
          ¿No tenés cuenta? <Link to="/registro">Registrate</Link>
        </p>
      </form>
    </div>
  );
}
