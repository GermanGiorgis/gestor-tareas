import { apiClient } from "./client";
import type { Categoria } from "../types";

export async function listarCategorias() {
  const { data } = await apiClient.get<Categoria[]>("/api/categorias");
  return data;
}

export async function crearCategoria(nombre: string, color: string) {
  const { data } = await apiClient.post<Categoria>("/api/categorias", { nombre, color });
  return data;
}

export async function eliminarCategoria(id: number) {
  await apiClient.delete(`/api/categorias/${id}`);
}
