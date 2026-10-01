/**
 * Mensaje que explica un error de la API. El Backend responde `{ message }` con la causa real (dato duplicado,
 * campo obligatorio, registro en uso...); si no hay respuesta (sin conexión) se usa el texto por defecto.
 */
export function mensajeDeError(error: unknown, porDefecto: string): string {
  const mensaje = (error as { error?: { message?: unknown } } | null)?.error?.message;
  return typeof mensaje === 'string' && mensaje ? mensaje : porDefecto;
}
