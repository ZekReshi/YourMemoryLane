package at.jku.yourmemorylane.fragments

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.text.TextUtils
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import at.jku.yourmemorylane.R
import at.jku.yourmemorylane.activities.EditActivity
import at.jku.yourmemorylane.activities.EditActivity.Companion.EXTRA_ID
import at.jku.yourmemorylane.activities.MainActivity
import at.jku.yourmemorylane.db.AppDatabase
import at.jku.yourmemorylane.db.entities.Memory
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import java.io.InputStream
import kotlin.random.Random


class GeofenceTransitionsJobIntentService : JobIntentService() {
    /**
     * Handles incoming intents.
     * @param intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    override fun onHandleWork(intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        val errorMessage: String = "error with geofencing"
        if(geofencingEvent == null)
            return
        if (geofencingEvent.hasError()) {
            Log.e(GeofenceTransitionsJobIntentService.Companion.TAG, errorMessage)
            return
        }
        val triggeringGeofences = geofencingEvent.triggeringGeofences
        val    memoryDao = AppDatabase.getInstance(application).memoryDao()
        if (triggeringGeofences != null) {
            sendNotification(memoryDao.getByIds(triggeringGeofences.map { it->it.requestId.toLong() }) )
        }
    }

    /**
     * Gets transition details and returns them as a formatted string.
     *
     * @param geofenceTransition    The ID of the geofence transition.
     * @param triggeringGeofences   The geofence(s) triggered.
     * @return                      The transition details formatted as String.
     */
    private fun getGeofenceTransitionDetails(
        geofenceTransition: Int,
        triggeringGeofences: List<Geofence>?
    ): String {
        val geofenceTransitionString = getTransitionString(geofenceTransition)

        // Get the Ids of each geofence that was triggered.
        val triggeringGeofencesIdsList = ArrayList<String?>()
        for (geofence in triggeringGeofences!!) {
            triggeringGeofencesIdsList.add(geofence.requestId)
        }
        val triggeringGeofencesIdsString = TextUtils.join(", ", triggeringGeofencesIdsList)
        return "$geofenceTransitionString: $triggeringGeofencesIdsString"
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private fun sendNotification(notificationDetails: List<Memory>) {
        // Get an instance of the Notification manager
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        val name: CharSequence = getString(R.string.app_name)
        // Create the channel for the notification
        val mChannel = NotificationChannel(
            GeofenceTransitionsJobIntentService.Companion.CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val randomIndex = Random.nextInt(notificationDetails.size)
        val memory = notificationDetails[randomIndex];
        // Set the Notification Channel for the Notification Manager.
        mNotificationManager.createNotificationChannel(mChannel)

        // Create an explicit content Intent that starts the main Activity.
        val notificationIntent = Intent(applicationContext, EditActivity::class.java)
        notificationIntent.putExtra(EXTRA_ID,memory.id)

        // Construct a task stack.
        val stackBuilder = TaskStackBuilder.create(this)

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addNextIntentWithParentStack(notificationIntent)
        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent)




        // Get a PendingIntent containing the entire back stack.
        val notificationPendingIntent =
            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        // Get a notification builder that's compatible with platform versions >= 4
        val builder = NotificationCompat.Builder(this)

        val mediaDao = AppDatabase.getInstance(application).mediaDao()

        val images=mediaDao.getMediaByMemoryIdAndType(memory.id,"image")
        val image = if( images != null)  (images[Random.nextInt(images.size)]) else null
        // Define the notification settings.
        builder
            .setSmallIcon(R.drawable.baseline_camera)
            .setColor(Color.RED)
            .setContentTitle("Revisit Memory")
            .setContentText("You have visited this place before and made a memory '"+ memory.title +"' here, go look at it!")
            .setContentIntent(notificationPendingIntent)

        // Set the Channel ID for Android O.
        builder.setChannelId(GeofenceTransitionsJobIntentService.Companion.CHANNEL_ID) // Channel ID

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true)
        if(image != null){
            val inputStream: InputStream? = applicationContext?.contentResolver?.openInputStream(image.path.toUri())
            val drawable = Drawable.createFromStream(inputStream, image.path)

            val bitmap = (drawable as BitmapDrawable).bitmap
            builder.setLargeIcon(bitmap)
            builder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null as Icon?))
        }
        // Issue the notification
        mNotificationManager.notify(0, builder.build())
    }

    /**
     * Maps geofence transition types to their human-readable equivalents.
     *
     * @param transitionType    A transition type constant defined in Geofence
     * @return                  A String indicating the type of transition
     */
    private fun getTransitionString(transitionType: Int): String {
        return when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_DWELL -> "DWELL"
            else -> "Häää"
        }
    }

    companion object {
        private const val JOB_ID = 573
        private const val TAG = "GeofenceTransitionsIS"
        private const val CHANNEL_ID = "channel_01"

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context?, intent: Intent?) {
            enqueueWork(
                context!!,
                GeofenceTransitionsJobIntentService::class.java,
                GeofenceTransitionsJobIntentService.Companion.JOB_ID,
                intent!!
            )
        }
    }
}
