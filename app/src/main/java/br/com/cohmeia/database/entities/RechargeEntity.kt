package br.com.cohmeia.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(
    tableName = "recharge",
    indices = [Index(value = ["externalId"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["document"],
            childColumns = ["employeeId"]
        )
    ]
)
data class RechargeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val externalId: String = UUID.randomUUID().toString(),
    val employeeId: String,
    val valueInCents: Int,
    val tagId: String,
    val paymentMethod: String,
    val createdAt: Date = Date(System.currentTimeMillis()),
    val syncDate: Long,
)