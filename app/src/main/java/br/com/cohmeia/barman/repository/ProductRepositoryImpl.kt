package br.com.cohmeia.barman.repository

import br.com.cohmeia.barman.checkout.Product
import br.com.cohmeia.database.CohmeiaDatabase

class ProductRepositoryImpl(val db: CohmeiaDatabase) : ProductRepository {

    override fun getProducts(): List<Product> {
         return db.productDao().getAll().map {
            Product(it.code, it.name, it.valueInCents, it.imagePath)
        }
    }

}
