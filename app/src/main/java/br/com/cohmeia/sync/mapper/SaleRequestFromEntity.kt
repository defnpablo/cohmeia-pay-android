package br.com.cohmeia.sync.mapper

import br.com.cohmeia.database.entities.RechargeEntity
import br.com.cohmeia.database.entities.SaleEntity
import br.com.cohmeia.sync.model.SaleRequest

object SaleRequestFromEntity {

    operator fun invoke(entity: SaleEntity): SaleRequest {
        return SaleRequest(
            createdAt = entity.createdAt,
            createdBy = entity.employeeId,
            externalId = entity.externalId,
            tagId = entity.tagId,
            valueInCents = entity.valueInCents,
        )
    }

}