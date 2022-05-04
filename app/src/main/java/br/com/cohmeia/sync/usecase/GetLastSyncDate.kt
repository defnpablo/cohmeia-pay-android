package br.com.cohmeia.sync.usecase

import br.com.cohmeia.sync.LastSyncDate
import br.com.cohmeia.sync.SyncRepository

interface GetLastSyncDate {
    suspend fun execute() : LastSyncDate?
}

class GetLastSyncDateImpl(private val syncRepository: SyncRepository) : GetLastSyncDate{

    override suspend fun execute(): LastSyncDate? {
        return syncRepository.getLastSyncDate()
    }

}
