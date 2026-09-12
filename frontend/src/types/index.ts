export type EstadoTarea = "PENDIENTE" | "EN_PROGRESO" | "COMPLETADA";
export type Prioridad = "BAJA" | "MEDIA" | "ALTA";

export interface Categoria {
  id: number;
  nombre: string;
  color: string;
}

export interface Tarea {
  id: number;
  titulo: string;
  descripcion: string | null;
  estado: EstadoTarea;
  prioridad: Prioridad;
  fechaLimite: string | null;
  orden: number;
  categoria: Categoria | null;
  creadoPorNombre: string;
}

export type RolPaquete = "PROPIETARIO" | "MIEMBRO";

export interface Paquete {
  id: number;
  nombre: string;
  miRol: RolPaquete;
  cantidadMiembros: number;
}

export interface Miembro {
  usuarioId: number;
  nombre: string;
  email: string;
  rol: RolPaquete;
}

export type EstadoInvitacion = "PENDIENTE" | "ACEPTADA" | "RECHAZADA";

export interface Invitacion {
  id: number;
  paqueteId: number;
  paqueteNombre: string;
  emailInvitado: string;
  invitadoPorNombre: string;
  estado: EstadoInvitacion;
  createdAt: string;
}

export interface TareaInput {
  titulo: string;
  descripcion?: string | null;
  estado?: EstadoTarea;
  prioridad?: Prioridad;
  fechaLimite?: string | null;
  categoriaId?: number | null;
}

export interface Usuario {
  email: string;
  nombre: string;
}
