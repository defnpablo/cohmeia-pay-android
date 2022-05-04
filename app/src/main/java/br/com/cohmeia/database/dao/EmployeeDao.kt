package br.com.cohmeia.database.dao

import androidx.room.*
import br.com.cohmeia.database.entities.EmployeeEntity

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employee")
    fun getAll(): List<EmployeeEntity>

    @Query("SELECT * FROM employee WHERE syncDate IS NULL OR syncDate <= 0 LIMIT :limit")
    fun getAllNotSynced(limit: Int): List<EmployeeEntity>

    @Insert
    fun insert(vararg employeeEntity: EmployeeEntity)

    @Delete
    fun delete(employeeEntity: EmployeeEntity)

    @Query("SELECT * FROM employee WHERE document IS :cpf")
    fun getByCpf(cpf: String): EmployeeEntity?

    @Update
    fun update(vararg entities: EmployeeEntity)

}