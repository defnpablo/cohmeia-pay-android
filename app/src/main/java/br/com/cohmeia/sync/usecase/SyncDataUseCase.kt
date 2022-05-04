package br.com.cohmeia.sync.usecase

import br.com.cohmeia.sync.DbSyncData
import br.com.cohmeia.sync.SyncRepository
import br.com.cohmeia.sync.SyncRequest
import br.com.cohmeia.sync.mapper.*
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.google.gson.GsonBuilder
import timber.log.Timber

interface SyncDataUseCase {
    suspend fun execute(dbSyncData: DbSyncData): ResponseResultOf<String>
}

class SyncDataUseCaseImpl(private val syncRepository: SyncRepository) : SyncDataUseCase {

    override suspend fun execute(dbSyncData: DbSyncData): ResponseResultOf<String> {
        val serializedData = dbSyncData.toGsonString();
        Timber.d("sync data: $serializedData")
        return syncRepository.sync(serializedData)
    }

}

private fun DbSyncData.toGsonString() : String {
    val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create()
    return gson.toJson(
        SyncRequest(
            currentUser,
            employees.map { EmployeeRequestFromEntity(it) },
            products.map { ProductRequestFromEntity(it) },
            productSales.map { ProductSaleRequestFromEntity(it) },
            recharges.map { RechargeRequestFromEntity(it) },
            sales.map { SaleRequestFromEntity(it) },
        )
    )
}
