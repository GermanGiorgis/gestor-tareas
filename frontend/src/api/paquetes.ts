import { apiClient } from "./client";
import type { Miembro, Paquete } from "../types";

export async function listarPaquetes() {
  const { data } = await apiClient.get<Paquete[]>("/api/paquetes");
  return data;
}

export async function crearPaquete(nombre: string) {
  const { data } = await apiClient.post<Paquete>("/api/paquetes", { nombre });
  return data;
}

export async function eliminarPaquete(id: number) {
  await apiClient.delete(`/api/paquetes/${id}`);
}

export async function listarMiembros(paqueteId: number) {
  const { data } = await apiClient.get<Miembro[]>(`/api/paquetes/${paqueteId}/miembros`);
  return data;
}

export async function eliminarMiembro(paqueteId: number, usuarioId: number) {
  await apiClient.delete(`/api/paquetes/${paqueteId}/miembros/${usuarioId}`);
}

export async function invitar(paqueteId: number, email: string) {
  const { data } = await apiClient.post(`/api/paquetes/${paqueteId}/invitaciones`, { email });
  return data;
}
