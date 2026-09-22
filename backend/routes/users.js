// routes/users.js
// Settings-update endpoint (PUT /users/settings), matching the
// "user must be able to change their settings" requirement.
const express = require('express');
const jwt = require('jsonwebtoken');
const db = require('../db');
const { authMiddleware, toPublicUser, JWT_SECRET } = require('./auth');

const router = express.Router();

router.put('/settings', authMiddleware, (req, res) => {
  const { fullName, phone } = req.body;
  if (!fullName) {
    return res.status(400).json({ status: 'error', message: 'fullName is required' });
  }

  db.prepare('UPDATE users SET full_name = ?, phone = ? WHERE id = ?')
    .run(fullName, phone || null, req.userId);

  const user = db.prepare('SELECT * FROM users WHERE id = ?').get(req.userId);
  const token = jwt.sign({ userId: user.id }, JWT_SECRET, { expiresIn: '7d' });
  res.json({ status: 'success', token, user: toPublicUser(user) });
});

module.exports = router;
