package br.com.cohmeia.admin.employee

import br.com.cohmeia.common.onlyLetters
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.database.entities.EmployeeEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class EmployeeRepositoryImpl : EmployeeRepository, KoinComponent {

    private val db: CohmeiaDatabase by inject()

    override suspend fun saveEmployee(employee: Employee,  createdByUserDocument: String) {
        withContext(Dispatchers.IO) {
            db.employeeDao().insert(
                EmployeeEntity(
                    name = employee.name,
                    document = employee.cpf,
                    profile = employee.profile,
                    syncDate = 0,
                    createdBy = createdByUserDocument.onlyLetters()
                )
            )
        }
    }

    override suspend fun removeEmployee(employee: Employee) {
        withContext(Dispatchers.IO) {
            with(db.employeeDao()) {
                getByCpf(employee.cpf)?.let {
                    delete(it)
                }
            }
        }

    }

    override suspend fun getEmployees(): List<Employee> {
        return db.employeeDao().getAll().map {
            Employee(it.name, it.document, it.profile)
        }
    }
}