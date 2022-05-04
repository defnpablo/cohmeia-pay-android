package br.com.cohmeia.sync

import br.com.cohmeia.BuildConfig
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.entities.SyncEntity
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.gson.jsonBody
import com.github.kittinunf.fuel.httpPost
import java.util.*

class SyncRepositoryImpl(val db: CohmeiaDatabase) : SyncRepository {

    private val syncUrl = BuildConfig.SYNC_URL

    override suspend fun setLastSyncDate(date: Date, success: Boolean) {
        db.syncDao().setLastSyncDate(SyncEntity(lastSyncDate = date.time, success = success))
    }

    override suspend fun getLastSyncDate(): LastSyncDate? {
        val syncEntity = db.syncDao().getLastSyncDate()
        val lastDate = syncEntity?.lastSyncDate ?: 0
        return if (lastDate <= 0) null else LastSyncDate(Date(lastDate), syncEntity?.success ?: false)
    }

    override suspend fun sync(serializedData: String): ResponseResultOf<String> {
        return syncUrl.httpPost()
            .header("Authorization", BuildConfig.AUTH_HEADER_DATA)
            .jsonBody(serializedData)
            .awaitStringResponseResult()
    }

}