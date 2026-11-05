const { initializeApp, applicationDefault } =
  require('firebase-admin/app');

const { getFirestore } =
  require('firebase-admin/firestore');

// Initialize Firebase Admin using Windows credentials
initializeApp({
  credential: applicationDefault()
});

// Connect to Cloud Firestore
const firestore = getFirestore();

module.exports = firestore;