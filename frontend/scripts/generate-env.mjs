// Genera src/environments/*.ts a partir de ../.env (o de las variables de entorno, p. ej. en Railway).
import { mkdirSync, writeFileSync } from 'node:fs';
import { resolve } from 'node:path';
import { raiz, requerida, variables } from './cargar-env.mjs';

// El WebSocket vive en el mismo host que la API: http -> ws, https -> wss.
const RUTA_WEBSOCKET = '/ws/citas';
const aWebSocket = (apiUrl) => (apiUrl ? apiUrl.replace(/^http/, 'ws') + RUTA_WEBSOCKET : '');

const plantilla = (production, apiUrl) =>
  `// Archivo generado por scripts/generate-env.mjs. No editar ni versionar.\n` +
  `export const environment = {\n  production: ${production},\n  apiUrl: '${apiUrl}',\n` +
  `  wsUrl: '${aWebSocket(apiUrl)}',\n};\n`;

const dir = resolve(raiz, 'src/environments');
// Git no versiona carpetas vacías ni los archivos generados: la carpeta puede no existir.
mkdirSync(dir, { recursive: true });
const apiLocal = `http://localhost:${requerida('SERVER_PORT')}`;
writeFileSync(resolve(dir, 'environment.ts'), plantilla(false, apiLocal));

// La URL de producción solo es obligatoria cuando se construye para producción.
const apiProduccion = variables.API_URL_PRODUCTION || '';
if (!apiProduccion && process.argv.includes('--production')) requerida('API_URL_PRODUCTION');
writeFileSync(resolve(dir, 'environment.production.ts'), plantilla(true, apiProduccion));

console.log(`environments generados (local: ${apiLocal}, producción: ${apiProduccion || 'sin definir'})`);
