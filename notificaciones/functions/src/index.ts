import * as functions from 'firebase-functions';

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });


import * as admin from 'firebase-admin';
admin.initializeApp();

exports.notificacionsismo = functions.database.ref('/sismos/{sismoID}')
.onCreate( async event =>{
    
    // evento de entrada
    const dataSismo = event.val();
    console.log(JSON.stringify(dataSismo))

    // Notification content
    const payload = {
        notification: {
            title: `Nuevo Sismo`,
            body: JSON.stringify(dataSismo)
        }
    }
    console.log(payload)

    const devicesRef = admin.database().ref('/usuarios');

    const devices = await devicesRef.get();

    const tokens:any = [];

    // send a notification to each device token
    devices.forEach(result => {
        const token = result.val().token;
        tokens.push(token)
    })

    console.log(tokens)
    console.log(dataSismo)

    return admin.messaging().sendToDevice(tokens, payload)
})