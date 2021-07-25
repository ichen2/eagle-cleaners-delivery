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
  const time = parseInt(req.query.time);

  if (!(name && time)) {
    res.send(false);
    return;
  }

  db.collection("delivery-requests").add({
    name: name,
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
