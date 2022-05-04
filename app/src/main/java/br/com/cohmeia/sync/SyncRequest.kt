package br.com.cohmeia.sync

import androidx.annotation.Keep
import br.com.cohmeia.database.entities.*
import br.com.cohmeia.sync.model.*

@Keep
data class SyncRequest(
    val currentUser: String,
    val employees: List<EmployeeRequest>?,
    val products: List<ProductRequest>?,
    val productSales: List<ProductSaleRequest>?,
    val recharges: List<RechargeRequest>?,
    val sales: List<SaleRequest>?
)