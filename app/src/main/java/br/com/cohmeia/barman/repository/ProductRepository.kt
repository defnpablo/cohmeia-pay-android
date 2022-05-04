package br.com.cohmeia.barman.repository

import br.com.cohmeia.barman.checkout.Product

interface ProductRepository {
    fun getProducts(): List<Product>
}