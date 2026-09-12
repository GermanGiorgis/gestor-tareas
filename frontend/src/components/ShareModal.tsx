import { useEffect, useState, type FormEvent } from "react";
import { AxiosError } from "axios";
import * as paquetesApi from "../api/paquetes";
import type { Miembro, Paquete } from "../types";

interface ShareModalProps {
  paquete: Paquete;
  onCerrar: () => void;
}

export function ShareModal({ paquete, onCerrar }: ShareModalProps) {
  const [miembros, setMiembros] = useState<Miembro[]>([]);
  const [email, setEmail] = useState("");
  const [cargando, setCargando] = useState(false);
  const [mensaje, setMensaje] = useState<{ tipo: "ok" | "error"; texto: string } | null>(null);

  const esPropietario = paquete.miRol === "PROPIETARIO";

  async function cargarMiembros() {
    const data = await paquetesApi.listarMiembros(paquete.id);
    setMiembros(data);
  }

  useEffect(() => {
    cargarMiembros();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paquete.id]);

  async function handleInvitar(e: FormEvent) {
    e.preventDefault();
    setMensaje(null);
    setCargando(true);
    try {
      await paquetesApi.invitar(paquete.id, email);
      setMensaje({ tipo: "ok", texto: `Invitación enviada a ${email}` });
      setEmail("");
    } catch (err) {
      const texto = err instanceof AxiosError ? err.response?.data?.error : null;
      setMensaje({ tipo: "error", texto: texto ?? "No se pudo invitar a ese email." });
    } finally {
      setCargando(false);
    }
  }

  async function handleEliminarMiembro(usuarioId: number) {
    await paquetesApi.eliminarMiembro(paquete.id, usuarioId);
    cargarMiembros();
  }

  return (
    <div className="modal-overlay" onClick={onCerrar}>
      <form className="modal-card" onClick={(e) => e.stopPropagation()} onSubmit={handleInvitar}>
        <h2>Compartir "{paquete.nombre}"</h2>

        <div className="member-list">
          {miembros.map((m) => (
            <div key={m.usuarioId} className="member-row">
              <div>
                <strong>{m.nombre}</strong>
                <span className="member-email">{m.email}</span>
              </div>
              <div className="member-row-right">
                <span className={`member-role ${m.rol === "PROPIETARIO" ? "owner" : ""}`}>
                  {m.rol === "PROPIETARIO" ? "Propietario" : "Miembro"}
                </span>
                {esPropietario && m.rol !== "PROPIETARIO" && (
                  <button
                    type="button"
                    className="task-card-delete"
                    onClick={() => handleEliminarMiembro(m.usuarioId)}
                    aria-label={`Quitar a ${m.nombre}`}
                  >
                    ×
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>

        {esPropietario ? (
          <>
            <label>
              Invitar por email
              <input
                type="email"
                placeholder="amigo@ejemplo.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </label>
            {mensaje && <p className={mensaje.tipo === "error" ? "field-error" : "field-hint"}>{mensaje.texto}</p>}
            <div className="modal-actions">
              <button type="button" className="btn-secondary-light" onClick={onCerrar}>
                Cerrar
              </button>
              <button type="submit" className="btn-primary" disabled={cargando}>
                {cargando ? "Invitando..." : "Invitar"}
              </button>
            </div>
          </>
        ) : (
          <div className="modal-actions">
            <button type="button" className="btn-primary" onClick={onCerrar}>
              Cerrar
            </button>
          </div>
        )}
      </form>
    </div>
  );
}
