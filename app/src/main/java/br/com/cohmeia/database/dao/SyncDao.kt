package br.com.cohmeia.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.cohmeia.database.entities.SyncEntity

@Dao
interface SyncDao {

    @Query("SELECT * FROM sync_date ORDER BY lastSyncDate DESC LIMIT 1")
    fun getLastSyncDate(): SyncEntity?

    @Insert
    fun setLastSyncDate(syncEntity: SyncEntity)

}