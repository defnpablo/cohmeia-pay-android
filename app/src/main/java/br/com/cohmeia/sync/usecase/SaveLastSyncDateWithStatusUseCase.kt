package br.com.cohmeia.sync.usecase

import br.com.cohmeia.sync.SyncRepository
import java.util.*

interface SaveLastSyncDateWithStatusUseCase {
    suspend fun execute(date: Date, success: Boolean)
}

class SaveLastSyncDateWithStatusUseCaseImpl(private val syncRepository: SyncRepository)
    : SaveLastSyncDateWithStatusUseCase {

    override suspend fun execute(date: Date, success: Boolean) {
        syncRepository.setLastSyncDate(date, success)
    }

}
