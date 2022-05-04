package br.com.cohmeia.admin.inventory

interface InventoryRepository {
    suspend fun saveProduct(inventoryProduct: InventoryProduct, createdByUserId: String): Long
    suspend fun removeProduct(inventoryProduct: InventoryProduct)
    suspend fun getProduct(): List<InventoryProduct>
}