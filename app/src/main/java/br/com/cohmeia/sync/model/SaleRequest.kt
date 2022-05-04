package br.com.cohmeia.sync.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class SaleRequest(
    val createdAt: Date,
    val createdBy: String,
    val externalId: String,
    val tagId: String,
    val valueInCents: Int,
)
