package br.com.cohmeia.database.dao

import androidx.room.*
import br.com.cohmeia.database.entities.EmployeeEntity
import br.com.cohmeia.database.entities.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM product")
    fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM product WHERE syncDate IS NULL OR syncDate <= 0 LIMIT :limit")
    fun getAllNotSynced(limit: Int): List<ProductEntity>

    @Insert
    fun insert(productEntity: ProductEntity) : Long

    @Delete
    fun delete(productEntity: ProductEntity)

    @Query("SELECT * FROM product WHERE id IS :id")
    fun getById(id: Int): ProductEntity?

    @Update
    fun update(vararg entities: ProductEntity)

}