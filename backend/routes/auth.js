// routes/auth.js
// Registration, login and settings-update endpoints.
// Passwords are hashed with bcrypt before they are ever written to the
// database, and the plain password is never returned in a response.
const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { v4: uuidv4 } = require('uuid');
const db = require('../db');

const router = express.Router();
const JWT_SECRET = process.env.JWT_SECRET || 'dev_secret_change_me';

function toPublicUser(row) {
  return {
    id: row.id,
    email: row.email,
    fullName: row.full_name,
    phone: row.phone,
    userType: row.user_type
  };
}

function authMiddleware(req, res, next) {
  const header = req.headers.authorization || '';
  const token = header.startsWith('Bearer ') ? header.slice(7) : null;
  if (!token) return res.status(401).json({ status: 'error', message: 'Missing token' });
  try {
    req.userId = jwt.verify(token, JWT_SECRET).userId;
    next();
  } catch {
    res.status(401).json({ status: 'error', message: 'Invalid or expired token' });
  }
}

// POST /auth/register
router.post('/register', async (req, res) => {
  const { email, password, fullName, phone, userType } = req.body;

  if (!email || !password || !fullName) {
    return res.status(400).json({ status: 'error', message: 'email, password and fullName are required' });
  }
  if (password.length < 8) {
    return res.status(400).json({ status: 'error', message: 'Password must be at least 8 characters' });
  }

  const existing = db.prepare('SELECT id FROM users WHERE email = ?').get(email);
  if (existing) {
    return res.status(409).json({ status: 'error', message: 'An account with that email already exists' });
  }

  const passwordHash = await bcrypt.hash(password, 12); // bcrypt salts + hashes the password
  const id = uuidv4();

  db.prepare(`
    INSERT INTO users (id, email, password_hash, full_name, phone, user_type)
    VALUES (?, ?, ?, ?, ?, ?)
  `).run(id, email, passwordHash, fullName, phone || null, userType || 'student');

  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(id);
  const token = jwt.sign({ userId: id }, JWT_SECRET, { expiresIn: '7d' });

  res.status(201).json({ status: 'success', token, user: toPublicUser(user) });
});

// POST /auth/login
router.post('/login', async (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ status: 'error', message: 'email and password are required' });
  }

  const user = db.prepare('SELECT * FROM users WHERE email = ?').get(email);
  if (!user) {
    return res.status(401).json({ status: 'error', message: 'Invalid email or password' });
  }

  const matches = await bcrypt.compare(password, user.password_hash);
  if (!matches) {
    return res.status(401).json({ status: 'error', message: 'Invalid email or password' });
  }

  const token = jwt.sign({ userId: user.id }, JWT_SECRET, { expiresIn: '7d' });
  res.json({ status: 'success', token, user: toPublicUser(user) });
});

module.exports = { router, authMiddleware, toPublicUser, JWT_SECRET };
