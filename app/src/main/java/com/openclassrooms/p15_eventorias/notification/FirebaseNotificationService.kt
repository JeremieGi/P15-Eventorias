package com.openclassrooms.p15_eventorias.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.openclassrooms.p15_eventorias.R
import com.openclassrooms.p15_eventorias.ui.MainActivity

// Doc : https://firebase.google.com/docs/cloud-messaging/android/client?hl=fr

class FirebaseNotificationService : FirebaseMessagingService() {

    companion object {

        // Nom de l'unique channel utilisé dans l'application
        const val CHANNEL_ID_DEFAULT: String = "default_channel_ID"
        const val CHANNEL_NAME_DEFAULT: String = "default_channel"

    }

    private val _notificationId = 1
    private val _notificationTag = "EVENTORIAS"

    /**Miscellaneous
     * Méthode appelée à la réception d'une notification
     */
    override fun onMessageReceived(message: RemoteMessage) {

        super.onMessageReceived(message)

        if (message.notification != null) {

            // Get message sent by Firebase
            val notification = message.notification

            // Affiche une notification visuelle
            sendVisualNotification(notification)

        }


    }

    /**
     * Permet l'affichage à l'écran de la notification
     */
    private fun sendVisualNotification(notification: RemoteMessage.Notification?) {

        // Create an Intent that will be shown when user will click on the Notification
        val intent = Intent(this, MainActivity::class.java )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Permet de supprimer toutes activités de la pile, jusqu'à MainActivity
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Build a Notification object
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, CHANNEL_ID_DEFAULT)
                .setSmallIcon(R.drawable.logo_eventorias)
                .setContentTitle(notification!!.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8.0 (API niveau 26) et supérieur, il est obligatoire de créer un Notification Channel
            // Si le canal n'existe pas
            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID_DEFAULT)
            if (existingChannel == null) {
                // Il est créé
                createChannel(notificationManager)
            }
        }


        // Envoie de la la notif à l'application
        notificationManager.notify(
            _notificationTag,
            _notificationId,
            notificationBuilder.build()
        )
    }

    // Je prends un warning si je n'implémente pas cette méthode
    override fun onNewToken(token: String) {
        // Mais je ne gère pas les tokens dans ce projet

        // TODO : denis : plus d'explication sur les tokens

        /**
         * Le token d'inscription est un identifiant unique attribué à chaque appareil ou instance d'application.
         * Lorsque votre application s'enregistre auprès de Firebase Cloud Messaging,
         * un token unique est généré pour cet appareil.
         * Ce token permet à Firebase d'identifier de manière unique chaque appareil ou utilisateur au sein de votre application.
         */
    }


    // Création du channel
    private fun createChannel(notificationManager: NotificationManager) {

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName: CharSequence = CHANNEL_NAME_DEFAULT
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID_DEFAULT, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }


    }

}