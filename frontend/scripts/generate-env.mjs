// Genera src/environments/*.ts a partir de ../.env (o de las variables de entorno, p. ej. en Railway).
import { existsSync, readFileSync, writeFileSync } from 'node:fs';
import { resolve, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const raiz = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const archivoEnv = resolve(raiz, '../.env');

const variables = { ...process.env };
if (existsSync(archivoEnv)) {
  for (const linea of readFileSync(archivoEnv, 'utf8').split(/\r?\n/)) {
    const coincidencia = linea.match(/^\s*([\w.]+)\s*=\s*(.*?)\s*$/);
    if (coincidencia && !linea.trim().startsWith('#')) {
      variables[coincidencia[1]] = coincidencia[2];
    }
  }
}

const requerida = (nombre) => {
  if (!variables[nombre]) {
    throw new Error(`Falta la variable ${nombre} (defínela en .env o como variable de entorno).`);
  }
  return variables[nombre];
};

// El WebSocket vive en el mismo host que la API: http -> ws, https -> wss.
const RUTA_WEBSOCKET = '/ws/citas';
const aWebSocket = (apiUrl) => (apiUrl ? apiUrl.replace(/^http/, 'ws') + RUTA_WEBSOCKET : '');

const plantilla = (production, apiUrl) =>
  `// Archivo generado por scripts/generate-env.mjs. No editar ni versionar.\n` +
  `export const environment = {\n  production: ${production},\n  apiUrl: '${apiUrl}',\n` +
  `  wsUrl: '${aWebSocket(apiUrl)}',\n};\n`;

const dir = resolve(raiz, 'src/environments');
const apiLocal = `http://localhost:${requerida('SERVER_PORT')}`;
writeFileSync(resolve(dir, 'environment.ts'), plantilla(false, apiLocal));

// La URL de producción solo es obligatoria cuando se construye para producción.
const apiProduccion = variables.API_URL_PRODUCTION || '';
if (!apiProduccion && process.argv.includes('--production')) requerida('API_URL_PRODUCTION');
writeFileSync(resolve(dir, 'environment.production.ts'), plantilla(true, apiProduccion));

console.log(`environments generados (local: ${apiLocal}, producción: ${apiProduccion || 'sin definir'})`);
