import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificationHelper {
    const val CHANNEL_ID = "money_mind_reminders"
    const val CHANNEL_NAME = "Lembretes Money Mind"

    fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Lembretes para cadastrar gastos mensais"
            enableLights(true)
            enableVibration(true)
        }

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        )
        notificationManager?.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, title: String, message: String) {
        createNotificationChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // alterar para algum icone caso queira
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        )
        notificationManager?.notify(System.currentTimeMillis().toInt(), notification) // ID único
    }
}