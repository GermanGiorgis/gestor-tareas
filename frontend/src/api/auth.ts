import { apiClient } from "./client";
import type { Usuario } from "../types";

export interface AuthResponse {
  token: string;
  email: string;
  nombre: string;
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  const { data } = await apiClient.post<AuthResponse>("/api/auth/login", { email, password });
  return data;
}

export async function registrar(email: string, password: string, nombre: string): Promise<AuthResponse> {
  const { data } = await apiClient.post<AuthResponse>("/api/auth/registro", { email, password, nombre });
  return data;
}

export function guardarSesion(auth: AuthResponse) {
  localStorage.setItem("token", auth.token);
  const usuario: Usuario = { email: auth.email, nombre: auth.nombre };
  localStorage.setItem("usuario", JSON.stringify(usuario));
}

export function obtenerUsuarioGuardado(): Usuario | null {
  const raw = localStorage.getItem("usuario");
  return raw ? (JSON.parse(raw) as Usuario) : null;
}

export function cerrarSesion() {
  localStorage.removeItem("token");
  localStorage.removeItem("usuario");
}
