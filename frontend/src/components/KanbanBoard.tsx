import { useState } from "react";
import {
  DndContext,
  DragOverlay,
  PointerSensor,
  closestCorners,
  useSensor,
  useSensors,
  type DragEndEvent,
  type DragStartEvent,
} from "@dnd-kit/core";
import type { EstadoTarea, Tarea } from "../types";
import { KanbanColumn } from "./KanbanColumn";
import { TaskCardView } from "./TaskCardView";

const COLUMNAS: { estado: EstadoTarea; titulo: string }[] = [
  { estado: "PENDIENTE", titulo: "Pendiente" },
  { estado: "EN_PROGRESO", titulo: "En progreso" },
  { estado: "COMPLETADA", titulo: "Completada" },
];

interface KanbanBoardProps {
  tareas: Tarea[];
  onEditar: (tarea: Tarea) => void;
  onEliminar: (id: number) => void;
  onMover: (tareaId: number, nuevoEstado: EstadoTarea, nuevoOrden: number) => void;
}

export function KanbanBoard({ tareas, onEditar, onEliminar, onMover }: KanbanBoardProps) {
  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 5 } }));
  const [tareaActiva, setTareaActiva] = useState<Tarea | null>(null);

  const columnas = COLUMNAS.map(({ estado, titulo }) => ({
    estado,
    titulo,
    tareas: tareas.filter((t) => t.estado === estado).sort((a, b) => a.orden - b.orden),
  }));

  function handleDragStart(event: DragStartEvent) {
    const tarea = tareas.find((t) => t.id === event.active.id);
    setTareaActiva(tarea ?? null);
  }

  function handleDragEnd(event: DragEndEvent) {
    setTareaActiva(null);
    const { active, over } = event;
    if (!over) return;

    const activa = tareas.find((t) => t.id === active.id);
    if (!activa) return;

    const esColumna = COLUMNAS.some((c) => c.estado === over.id);
    const estadoDestino: EstadoTarea = esColumna
      ? (over.id as EstadoTarea)
      : tareas.find((t) => t.id === over.id)?.estado ?? activa.estado;

    const columnaDestino = tareas
      .filter((t) => t.estado === estadoDestino && t.id !== activa.id)
      .sort((a, b) => a.orden - b.orden);

    let indiceDestino = columnaDestino.length;
    if (!esColumna) {
      indiceDestino = columnaDestino.findIndex((t) => t.id === over.id);
      if (indiceDestino === -1) indiceDestino = columnaDestino.length;
    }

    columnaDestino.splice(indiceDestino, 0, { ...activa, estado: estadoDestino });

    columnaDestino.forEach((tarea, index) => {
      if (tarea.id === activa.id || tarea.orden !== index) {
        onMover(tarea.id, estadoDestino, index);
      }
    });
  }

  return (
    <DndContext
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragEnd={handleDragEnd}
      onDragCancel={() => setTareaActiva(null)}
    >
      <div className="kanban-board">
        {columnas.map((col) => (
          <KanbanColumn
            key={col.estado}
            estado={col.estado}
            titulo={col.titulo}
            tareas={col.tareas}
            onEditar={onEditar}
            onEliminar={onEliminar}
          />
        ))}
      </div>
      <DragOverlay>
        {tareaActiva && (
          <div className="drag-overlay-card">
            <TaskCardView
              tarea={tareaActiva}
              onEditar={() => {}}
              onEliminar={() => {}}
              className={`task-card priority-${tareaActiva.prioridad.toLowerCase()}`}
            />
          </div>
        )}
      </DragOverlay>
    </DndContext>
  );
}
