// Integration suite: runs against local PostgreSQL; creates and removes one test user.
import 'dotenv/config';
import assert from 'node:assert/strict';
import { randomUUID } from 'node:crypto';
import { after, before, describe, it } from 'node:test';
import * as argon2 from 'argon2';
import { app } from '../dist/app.js';
import { prisma } from '../dist/config/database.js';

const email = `auth-test-${randomUUID()}@example.invalid`;
const password = 'SecureLocalTest#2026';
let port;
let server;
let userId;
let accessToken;
let refreshToken;
let secondRefresh;

async function api(path, method = 'GET', body, bearer) {
  const response = await fetch(`http://127.0.0.1:${port}${path}`, {
    method,
    headers: {
      ...(body === undefined ? {} : { 'content-type': 'application/json' }),
      ...(bearer === undefined ? {} : { authorization: `Bearer ${bearer}` }),
    },
    ...(body === undefined ? {} : { body: JSON.stringify(body) }),
  });
  const text = await response.text();
  return { status: response.status, body: text ? JSON.parse(text) : null };
}

if (process.env.NODE_ENV === 'production') {
  throw new Error('Do not run auth integration tests against a production database');
}

describe('BandUp auth integration', () => {
  before(async () => {
    await prisma.$connect();
    server = app.listen(0, '127.0.0.1');
    await new Promise((resolve, reject) => {
      server.once('listening', resolve);
      server.once('error', reject);
    });
    port = server.address().port;
  });
  after(async () => {
    if (server) await new Promise((resolve) => server.close(resolve));
    if (userId) await prisma.user.deleteMany({ where: { id: userId } });
    await prisma.$disconnect();
  });

  it('rejects missing and forged Bearer tokens', async () => {
    assert.equal((await api('/users/me')).status, 401);
    assert.equal((await api('/users/me', 'GET', undefined, 'not-a-real-jwt')).status, 401);
  });

  it('registers an account and stores only an Argon2id hash', async () => {
    const result = await api('/auth/register', 'POST', { email, password, fullName: 'Integration Tester' });
    assert.equal(result.status, 201, JSON.stringify(result.body));
    userId = result.body.user.id;
    accessToken = result.body.accessToken;
    refreshToken = result.body.refreshToken;
    assert.ok(accessToken && refreshToken);
    assert.equal(result.body.user.passwordHash, undefined);
    const dbUser = await prisma.user.findUniqueOrThrow({ where: { id: userId } });
    assert.ok(dbUser.passwordHash.startsWith('$argon2id$'));
    assert.notEqual(dbUser.passwordHash, password);
    assert.equal(await argon2.verify(dbUser.passwordHash, password), true);
    assert.equal((await api('/auth/register', 'POST', { email, password, fullName: 'Tester' })).status, 409);
  });

  it('rejects invalid login, accepts valid login', async () => {
    assert.equal((await api('/auth/login', 'POST', { email, password: 'wrong' })).status, 401);
    const result = await api('/auth/login', 'POST', { email, password });
    assert.equal(result.status, 200);
    accessToken = result.body.accessToken;
    refreshToken = result.body.refreshToken;
  });

  it('protects /users/me and permits authorized profile updates', async () => {
    const me = await api('/users/me', 'GET', undefined, accessToken);
    assert.equal(me.status, 200);
    assert.equal(me.body.user.email, email);
    assert.equal(me.body.user.passwordHash, undefined);
    const target = await api('/users/target-band', 'PUT', { targetBand: 7.5 }, accessToken);
    assert.equal(target.status, 200, JSON.stringify(target.body));
    assert.equal(target.body.user.targetBand, 7.5);
    assert.equal((await api('/users/target-band', 'PUT', { targetBand: 7.3 }, accessToken)).status, 400);
    const profile = await api('/users/me', 'PUT', { fullName: 'Updated Name', dailyStudyMinutes: 45 }, accessToken);
    assert.equal(profile.status, 200, JSON.stringify(profile.body));
    assert.equal(profile.body.user.fullName, 'Updated Name');
    assert.equal(profile.body.user.dailyStudyMinutes, 45);
  });

  it('rotates refresh token and rejects reuse', async () => {
    const first = await api('/auth/refresh', 'POST', { refreshToken });
    assert.equal(first.status, 200, JSON.stringify(first.body));
    secondRefresh = first.body.refreshToken;
    assert.notEqual(secondRefresh, refreshToken);
    assert.equal((await api('/auth/refresh', 'POST', { refreshToken })).status, 401);
  });

  it('revokes refresh token on logout', async () => {
    const result = await api('/auth/logout', 'POST', { refreshToken: secondRefresh });
    assert.equal(result.status, 204);
    assert.equal((await api('/auth/refresh', 'POST', { refreshToken: secondRefresh })).status, 401);
  });
});
