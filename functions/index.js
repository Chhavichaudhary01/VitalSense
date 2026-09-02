const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

/**
 * Cloud Function triggered on change to queue_entries/{entryId}.
 * Dispatches FCM push notifications when a patient is called or position updates.
 */
exports.onQueueEntryChanged = functions.firestore
  .document("queue_entries/{entryId}")
  .onWrite(async (change, context) => {
    const after = change.after.exists ? change.after.data() : null;
    const before = change.before.exists ? change.before.data() : null;

    if (!after) {
      return null; // Deleted
    }

    const patientId = after.patientId;
    const doctorName = after.doctorName || "Doctor";
    const tokenNumber = after.tokenNumber;

    // Fetch user device token
    const userDoc = await admin.firestore().collection("users").doc(patientId).get();
    const fcmToken = userDoc.exists ? userDoc.data().fcmToken : null;

    if (!fcmToken) {
      console.log(`No FCM token registered for patient ${patientId}`);
      return null;
    }

    // 1. Patient CALLED event
    if (after.status === "CALLED" && (!before || before.status !== "CALLED")) {
      const payload = {
        token: fcmToken,
        notification: {
          title: "It's Your Turn! 🩺",
          body: `Dr. ${doctorName} is ready to see you. Please proceed to the consultation room.`
        },
        data: {
          type: "QUEUE_CALLED",
          entryId: context.params.entryId,
          tokenNumber: String(tokenNumber)
        }
      };
      return admin.messaging().send(payload);
    }

    // 2. Position / Token Confirmation
    if (before && before.provisionalToken === true && after.provisionalToken === false) {
      const payload = {
        token: fcmToken,
        notification: {
          title: "Queue Token Confirmed",
          body: `Your official token is #${tokenNumber} for Dr. ${doctorName}.`
        },
        data: {
          type: "QUEUE_POSITION_UPDATE",
          entryId: context.params.entryId,
          tokenNumber: String(tokenNumber)
        }
      };
      return admin.messaging().send(payload);
    }

    return null;
  });
