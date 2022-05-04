package br.com.cohmeia.database.entities

import br.com.cohmeia.database.CohmeiaDatabase

object EntityHelper {

    fun createEmployee(db: CohmeiaDatabase) {
        db.employeeDao().insert(
            EmployeeEntity(
                name = "name_test",
                profile = 1,
                document = "112223334"
            )
        )
    }

}