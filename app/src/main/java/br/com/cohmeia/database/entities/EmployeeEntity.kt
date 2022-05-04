package br.com.cohmeia.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "employee",
    indices = [
        Index(value = ["document"], unique = true)
    ]
)
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val document: String,
    val profile: Int,
    val createdAt: Date = Date(System.currentTimeMillis()),
    val externalId: String = UUID.randomUUID().toString(),
    val syncDate: Long,
    val createdBy: String,
)