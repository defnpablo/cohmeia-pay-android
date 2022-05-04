package br.com.cohmeia.admin.employee

interface EmployeeRepository {
    suspend fun saveEmployee(employee: Employee, createdByUserDocument: String)
    suspend fun removeEmployee(employee: Employee)
    suspend fun getEmployees(): List<Employee>
}
