import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { parseError, type FormError } from "../utils/errors";

const SIN_ERRORES: FormError = { general: null, campos: {} };

export function RegisterPage() {
  const { registrar } = useAuth();
  const navigate = useNavigate();
  const [nombre, setNombre] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errores, setErrores] = useState<FormError>(SIN_ERRORES);
  const [cargando, setCargando] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErrores(SIN_ERRORES);
    setCargando(true);
    try {
      await registrar(email, password, nombre);
      navigate("/");
    } catch (err) {
      setErrores(parseError(err, "No se pudo crear la cuenta."));
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="auth-page">
      <form className="auth-card" onSubmit={handleSubmit}>
        <h1>Crear cuenta</h1>
        {errores.general && <p className="auth-error">{errores.general}</p>}
        <label>
          Nombre
          <input type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} required />
          {errores.campos.nombre && <span className="field-error">{errores.campos.nombre}</span>}
        </label>
        <label>
          Email
          <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
          <span className="field-hint">Formato: nombre@dominio.com</span>
          {errores.campos.email && <span className="field-error">{errores.campos.email}</span>}
        </label>
        <label>
          Contraseña
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            minLength={6}
            required
          />
          <span className="field-hint">Mínimo 6 caracteres. Sin restricción de símbolos.</span>
          {errores.campos.password && <span className="field-error">{errores.campos.password}</span>}
        </label>
        <button type="submit" className="btn-primary" disabled={cargando}>
          {cargando ? "Creando cuenta..." : "Registrarme"}
        </button>
        <p className="auth-switch">
          ¿Ya tenés cuenta? <Link to="/login">Iniciá sesión</Link>
        </p>
      </form>
    </div>
  );
}
