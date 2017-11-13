const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotification = functions.database.ref("Notifications/{uid}")
    .onWrite(event => {
        //en request está el contenido de una Notifications
        var request = event.data.val();
        var payload = {
            data: {
                userFrom : request.userFrom,
                message: request.message
            }
        };

        admin.messaging().sendToTopic(request.topic, payload)
            .then(function(response){
                console.log("Mensaje enviado: ", response);
            })
            .catch(function(error){
                console.log("Error enviando mensaje: ", error);
            })
    })
