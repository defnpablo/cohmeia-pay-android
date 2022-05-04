package br.com.cohmeia.sync.model

import androidx.annotation.Keep

@Keep
data class ProductSaleRequest(
    val externalId: String,
    val productCode: String,
    val productQuantity: Int,
    val saleExternalId: String,
)