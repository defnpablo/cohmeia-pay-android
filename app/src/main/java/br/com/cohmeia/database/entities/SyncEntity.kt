package br.com.cohmeia.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "sync_date",
    indices = [Index(value = ["externalId"], unique = true)]
)
data class SyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lastSyncDate: Long,
    val success: Boolean,
    val externalId: String = UUID.randomUUID().toString(),
)