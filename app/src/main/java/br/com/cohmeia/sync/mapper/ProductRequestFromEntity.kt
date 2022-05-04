package br.com.cohmeia.sync.mapper

import br.com.cohmeia.common.toFormat
import br.com.cohmeia.database.entities.ProductEntity
import br.com.cohmeia.sync.model.ProductRequest

object ProductRequestFromEntity {

    operator fun invoke(entity: ProductEntity): ProductRequest {
        return ProductRequest(
            inMenu = entity.inMenu,
            name = entity.name,
            valueInCents = entity.valueInCents,
            createdAt = entity.createdAt,
            createdBy = entity.createdBy,
            externalId = entity.externalId,
            code = entity.code,
        )
    }

}