import { useDroppable } from "@dnd-kit/core";
import { SortableContext, verticalListSortingStrategy } from "@dnd-kit/sortable";
import type { EstadoTarea, Tarea } from "../types";
import { TaskCard } from "./TaskCard";

interface KanbanColumnProps {
  estado: EstadoTarea;
  titulo: string;
  tareas: Tarea[];
  onEditar: (tarea: Tarea) => void;
  onEliminar: (id: number) => void;
}

export function KanbanColumn({ estado, titulo, tareas, onEditar, onEliminar }: KanbanColumnProps) {
  const { setNodeRef } = useDroppable({ id: estado });

  return (
    <div className="kanban-column">
      <div className="kanban-column-header">
        <h2>{titulo}</h2>
        <span className="kanban-column-count">{tareas.length}</span>
      </div>
      <div ref={setNodeRef} className="kanban-column-body">
        <SortableContext items={tareas.map((t) => t.id)} strategy={verticalListSortingStrategy}>
          {tareas.map((tarea) => (
            <TaskCard
              key={tarea.id}
              tarea={tarea}
              onEditar={() => onEditar(tarea)}
              onEliminar={() => onEliminar(tarea.id)}
            />
          ))}
          {tareas.length === 0 && <p className="kanban-column-empty">Sin tareas</p>}
        </SortableContext>
      </div>
    </div>
  );
}
