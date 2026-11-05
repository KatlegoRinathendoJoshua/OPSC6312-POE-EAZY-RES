const express = require('express');
const firestore = require('../firebase');
const db = require('../db');
const { authMiddleware } = require('./auth');

const router = express.Router();

// GET /favourites
// Retrieve the logged-in user's saved properties.
router.get('/', authMiddleware, async (req, res) => {
  try {
    const doc = await firestore
      .collection('savedProperties')
      .doc(req.userId)
      .get();

    const propertyIds = doc.exists ? doc.data().propertyIds || [] : [];

    res.json({
      status: 'success',
      propertyIds
    });
  } catch (error) {
    console.error('Get favourites error:', error.message);
    res.status(500).json({
      status: 'error',
      message: 'Could not retrieve saved properties'
    });
  }
});

// POST /favourites/:propertyId
// Save a property for the logged-in user.
router.post('/:propertyId', authMiddleware, async (req, res) => {
  try {
    const property = db.prepare(
      'SELECT id FROM properties WHERE id = ?'
    ).get(req.params.propertyId);

    if (!property) {
      return res.status(404).json({
        status: 'error',
        message: 'Property not found'
      });
    }

    const ref = firestore
      .collection('savedProperties')
      .doc(req.userId);

    await firestore.runTransaction(async (transaction) => {
      const doc = await transaction.get(ref);
      const propertyIds = doc.exists
        ? doc.data().propertyIds || []
        : [];

      if (!propertyIds.includes(req.params.propertyId)) {
        propertyIds.push(req.params.propertyId);
      }

      transaction.set(ref, { propertyIds });
    });

    res.json({
      status: 'success',
      message: 'Property saved successfully'
    });
  } catch (error) {
    console.error('Save favourite error:', error.message);
    res.status(500).json({
      status: 'error',
      message: 'Could not save property'
    });
  }
});

// DELETE /favourites/:propertyId
// Remove a saved property.
router.delete('/:propertyId', authMiddleware, async (req, res) => {
  try {
    const ref = firestore
      .collection('savedProperties')
      .doc(req.userId);

    await firestore.runTransaction(async (transaction) => {
      const doc = await transaction.get(ref);

      if (!doc.exists) return;

      const propertyIds = doc.data().propertyIds || [];
      const updatedIds = propertyIds.filter(
        id => id !== req.params.propertyId
      );

      transaction.set(ref, { propertyIds: updatedIds });
    });

    res.json({
      status: 'success',
      message: 'Property removed from saved properties'
    });
  } catch (error) {
    console.error('Remove favourite error:', error.message);
    res.status(500).json({
      status: 'error',
      message: 'Could not remove saved property'
    });
  }
});

module.exports = router;