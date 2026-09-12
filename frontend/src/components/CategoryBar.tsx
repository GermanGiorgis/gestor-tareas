import { useState, type FormEvent } from "react";
import type { Categoria } from "../types";

const COLORES_SUGERIDOS = ["#3caf5f", "#ff8a3d", "#3d8bff", "#a855f7", "#e0527a", "#14b8a6", "#eab308"];

interface CategoryBarProps {
  categorias: Categoria[];
  categoriaSeleccionada: number | null;
  onSeleccionar: (id: number | null) => void;
  onCrear: (nombre: string, color: string) => void;
  onEliminar: (id: number) => void;
}

export function CategoryBar({ categorias, categoriaSeleccionada, onSeleccionar, onCrear, onEliminar }: CategoryBarProps) {
  const [creando, setCreando] = useState(false);
  const [nombre, setNombre] = useState("");
  const [color, setColor] = useState(COLORES_SUGERIDOS[0]);

  function handleCrear(e: FormEvent) {
    e.preventDefault();
    if (!nombre.trim()) return;
    onCrear(nombre.trim(), color);
    setNombre("");
    setCreando(false);
  }

  return (
    <div className="category-bar">
      <button
        type="button"
        className={`category-chip ${categoriaSeleccionada === null ? "active" : ""}`}
        onClick={() => onSeleccionar(null)}
      >
        Todas
      </button>
      {categorias.map((c) => (
        <span key={c.id} className={`category-chip ${categoriaSeleccionada === c.id ? "active" : ""}`}>
          <button type="button" style={{ color: c.color }} onClick={() => onSeleccionar(c.id)}>
            {c.nombre}
          </button>
          <button type="button" className="category-chip-remove" onClick={() => onEliminar(c.id)} aria-label={`Eliminar ${c.nombre}`}>
            ×
          </button>
        </span>
      ))}

      {creando ? (
        <form className="category-new-form" onSubmit={handleCrear}>
          <input
            autoFocus
            placeholder="Nombre"
            value={nombre}
            onChange={(e) => setNombre(e.target.value)}
          />
          <select value={color} onChange={(e) => setColor(e.target.value)}>
            {COLORES_SUGERIDOS.map((c) => (
              <option key={c} value={c} style={{ color: c }}>
                ●
              </option>
            ))}
          </select>
          <button type="submit" className="btn-primary">Agregar</button>
          <button type="button" className="btn-secondary" onClick={() => setCreando(false)}>Cancelar</button>
        </form>
      ) : (
        <button type="button" className="category-chip category-chip-add" onClick={() => setCreando(true)}>
          + Categoría
        </button>
      )}
    </div>
  );
}
