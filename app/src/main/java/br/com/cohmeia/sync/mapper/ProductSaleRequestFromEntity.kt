package br.com.cohmeia.sync.mapper

import br.com.cohmeia.database.entities.ProductSaleEntity
import br.com.cohmeia.sync.model.ProductSaleRequest

object ProductSaleRequestFromEntity {

    operator fun invoke(entity: ProductSaleEntity): ProductSaleRequest {
        return ProductSaleRequest(
            externalId = entity.externalId,
            productCode = entity.productId,
            productQuantity = entity.productQuantity,
            saleExternalId = entity.saleId,
        )
    }

}