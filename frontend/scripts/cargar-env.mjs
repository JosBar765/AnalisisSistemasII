// Lee ../.env (si existe) sobre las variables de entorno del proceso (p. ej. las de Railway).
import { existsSync, readFileSync } from 'node:fs';
import { resolve, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

export const raiz = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const archivoEnv = resolve(raiz, '../.env');

export const variables = { ...process.env };
if (existsSync(archivoEnv)) {
  for (const linea of readFileSync(archivoEnv, 'utf8').split(/\r?\n/)) {
    const coincidencia = linea.match(/^\s*([\w.]+)\s*=\s*(.*?)\s*$/);
    if (coincidencia && !linea.trim().startsWith('#')) {
      variables[coincidencia[1]] = coincidencia[2];
    }
  }
}

export const requerida = (nombre) => {
  if (!variables[nombre]) {
    throw new Error(`Falta la variable ${nombre} (defínela en .env o como variable de entorno).`);
  }
  return variables[nombre];
};
