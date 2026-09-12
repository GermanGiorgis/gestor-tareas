import type { Invitacion } from "../types";

interface PendingInvitationsProps {
  invitaciones: Invitacion[];
  onAceptar: (id: number) => void;
  onRechazar: (id: number) => void;
}

export function PendingInvitations({ invitaciones, onAceptar, onRechazar }: PendingInvitationsProps) {
  if (invitaciones.length === 0) return null;

  return (
    <div className="invitations-bar">
      {invitaciones.map((inv) => (
        <div key={inv.id} className="invitation-chip">
          <span>
            <strong>{inv.invitadoPorNombre}</strong> te invitó a "{inv.paqueteNombre}"
          </span>
          <div className="invitation-chip-actions">
            <button type="button" className="btn-primary" onClick={() => onAceptar(inv.id)}>
              Aceptar
            </button>
            <button type="button" className="btn-secondary" onClick={() => onRechazar(inv.id)}>
              Rechazar
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
