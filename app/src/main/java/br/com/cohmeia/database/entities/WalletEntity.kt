package br.com.cohmeia.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "wallet",
    indices = [Index(value = ["externalId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["document"],
            childColumns = ["employeeId"]
        )
    ]
)
data class WalletEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tagId: String,

    val externalId: String = UUID.randomUUID().toString(),
    val employeeId: String,
    val createdAt: Date = Date(System.currentTimeMillis()),
    val syncDate: Long,
)
