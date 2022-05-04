package br.com.cohmeia

import android.app.Application
import android.app.NotificationManager
import android.content.Intent
import androidx.core.content.ContextCompat
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.di.*
import br.com.cohmeia.sync.service.SyncService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class CohmeiaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            logger()
            androidContext(this@CohmeiaApp)
            modules(
                appModule,
                cashierModule,
                barmanModule,
                loginModule,
                analyticsModule,
                adminModule,
                imageHelperModule,
                syncModule,
                backupModule,
            )
        }
        setupLoggers()
        setupDatabase()
        startSyncJob()
    }

    private fun startSyncJob() {
        val intent = Intent(this, SyncService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun setupDatabase() {
        CohmeiaDatabase.getDatabase(this)
//        CohmeiaDatabase.testDb(this)
    }

    private fun setupLoggers() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}