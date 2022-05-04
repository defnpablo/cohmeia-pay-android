package br.com.cohmeia.sync.mapper

import br.com.cohmeia.database.entities.RechargeEntity
import br.com.cohmeia.sync.model.RechargeRequest
import br.com.cohmeia.wallet.operation.recharge.RechargePaymentType
import java.util.*

object RechargeRequestFromEntity {

    operator fun invoke(entity: RechargeEntity): RechargeRequest {
        return RechargeRequest(
            createdAt = entity.createdAt,
            createdBy = entity.employeeId,
            externalId = entity.externalId,
            paymentMethod = RechargePaymentType.valueOf(entity.paymentMethod.toUpperCase(Locale.ROOT)).typeId,
            tagId = entity.tagId,
            valueInCents = entity.valueInCents,
        )
    }

}