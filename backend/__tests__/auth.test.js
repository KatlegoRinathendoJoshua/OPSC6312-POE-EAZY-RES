const request = require('supertest');

// Use a throwaway test database so tests don't touch eazyres.db
process.env.JWT_SECRET = 'test_secret';

jest.mock('../db', () => {
  const Database = require('better-sqlite3');
  const db = new Database(':memory:');
  db.exec(`
    CREATE TABLE users (
      id TEXT PRIMARY KEY, email TEXT UNIQUE, password_hash TEXT,
      full_name TEXT, phone TEXT, user_type TEXT DEFAULT 'student'
    );
    CREATE TABLE properties (
      id TEXT PRIMARY KEY, name TEXT, address TEXT, city TEXT, price REAL,
      property_type TEXT, description TEXT, amenities TEXT, photo_url TEXT,
      rating_avg REAL, is_verified INTEGER, is_nsfas_approved INTEGER, available_rooms INTEGER
    );
  `);
  return db;
});

const express = require('express');
const { router: authRouter } = require('../routes/auth');

const app = express();
app.use(express.json());
app.use('/auth', authRouter);

describe('Auth endpoints', () => {
  const newUser = {
    email: 'student@university.ac.za',
    password: 'securePassword123',
    fullName: 'Thabo Mokoena',
    phone: '+27721234567'
  };

  test('registers a new user and returns a token', async () => {
    const res = await request(app).post('/auth/register').send(newUser);
    expect(res.status).toBe(201);
    expect(res.body.status).toBe('success');
    expect(res.body.token).toBeDefined();
    expect(res.body.user.email).toBe(newUser.email);
    expect(res.body.user.password).toBeUndefined(); // password must never be returned
  });

  test('rejects a duplicate email', async () => {
    const res = await request(app).post('/auth/register').send(newUser);
    expect(res.status).toBe(409);
  });

  test('rejects registration with a short password', async () => {
    const res = await request(app).post('/auth/register').send({
      ...newUser, email: 'other@university.ac.za', password: '123'
    });
    expect(res.status).toBe(400);
  });

  test('logs in with correct credentials', async () => {
    const res = await request(app).post('/auth/login').send({
      email: newUser.email, password: newUser.password
    });
    expect(res.status).toBe(200);
    expect(res.body.token).toBeDefined();
  });

  test('rejects login with the wrong password', async () => {
    const res = await request(app).post('/auth/login').send({
      email: newUser.email, password: 'wrongPassword'
    });
    expect(res.status).toBe(401);
  });
});
