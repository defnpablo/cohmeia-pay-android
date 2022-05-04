package br.com.cohmeia.sync

import com.github.kittinunf.fuel.core.ResponseResultOf
import java.util.*

interface SyncRepository {
    suspend fun getLastSyncDate(): LastSyncDate?
    suspend fun setLastSyncDate(date: Date, success: Boolean)
    suspend fun sync(serializedData: String) : ResponseResultOf<String>
}