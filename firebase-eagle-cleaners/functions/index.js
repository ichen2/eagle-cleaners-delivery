const functions = require("firebase-functions");
const admin = require("firebase-admin");

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

admin.initializeApp({
  credential: admin.credential.applicationDefault(),
});

const db = admin.firestore();

exports.requestDelivery = functions.https.onRequest((req, res) => {
  const name = req.query.name;
  const addressName = req.query.addressName;
  const addressLat = parseFloat(req.query.addressLat);
  const addressLng = parseFloat(req.query.addressLng);
  const time = parseInt(req.query.time);

  if (!(name && time)) {
    res.send(false);
    return;
  }

  db.collection("delivery-requests").add({
    name: name,
    addressName: addressName,
    addressCoordinates: new admin.firestore.GeoPoint(addressLat, addressLng),
    time: time,
  }).then((response) => {
    return admin.messaging().send({
      notification: {
        title: "New delivery request",
        body: "From " + name,
      },
      topic: "admin",
    });
  }).then((response) => {
    res.send(true);
  }).catch((error) => {
    res.send(false);
  });
});
