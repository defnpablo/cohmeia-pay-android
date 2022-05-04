package br.com.cohmeia.login

sealed class User(val userCpf: String, val userId: Int, val employeeName: String)

data class Barman(val cpf: String, val id: Int, val name: String) : User(cpf, id, name)
data class Cashier(val cpf: String, val id: Int, val name: String) : User(cpf, id, name)
data class Admin(val cpf: String, val id: Int, val name: String) : User(cpf, id, name)

fun profileForType(type: Int): String {
    return when (type) {
        0 -> "Admin"
        1 -> "Caixa"
        2 -> "Barman"
        else -> ""
    }
}