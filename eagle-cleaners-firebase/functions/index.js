const functions = require("firebase-functions");
const admin = require('firebase-admin');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

admin.initializeApp({
    credential: admin.credential.applicationDefault()
});
  
const db = admin.firestore();

exports.helloWorld = functions.https.onRequest((request, response) => {
    functions.logger.info("Hello logs!", {structuredData: true});
    response.send("Hello from Firebase!");
});

exports.requestDelivery = functions.https.onRequest((req, res) => {
    const name = req.query.name;
    const time = parseInt(req.query.time);

    if(!(name && time)) {
        console.log(req);
        res.send({deliverySuccess: false, error: 'Invalid delivery object'});
        return;
    }

    db.collection('delivery-requests').add({
        name: name,
        time: time,
    }).then((response) => {
        return admin.messaging().send({
            notification: {
                title: "New delivery request",
                body: "From " + name
            },
            topic: 'admin'
        });
    }).then((response) => {
        console.log('Success!');
        res.send({deliverySuccess: true});
    })
    .catch((error) => {
        console.log('Error: ' + error);
        res.send({deliverySuccess: false, error: error});
    });
});
