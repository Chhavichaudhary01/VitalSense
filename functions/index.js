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

/**
 * Cloud Function triggered on change to condition_records/{caseId}.
 * Creates a CONDITION entry in patientMedicalHistory when status changes to RESPONDED or CLOSED.
 */
exports.onCaseResponded = functions.firestore
  .document("condition_records/{caseId}")
  .onWrite(async (change, context) => {
    const after = change.after.exists ? change.after.data() : null;
    const before = change.before.exists ? change.before.data() : null;

    if (!after) return null;

    const isNewlyRespondedOrClosed = (after.status === "RESPONDED" || after.status === "CLOSED") &&
      (!before || (before.status !== "RESPONDED" && before.status !== "CLOSED"));

    if (isNewlyRespondedOrClosed) {
      const patientId = after.patientId;
      if (!patientId) return null;

      const historyData = {
        patientId: patientId,
        type: "CONDITION",
        referenceId: context.params.caseId,
        date: admin.firestore.FieldValue.serverTimestamp(),
        details: {
          condition: after.condition || "",
          diagnosis: after.diagnosis || "",
          status: after.status
        }
      };

      return admin.firestore().collection("patientMedicalHistory").add(historyData);
    }
    return null;
  });

/**
 * Cloud Function triggered on creation of prescriptions/{rxId}.
 * Creates MEDICATION entries in patientMedicalHistory for each medicine.
 */
exports.onPrescriptionCreated = functions.firestore
  .document("prescriptions/{rxId}")
  .onCreate(async (snap, context) => {
    const data = snap.data();
    if (!data) return null;

    const patientId = data.patientId;
    if (!patientId) return null;

    const medicines = data.medicines || [];
    if (!Array.isArray(medicines) || medicines.length === 0) return null;

    const batch = admin.firestore().batch();
    const historyCol = admin.firestore().collection("patientMedicalHistory");

    medicines.forEach(med => {
      const docRef = historyCol.doc();
      batch.set(docRef, {
        patientId: patientId,
        type: "MEDICATION",
        referenceId: context.params.rxId,
        date: admin.firestore.FieldValue.serverTimestamp(),
        details: {
          medicineName: med.name || med.medicineName || "",
          dosage: med.dosage || "",
          duration: med.duration || "",
          instructions: med.instructions || ""
        }
      });
    });

    return batch.commit();
  });
