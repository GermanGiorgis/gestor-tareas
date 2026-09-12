import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import type { Tarea } from "../types";
import { TaskCardView } from "./TaskCardView";

interface TaskCardProps {
  tarea: Tarea;
  onEditar: () => void;
  onEliminar: () => void;
}

export function TaskCard({ tarea, onEditar, onEliminar }: TaskCardProps) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: tarea.id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.35 : 1,
  };

  return (
    <TaskCardView
      tarea={tarea}
      onEditar={onEditar}
      onEliminar={onEliminar}
      innerRef={setNodeRef}
      style={style}
      className={`task-card priority-${tarea.prioridad.toLowerCase()}${isDragging ? " is-dragging" : ""}`}
      dragHandleProps={{ ...attributes, ...listeners }}
    />
  );
}
