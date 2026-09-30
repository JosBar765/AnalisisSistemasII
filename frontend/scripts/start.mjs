// Levanta `ng serve` en el puerto FRONTEND_PORT del .env (4200 si no está definido).
import { spawn } from 'node:child_process';
import { variables } from './cargar-env.mjs';

const puerto = variables.FRONTEND_PORT || '4200';
console.log(`ng serve en el puerto ${puerto}`);
const proceso = spawn('ng', ['serve', '--port', puerto, ...process.argv.slice(2)], { stdio: 'inherit', shell: true });
proceso.on('exit', (codigo) => process.exit(codigo ?? 0));
