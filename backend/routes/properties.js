// routes/properties.js
// Read endpoints for property listings, matching Section 5.3 of the
// Planning & Design document. Filtering by city/min/max price is
// supported via query params, matching the Browse/Search screen.
const express = require('express');
const db = require('../db');

const router = express.Router();

function toPublicProperty(row) {
  return {
    id: row.id,
    name: row.name,
    address: row.address,
    city: row.city,
    price: row.price,
    propertyType: row.property_type,
    description: row.description,
    amenities: JSON.parse(row.amenities || '[]'),
    photoUrl: row.photo_url,
    ratingAvg: row.rating_avg,
    isVerified: !!row.is_verified,
    isNsfasApproved: !!row.is_nsfas_approved,
    availableRooms: row.available_rooms
  };
}

// GET /properties?city=&minPrice=&maxPrice=
router.get('/', (req, res) => {
  const { city, minPrice, maxPrice } = req.query;

  let sql = 'SELECT * FROM properties WHERE 1=1';
  const params = [];

  if (city) { sql += ' AND city = ?'; params.push(city); }
  if (minPrice) { sql += ' AND price >= ?'; params.push(Number(minPrice)); }
  if (maxPrice) { sql += ' AND price <= ?'; params.push(Number(maxPrice)); }

  const rows = db.prepare(sql).all(...params);
  res.json({ status: 'success', properties: rows.map(toPublicProperty) });
});

// GET /properties/:id
router.get('/:id', (req, res) => {
  const row = db.prepare('SELECT * FROM properties WHERE id = ?').get(req.params.id);
  if (!row) {
    return res.status(404).json({ status: 'error', message: 'Property not found' });
  }
  res.json(toPublicProperty(row));
});

module.exports = router;
