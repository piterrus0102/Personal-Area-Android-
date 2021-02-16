package ru.elesta.jupiter_lkselfguard.helpers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.elesta.jupiter_lkselfguard.Activities.LKActivity
import ru.elesta.jupiter_lkselfguard.Activities.MainActivity
import ru.elesta.jupiter_lkselfguard.R


class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(tagFCM, "From: ${remoteMessage.from}")

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(tagFCM, "Message data payload: ${remoteMessage.data}")
            Log.e("MESSAGA", remoteMessage.data.get("body").toString())
            sendNotification(remoteMessage.data.get("body").toString(), remoteMessage.data.get("title").toString())
        }
        else {
            Log.e("MINE", "NET NET!!!")
        }

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(tagFCM, "Message Notification Body: ${it.body}")
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    // [START on_new_token]
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token: String) {
        Log.d(tagFCM, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(tagFCM, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String, messageTitle: String) {
        val splitEvent = messageBody.split(" ")
        var finalMessage = ""
        for (index in splitEvent.indices){
            if(index != 0){
                finalMessage += splitEvent[index]
                finalMessage += " "
            }
        }
        val mainPref = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val m = mainPref.getBoolean("autoLogin", false)
        val intent = if(m) Intent(this, LKActivity::class.java)
                     else Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            0
        )
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "12345")
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.logokros)
                .setContentTitle(messageTitle)
                .setContentText(finalMessage)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(longArrayOf(200, 200, 200))
                .setContentIntent(pendingIntent)
        } else {
            notificationBuilder.setSmallIcon(R.drawable.logokros)
                .setContentTitle(messageTitle)
                .setContentText(finalMessage)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(longArrayOf(200, 200, 200))
                .setFullScreenIntent(pendingIntent, true)
        }

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        r++
        Log.v("r", r.toString())
        notificationManager.notify(r, notificationBuilder.build())

    }

    private val tagFCM = "MyFirebaseMsgService"

    companion object {
       private var r = 0
    }
}
