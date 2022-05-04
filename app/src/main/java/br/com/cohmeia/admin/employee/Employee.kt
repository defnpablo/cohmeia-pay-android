package br.com.cohmeia.admin.employee

import androidx.annotation.Keep

@Keep
data class Employee(
    val name: String,
    val cpf: String,
    val profile: Int
)
