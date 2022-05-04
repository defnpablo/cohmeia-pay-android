package br.com.cohmeia.sync.usecase

import br.com.cohmeia.BuildConfig
import br.com.cohmeia.common.onlyLetters
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.entities.EmployeeEntity
import br.com.cohmeia.login.LoginRepository
import br.com.cohmeia.sync.DbSyncData

interface GetDbSyncDataUseCase {
    suspend fun execute(): DbSyncData
}

class GetDbSyncDataUseCaseImpl(
    private val db: CohmeiaDatabase,
    private val loginRepository: LoginRepository,
) : GetDbSyncDataUseCase {

    override suspend fun execute(): DbSyncData {
        val batchLimit = BuildConfig.BATCH_LIMIT
        val employees = sanitizeEmployees(db.employeeDao().getAllNotSynced(batchLimit))
        val products = db.productDao().getAllNotSynced(batchLimit)
        val productSales = db.productSaleDao().getAllNotSynced(batchLimit)
        val recharges = db.rechargeDao().getAllNotSynced(batchLimit)
        val sales = db.saleDao().getAllNotSynced(batchLimit)
        return DbSyncData(
            loginRepository.getCurrentUser()?.userCpf ?: "-1",
            employees,
            products,
            productSales,
            recharges,
            sales,
        )
    }

    private fun sanitizeEmployees(employees: List<EmployeeEntity>): List<EmployeeEntity> {
        return employees.map { it.copy(document = it.document.onlyLetters()) }
    }

}
