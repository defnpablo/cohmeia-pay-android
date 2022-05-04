package br.com.cohmeia.admin.inventory

import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.entities.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class InventoryRepositoryImpl : InventoryRepository, KoinComponent {

    private val db: CohmeiaDatabase by inject()

    override suspend fun saveProduct(inventoryProduct: InventoryProduct, createdByUserId: String): Long {
        return db.productDao().insert(
            ProductEntity(
                name = inventoryProduct.name,
                valueInCents = inventoryProduct.valueInCents,
                code = inventoryProduct.code,
                inMenu = 0,
                imagePath = inventoryProduct.imagePath,
                syncDate = 0,
                createdBy = createdByUserId,
            )
        )
    }

    override suspend fun removeProduct(inventoryProduct: InventoryProduct) {
        withContext(Dispatchers.IO) {
            with(db.productDao()) {
                val productEntity = getById(inventoryProduct.id)
                productEntity?.let {
                    delete(it)
                }
            }
        }
    }

    override suspend fun getProduct(): List<InventoryProduct> {
        return db.productDao().getAll().map {
            InventoryProduct(it.id, it.name, it.valueInCents, it.code, it.imagePath)
        }
    }
}