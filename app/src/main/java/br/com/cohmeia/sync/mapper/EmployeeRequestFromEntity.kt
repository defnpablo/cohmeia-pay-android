package br.com.cohmeia.sync.mapper

import br.com.cohmeia.database.entities.EmployeeEntity
import br.com.cohmeia.sync.model.EmployeeRequest

object EmployeeRequestFromEntity {

    operator fun invoke(entity: EmployeeEntity): EmployeeRequest {
        return EmployeeRequest(
            name = entity.name,
            document = entity.document,
            profile = entity.profile,
            createdAt = entity.createdAt,
            externalId = entity.externalId,
            createdBy = entity.createdBy,
        )
    }

}
