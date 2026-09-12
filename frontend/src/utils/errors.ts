import { AxiosError } from "axios";

interface ApiErrorBody {
  error?: string;
  errores?: Record<string, string>;
}

export interface FormError {
  general: string | null;
  campos: Record<string, string>;
}

export function parseError(err: unknown, mensajeGenerico: string): FormError {
  if (!(err instanceof AxiosError)) {
    return { general: mensajeGenerico, campos: {} };
  }

  if (!err.response) {
    return { general: "No se pudo conectar con el servidor. Verificá tu conexión e intentá de nuevo.", campos: {} };
  }

  const data = err.response.data as ApiErrorBody | undefined;

  if (data?.errores && Object.keys(data.errores).length > 0) {
    return { general: null, campos: data.errores };
  }

  return { general: data?.error ?? mensajeGenerico, campos: {} };
}
