/**
 * Normaliza un texto para compararlo en una búsqueda: sin mayúsculas ni tildes ("Josué" → "josue"). La ñ también
 * pierde su tilde ("ñ" → "n"), lo habitual en un buscador.
 */
export function normalizarBusqueda(texto: string | null | undefined): string {
  return (texto ?? '')
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .toLowerCase()
    .trim();
}

/** `true` si `texto` contiene lo escrito en el buscador (vacío = coincide con todo), sin distinguir mayúsculas ni tildes. */
export function coincideBusqueda(texto: string, busqueda: string): boolean {
  const buscado = normalizarBusqueda(busqueda);
  return !buscado || normalizarBusqueda(texto).includes(buscado);
}
