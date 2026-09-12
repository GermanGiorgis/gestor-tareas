import { useState, type FormEvent } from "react";
import type { Categoria, Prioridad, Tarea, TareaInput } from "../types";

interface TaskModalProps {
  tarea: Tarea | null;
  categorias: Categoria[];
  onGuardar: (input: TareaInput) => void;
  onCerrar: () => void;
}

export function TaskModal({ tarea, categorias, onGuardar, onCerrar }: TaskModalProps) {
  const [titulo, setTitulo] = useState(tarea?.titulo ?? "");
  const [descripcion, setDescripcion] = useState(tarea?.descripcion ?? "");
  const [prioridad, setPrioridad] = useState<Prioridad>(tarea?.prioridad ?? "MEDIA");
  const [fechaLimite, setFechaLimite] = useState(tarea?.fechaLimite ?? "");
  const [categoriaId, setCategoriaId] = useState<string>(tarea?.categoria?.id.toString() ?? "");

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    onGuardar({
      titulo,
      descripcion: descripcion || null,
      prioridad,
      fechaLimite: fechaLimite || null,
      categoriaId: categoriaId ? Number(categoriaId) : null,
    });
  }

  return (
    <div className="modal-overlay" onClick={onCerrar}>
      <form className="modal-card" onClick={(e) => e.stopPropagation()} onSubmit={handleSubmit}>
        <h2>{tarea ? "Editar tarea" : "Nueva tarea"}</h2>
        <label>
          Título
          <input value={titulo} onChange={(e) => setTitulo(e.target.value)} required />
        </label>
        <label>
          Descripción
          <textarea value={descripcion ?? ""} onChange={(e) => setDescripcion(e.target.value)} rows={3} />
        </label>
        <div className="modal-row">
          <label>
            Prioridad
            <select value={prioridad} onChange={(e) => setPrioridad(e.target.value as Prioridad)}>
              <option value="BAJA">Baja</option>
              <option value="MEDIA">Media</option>
              <option value="ALTA">Inmediata</option>
            </select>
          </label>
          <label>
            Fecha límite
            <input type="date" value={fechaLimite ?? ""} onChange={(e) => setFechaLimite(e.target.value)} />
          </label>
        </div>
        <label>
          Categoría
          <select value={categoriaId} onChange={(e) => setCategoriaId(e.target.value)}>
            <option value="">Sin categoría</option>
            {categorias.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombre}
              </option>
            ))}
          </select>
        </label>
        <div className="modal-actions">
          <button type="button" className="btn-secondary-light" onClick={onCerrar}>
            Cancelar
          </button>
          <button type="submit" className="btn-primary">
            Guardar
          </button>
        </div>
      </form>
    </div>
  );
}
