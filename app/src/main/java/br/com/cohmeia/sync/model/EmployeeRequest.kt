package br.com.cohmeia.sync.model

import androidx.annotation.Keep
import java.util.*

@Keep
data class EmployeeRequest(
    val name: String,
    val document: String,
    val profile: Int,
    val createdAt: Date,
    val externalId: String,
    val createdBy: String, // User document
)