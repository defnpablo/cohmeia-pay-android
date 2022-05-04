package br.com.cohmeia.sync.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class ProductRequest(
    val inMenu: Int,
    val name: String,
    val valueInCents: Int,
    val createdAt: Date,
    val createdBy: String,
    val externalId: String,
    val code: String,
)