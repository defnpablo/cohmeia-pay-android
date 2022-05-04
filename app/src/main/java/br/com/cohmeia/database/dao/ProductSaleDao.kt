package br.com.cohmeia.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import br.com.cohmeia.database.entities.ProductSaleEntity

@Dao
interface ProductSaleDao {

    @Query("SELECT * FROM productSale")
    fun getAll(): List<ProductSaleEntity>

    @Query("SELECT * FROM productSale WHERE syncDate IS NULL OR syncDate <= 0 LIMIT :limit")
    fun getAllNotSynced(limit: Int): List<ProductSaleEntity>

    @Insert
    fun insert(vararg productSaleEntity: ProductSaleEntity)

    @Update
    fun update(vararg entities: ProductSaleEntity)

}