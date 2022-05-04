package br.com.cohmeia.sync.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class RechargeRequest(
    val createdAt: Date,
    val createdBy: String,
    val externalId: String,
    val paymentMethod: Int, //br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
    val tagId: String,
    val valueInCents: Int,
)