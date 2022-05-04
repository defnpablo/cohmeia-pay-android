package br.com.cohmeia.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "productSale",
    indices = [Index(value = ["externalId"], unique = true)],
    primaryKeys = ["productId", "saleId"],
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["code"],
            childColumns = ["productId"]
        ),
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["externalId"],
            childColumns = ["saleId"]
        )
    ]
)
data class ProductSaleEntity(
    val productId: String,
    val productQuantity: Int,
    val saleId: String,
    val externalId: String = UUID.randomUUID().toString(),
    val syncDate: Long
)
