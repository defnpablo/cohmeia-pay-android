package br.com.cohmeia.sync

import br.com.cohmeia.database.entities.*

data class DbSyncData(
    val currentUser: String,
    val employees: List<EmployeeEntity> = emptyList(),
    val products: List<ProductEntity> = emptyList(),
    val productSales: List<ProductSaleEntity> = emptyList(),
    val recharges: List<RechargeEntity> = emptyList(),
    val sales: List<SaleEntity> = emptyList(),
)