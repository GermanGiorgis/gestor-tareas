import { useState, type FormEvent } from "react";
import type { Paquete } from "../types";

interface PackageSelectorProps {
  paquetes: Paquete[];
  paqueteActivoId: number | null;
  onSeleccionar: (id: number) => void;
  onCrear: (nombre: string) => void;
  onCompartir: () => void;
}

export function PackageSelector({ paquetes, paqueteActivoId, onSeleccionar, onCrear, onCompartir }: PackageSelectorProps) {
  const [creando, setCreando] = useState(false);
  const [nombre, setNombre] = useState("");

  function handleCrear(e: FormEvent) {
    e.preventDefault();
    if (!nombre.trim()) return;
    onCrear(nombre.trim());
    setNombre("");
    setCreando(false);
  }

  return (
    <div className="package-selector">
      <select
        className="estado-filter"
        value={paqueteActivoId ?? ""}
        onChange={(e) => onSeleccionar(Number(e.target.value))}
      >
        {paquetes.map((p) => (
          <option key={p.id} value={p.id}>
            {p.nombre} {p.cantidadMiembros > 1 ? `· ${p.cantidadMiembros} miembros` : ""}
          </option>
        ))}
      </select>

      <button type="button" className="btn-secondary" onClick={onCompartir}>
        Compartir
      </button>

      {creando ? (
        <form className="category-new-form" onSubmit={handleCrear}>
          <input
            autoFocus
            placeholder="Nombre del paquete"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
          />
          <button type="submit" className="btn-primary">
            Crear
          </button>
          <button type="button" className="btn-secondary" onClick={() => setCreando(false)}>
            Cancelar
          </button>
        </form>
      ) : (
        <button type="button" className="category-chip category-chip-add" onClick={() => setCreando(true)}>
          + Paquete
        </button>
      )}
    </div>
  );
}
