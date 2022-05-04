package br.com.cohmeia.login

import br.com.cohmeia.database.entities.EmployeeEntity

interface LoginRepository {
    fun saveUser(user: User)
    fun getCurrentUser(): User?
    fun logout()
    suspend fun userExists(cpf: String): EmployeeEntity?
}