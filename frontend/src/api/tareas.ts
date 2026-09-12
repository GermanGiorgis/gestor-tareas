import { apiClient } from "./client";
import type { EstadoTarea, Tarea, TareaInput } from "../types";

export async function listarTareas(
  paqueteId: number,
  filtros?: { estado?: EstadoTarea; categoriaId?: number; texto?: string }
) {
  const { data } = await apiClient.get<Tarea[]>(`/api/paquetes/${paqueteId}/tareas`, { params: filtros });
  return data;
}

export async function crearTarea(paqueteId: number, input: TareaInput) {
  const { data } = await apiClient.post<Tarea>(`/api/paquetes/${paqueteId}/tareas`, input);
  return data;
}

export async function actualizarTarea(paqueteId: number, id: number, input: TareaInput) {
  const { data } = await apiClient.put<Tarea>(`/api/paquetes/${paqueteId}/tareas/${id}`, input);
  return data;
}

export async function moverTarea(paqueteId: number, id: number, estado: EstadoTarea, orden: number) {
  const { data } = await apiClient.patch<Tarea>(`/api/paquetes/${paqueteId}/tareas/${id}/mover`, { estado, orden });
  return data;
}

export async function eliminarTarea(paqueteId: number, id: number) {
  await apiClient.delete(`/api/paquetes/${paqueteId}/tareas/${id}`);
}
