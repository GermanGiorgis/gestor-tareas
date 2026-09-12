import type { CSSProperties, HTMLAttributes } from "react";
import type { Tarea } from "../types";

const PRIORIDAD_LABEL: Record<Tarea["prioridad"], string> = {
  BAJA: "Baja",
  MEDIA: "Media",
  ALTA: "Inmediata",
};

interface TaskCardViewProps {
  tarea: Tarea;
  onEditar: () => void;
  onEliminar: () => void;
  className: string;
  style?: CSSProperties;
  innerRef?: (node: HTMLElement | null) => void;
  dragHandleProps?: HTMLAttributes<HTMLDivElement>;
}

export function TaskCardView({
  tarea,
  onEditar,
  onEliminar,
  className,
  style,
  innerRef,
  dragHandleProps,
}: TaskCardViewProps) {
  return (
    <div ref={innerRef} style={style} className={className} {...dragHandleProps}>
      <div className="task-card-header">
        <span className={`priority-badge priority-${tarea.prioridad.toLowerCase()}`}>
          {PRIORIDAD_LABEL[tarea.prioridad]}
        </span>
        <button
          type="button"
          className="task-card-delete"
          onPointerDown={(e) => e.stopPropagation()}
          onClick={(e) => {
            e.stopPropagation();
            onEliminar();
          }}
          aria-label="Eliminar tarea"
        >
          ×
        </button>
      </div>
      <h3
        onPointerDown={(e) => e.stopPropagation()}
        onClick={(e) => {
          e.stopPropagation();
          onEditar();
        }}
      >
        {tarea.titulo}
      </h3>
      {tarea.descripcion && <p className="task-card-desc">{tarea.descripcion}</p>}
      <div className="task-card-footer">
        {tarea.categoria && (
          <span className="category-badge" style={{ backgroundColor: tarea.categoria.color }}>
            {tarea.categoria.nombre}
          </span>
        )}
        {tarea.fechaLimite && <span className="task-card-date">{tarea.fechaLimite}</span>}
        <span className="task-card-author">{tarea.creadoPorNombre}</span>
      </div>
    </div>
  );
}
