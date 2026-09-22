// db.js
// Sets up a local SQLite database file (eazyres.db) and creates the
// tables described in the Planning & Design document (Section 5.8),
// trimmed to what Part 2 of the POE actually needs: users + properties.
const Database = require('better-sqlite3');
const { v4: uuidv4 } = require('uuid');

const db = new Database('eazyres.db');
db.pragma('journal_mode = WAL');

db.exec(`
  CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    full_name TEXT NOT NULL,
    phone TEXT,
    user_type TEXT NOT NULL DEFAULT 'student',
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
  );

  CREATE TABLE IF NOT EXISTS properties (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    city TEXT NOT NULL,
    price REAL NOT NULL,
    property_type TEXT NOT NULL,
    description TEXT,
    amenities TEXT,          -- JSON-encoded array
    photo_url TEXT,
    rating_avg REAL DEFAULT 0,
    is_verified INTEGER DEFAULT 0,
    is_nsfas_approved INTEGER DEFAULT 0,
    available_rooms INTEGER DEFAULT 0
  );
`);

// Seed a handful of demo properties the first time the DB is created,
// so the app has something to show right after cloning the repo.
const count = db.prepare('SELECT COUNT(*) AS c FROM properties').get().c;
if (count === 0) {
  const insert = db.prepare(`
    INSERT INTO properties
      (id, name, address, city, price, property_type, description, amenities, photo_url, rating_avg, is_verified, is_nsfas_approved, available_rooms)
    VALUES (@id, @name, @address, @city, @price, @property_type, @description, @amenities, @photo_url, @rating_avg, @is_verified, @is_nsfas_approved, @available_rooms)
  `);

  const seed = [
    {
      id: uuidv4(), name: 'Campus Heights', address: '123 Jan Smuts Ave', city: 'Johannesburg',
      price: 4500, property_type: 'private_room',
      description: 'A bright, quiet private room five minutes from campus, with fast Wi-Fi and 24/7 security.',
      amenities: JSON.stringify(['Wi-Fi', 'Security', 'Parking']),
      photo_url: 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800',
      rating_avg: 4.8, is_verified: 1, is_nsfas_approved: 1, available_rooms: 3
    },
    {
      id: uuidv4(), name: 'Ivy Residence', address: '45 Kingsway', city: 'Johannesburg',
      price: 3200, property_type: 'shared_apartment',
      description: 'Shared apartment close to Wits, walking distance to shops and the train station.',
      amenities: JSON.stringify(['Wi-Fi', 'Laundry']),
      photo_url: 'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800',
      rating_avg: 4.3, is_verified: 1, is_nsfas_approved: 0, available_rooms: 1
    },
    {
      id: uuidv4(), name: 'Stellenbosch Studios', address: '8 Dorp Street', city: 'Stellenbosch',
      price: 5200, property_type: 'studio',
      description: 'Self-contained studio unit, fully furnished, five minutes from the main campus.',
      amenities: JSON.stringify(['Wi-Fi', 'Security', 'Kitchenette']),
      photo_url: 'https://images.unsplash.com/photo-1502672023488-70e25813eb80?w=800',
      rating_avg: 4.9, is_verified: 1, is_nsfas_approved: 1, available_rooms: 2
    }
  ];
  const insertMany = db.transaction((rows) => rows.forEach((r) => insert.run(r)));
  insertMany(seed);
}

module.exports = db;
