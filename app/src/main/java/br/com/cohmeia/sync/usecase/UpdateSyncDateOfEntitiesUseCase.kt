package br.com.cohmeia.sync.usecase

import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.sync.DbSyncData

interface UpdateSyncDateOfEntitiesUseCase {
    suspend fun execute(data: DbSyncData)
}

class UpdateSyncDateOfEntitiesUseCaseImpl(
    private val db: CohmeiaDatabase
) : UpdateSyncDateOfEntitiesUseCase {

    override suspend fun execute(data: DbSyncData) {
        data.employees.map { it.copy(syncDate = System.currentTimeMillis()) }.also {
            db.employeeDao().update(*it.toTypedArray())
        }
        data.products.map { it.copy(syncDate = System.currentTimeMillis()) }.also {
            db.productDao().update(*it.toTypedArray())
        }
        data.productSales.map { it.copy(syncDate = System.currentTimeMillis()) }.also {
            db.productSaleDao().update(*it.toTypedArray())
        }
        data.recharges.map { it.copy(syncDate = System.currentTimeMillis()) }.also {
            db.rechargeDao().update(*it.toTypedArray())
        }
        data.sales.map { it.copy(syncDate = System.currentTimeMillis()) }.also {
            db.saleDao().update(*it.toTypedArray())
        }
    }

}
