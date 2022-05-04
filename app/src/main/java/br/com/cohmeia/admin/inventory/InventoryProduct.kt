package br.com.cohmeia.admin.inventory

import androidx.annotation.Keep

@Keep
data class InventoryProduct(
    val id: Int = -1,
    val name: String,
    val valueInCents: Int,
    val code: String,
    val imagePath: String = ""
)