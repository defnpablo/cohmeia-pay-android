package br.com.cohmeia.sync.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import br.com.cohmeia.BuildConfig
import br.com.cohmeia.login.LoginRepository
import br.com.cohmeia.sync.DbSyncData
import br.com.cohmeia.sync.usecase.GetDbSyncDataUseCase
import br.com.cohmeia.sync.usecase.SaveLastSyncDateWithStatusUseCase
import br.com.cohmeia.sync.usecase.SyncDataUseCase
import br.com.cohmeia.sync.usecase.UpdateSyncDateOfEntitiesUseCase
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.TimeUnit


class SyncService : Service(), KoinComponent {

    private val syncDataUseCase: SyncDataUseCase by inject()
    private val getDbSyncDataUseCase: GetDbSyncDataUseCase by inject()
    private val updateSyncDateOfEntitiesUseCase: UpdateSyncDateOfEntitiesUseCase by inject()
    private val saveLastSyncDateWithStatusUseCase: SaveLastSyncDateWithStatusUseCase by inject()
    private val loginRepository: LoginRepository by inject()

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "channel_id"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "CohmeiaPay",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification: Notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()
            startForeground(1, notification)
        }

        scope.launch {
            while (true) {
                if (userLoggedIn()) {
                    syncData()
                }
                delay(TimeUnit.SECONDS.toMillis(BuildConfig.SYNC_DELAY_IN_SECONDS.toLong()))
            }
        }
        return START_STICKY
    }

    private fun userLoggedIn(): Boolean {
        return loginRepository.getCurrentUser() != null
    }

    private suspend fun syncData() {
        Timber.d("Running sync SERVICE")
        val data = getDbSyncDataUseCase.execute()
        if (hasDataToSync(data)) {
            Timber.d("Sync...")
            val (_, response, _) = syncDataUseCase.execute(data)
            val successSendingData = response.statusCode == HttpURLConnection.HTTP_OK
            if (successSendingData) {
                updateSyncDateOfEntitiesUseCase.execute(data)
            }
            saveLastSyncDateWithStatusUseCase.execute(Date(), successSendingData)
        } else {
            Timber.d("No data to sync")
        }
    }

    private fun hasDataToSync(data: DbSyncData): Boolean {
        return data.employees.isNotEmpty() ||
        data.products.isNotEmpty() ||
        data.productSales.isNotEmpty() ||
        data.recharges.isNotEmpty() ||
        data.sales.isNotEmpty()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopForeground(true)
    }

}