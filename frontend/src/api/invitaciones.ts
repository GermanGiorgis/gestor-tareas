import { apiClient } from "./client";
import type { Invitacion } from "../types";

export async function misInvitaciones() {
  const { data } = await apiClient.get<Invitacion[]>("/api/invitaciones");
  return data;
}

export async function aceptarInvitacion(id: number) {
  await apiClient.post(`/api/invitaciones/${id}/aceptar`);
}

export async function rechazarInvitacion(id: number) {
  await apiClient.post(`/api/invitaciones/${id}/rechazar`);
}
