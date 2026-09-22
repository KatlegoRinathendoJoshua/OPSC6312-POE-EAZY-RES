// server.js
// Entry point for the EazyRes REST API.
// Run locally with: npm install && npm start
// Then point the Android app's BASE_URL at http://10.0.2.2:3000/ (emulator)
// or http://<your-lan-ip>:3000/ (physical device on the same Wi-Fi),
// or deploy this folder to a host such as Render/Railway and use that
// public URL instead.
require('dotenv').config();
const express = require('express');
const cors = require('cors');

const { router: authRouter } = require('./routes/auth');
const propertiesRouter = require('./routes/properties');
const usersRouter = require('./routes/users');

const app = express();
app.use(cors());
app.use(express.json());

app.get('/', (req, res) => {
  res.json({ status: 'success', message: 'EazyRes API is running' });
});

app.use('/auth', authRouter);
app.use('/properties', propertiesRouter);
app.use('/users', usersRouter);

app.use((req, res) => {
  res.status(404).json({ status: 'error', message: 'Not found' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`EazyRes API listening on port ${PORT}`);
});
