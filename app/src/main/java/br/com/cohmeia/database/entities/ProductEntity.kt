package br.com.cohmeia.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "product",
    indices = [
        Index(value = ["externalId"], unique = true),
        Index(value = ["code"], unique = true)
    ],
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val valueInCents: Int,
    val code: String,
    val inMenu: Int,
    val createdAt: Date = Date(System.currentTimeMillis()),
    val imagePath: String,
    val externalId: String = UUID.randomUUID().toString(),
    val syncDate: Long,
    val createdBy: String, //employee document
)
