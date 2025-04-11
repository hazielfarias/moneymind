package com.example.moneymind.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val title = "Lembrete Money Mind"
            val message = "Não se esqueça de cadastrar seus gastos deste mês!"

            NotificationHelper.showNotification(applicationContext, title, message)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        fun scheduleMonthlyReminder(context: Context, dayOfMonth: Int) {
            val currentDate = Calendar.getInstance()
            // notificar as 9 horas do dia escolhido
            val dueDate = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 9)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)

                if (before(currentDate)) {
                    add(Calendar.MONTH, 1)
                }
            }


            val initialDelay =  dueDate.timeInMillis - currentDate.timeInMillis


            val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
                30, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay,TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "monthly_reminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )

            // logcat
            android.util.Log.d("ReminderWorker",
                "Lembrete agendado para dia $dayOfMonth. Delay: $initialDelay ms")
        }

        fun cancelReminder(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork("monthly_reminder")
            android.util.Log.d("ReminderWorker", "Lembretes cancelados")
        }
    }
}