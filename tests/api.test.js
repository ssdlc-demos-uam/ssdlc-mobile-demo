import request from 'supertest';
import { describe, expect, it } from 'vitest';
import { createApp } from '../src/app.js';

const secret = 'una-clave-de-prueba-larga-y-no-reutilizable-123456';
function app() { return createApp({ jwtSecret: secret }); }

async function registerAndLogin(testApp, email, password = 'Contraseña-segura-2026') {
  await request(testApp).post('/api/auth/register').send({ email, password }).expect(201);
  const response = await request(testApp).post('/api/auth/login').send({ email, password }).expect(200);
  return response.body.token;
}

describe('API segura de tareas', () => {
  it('publica una comprobación de salud sin datos internos', async () => {
    const response = await request(app()).get('/health').expect(200);
    expect(response.body).toEqual({ status: 'ok' });
  });

  it('rechaza contraseñas que no cumplen el requisito', async () => {
    const response = await request(app()).post('/api/auth/register').send({ email: 'aula@example.test', password: 'corta' }).expect(400);
    expect(response.body.error).toBe('Solicitud no válida');
  });

  it('no entrega el hash de la contraseña al registrar', async () => {
    const response = await request(app()).post('/api/auth/register').send({ email: 'aula@example.test', password: 'Contraseña-segura-2026' }).expect(201);
    expect(response.body).toEqual({ id: '1', email: 'aula@example.test' });
  });

  it('requiere autenticación para crear tareas', async () => {
    await request(app()).post('/api/tasks').send({ title: 'No autorizada' }).expect(401);
  });

  it('aísla las tareas entre dos usuarios (previene IDOR)', async () => {
    const testApp = app();
    const anaToken = await registerAndLogin(testApp, 'ana@example.test');
    const brunoToken = await registerAndLogin(testApp, 'bruno@example.test');
    const task = await request(testApp).post('/api/tasks').set('Authorization', `Bearer ${anaToken}`).send({ title: 'Tarea privada' }).expect(201);
    await request(testApp).patch(`/api/tasks/${task.body.id}`).set('Authorization', `Bearer ${brunoToken}`).send({ done: true }).expect(404);
    const anaTasks = await request(testApp).get('/api/tasks').set('Authorization', `Bearer ${anaToken}`).expect(200);
    expect(anaTasks.body[0].done).toBe(false);
  });

  it('rechaza propiedades no esperadas en la entrada', async () => {
    const testApp = app();
    const token = await registerAndLogin(testApp, 'validacion@example.test');
    await request(testApp).post('/api/tasks').set('Authorization', `Bearer ${token}`).send({ title: 'Prueba', ownerId: '2' }).expect(400);
  });
});
