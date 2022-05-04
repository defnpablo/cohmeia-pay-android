package br.com.cohmeia.di

import br.com.cohmeia.admin.employee.EmployeeContract
import br.com.cohmeia.admin.employee.EmployeePresenter
import br.com.cohmeia.admin.employee.EmployeeRepository
import br.com.cohmeia.admin.employee.EmployeeRepositoryImpl
import br.com.cohmeia.admin.inventory.InventoryContract
import br.com.cohmeia.admin.inventory.InventoryPresenter
import br.com.cohmeia.admin.inventory.InventoryRepository
import br.com.cohmeia.admin.inventory.InventoryRepositoryImpl
import br.com.cohmeia.analytics.CohmeiaAnalytics
import br.com.cohmeia.analytics.CohmeiaAnalyticsImpl
import br.com.cohmeia.backup.BackupContract
import br.com.cohmeia.backup.BackupNfcDataRepository
import br.com.cohmeia.backup.BackupNfcDataRepositoryImpl
import br.com.cohmeia.backup.BackupPresenter
import br.com.cohmeia.backup.usecase.BackupNfcDataUseCase
import br.com.cohmeia.backup.usecase.BackupNfcDataUseCaseImpl
import br.com.cohmeia.backup.usecase.GetBackupNfcDataUseCase
import br.com.cohmeia.backup.usecase.GetBackupNfcDataUseCaseImpl
import br.com.cohmeia.barman.BarmanContract
import br.com.cohmeia.barman.BarmanPresenter
import br.com.cohmeia.barman.repository.ProductRepository
import br.com.cohmeia.barman.repository.ProductRepositoryImpl
import br.com.cohmeia.database.CohmeiaDatabase
import br.com.cohmeia.image.ImageHelper
import br.com.cohmeia.login.LoginRepository
import br.com.cohmeia.login.LoginRepositoryImpl
import br.com.cohmeia.nfc.NfcContract
import br.com.cohmeia.nfc.NfcPresenter
import br.com.cohmeia.sync.SyncContract
import br.com.cohmeia.sync.SyncPresenterImpl
import br.com.cohmeia.sync.SyncRepository
import br.com.cohmeia.sync.SyncRepositoryImpl
import br.com.cohmeia.sync.usecase.*
import br.com.cohmeia.wallet.operation.WalletOperationContract
import br.com.cohmeia.wallet.operation.WalletOperationPresenter
import br.com.cohmeia.wallet.operation.read.ReadWalletContract
import br.com.cohmeia.wallet.operation.read.ReadWalletPresenter
import br.com.cohmeia.wallet.operation.recharge.RechargeContract
import br.com.cohmeia.wallet.operation.recharge.RechargePresenter
import br.com.cohmeia.wallet.repository.WalletRepository
import br.com.cohmeia.wallet.repository.WalletRepositoryImpl
import org.koin.dsl.module
import kotlin.math.sin

val appModule = module {
    single { CohmeiaDatabase.getDatabase(get()) }
    single<WalletRepository> { WalletRepositoryImpl(get(), get(), get()) }
    factory { WalletOperationPresenter(get()) }
    factory<NfcContract.Presenter> { NfcPresenter() }
    factory<ReadWalletContract.Presenter> { ReadWalletPresenter() }
}

val cashierModule = module {
    factory<RechargeContract.Presenter> { RechargePresenter() }
}

val barmanModule = module {
    single<ProductRepository> { ProductRepositoryImpl(get()) }
    factory<BarmanContract.Presenter> { BarmanPresenter(get()) }
}

val loginModule = module {
    single<LoginRepository> { LoginRepositoryImpl(get()) }
}

val analyticsModule = module {
    single<CohmeiaAnalytics> { CohmeiaAnalyticsImpl(get()) }
}

val adminModule = module {
    single<EmployeeRepository> { EmployeeRepositoryImpl() }
    factory<EmployeeContract.Presenter> { EmployeePresenter(get(), get()) }
    single<InventoryRepository> { InventoryRepositoryImpl() }
    factory<InventoryContract.Presenter> { InventoryPresenter(get(), get()) }
    single<SyncRepository> { SyncRepositoryImpl(get()) }
}

val imageHelperModule = module {
    single<ImageHelper> { ImageHelper(get()) }
}

val syncModule = module {

    single<GetLastSyncDate> { GetLastSyncDateImpl(get()) }
    single<SaveLastSyncDateWithStatusUseCase> { SaveLastSyncDateWithStatusUseCaseImpl(get()) }
    single<GetDbSyncDataUseCase> { GetDbSyncDataUseCaseImpl(get(), get()) }
    single<SyncDataUseCase> { SyncDataUseCaseImpl(get()) }
    single<UpdateSyncDateOfEntitiesUseCase> { UpdateSyncDateOfEntitiesUseCaseImpl(get()) }

    factory<SyncContract.Presenter> { (view: SyncContract.View) ->
        SyncPresenterImpl(
            view,
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
}

val backupModule = module {

    single<BackupNfcDataRepository> { BackupNfcDataRepositoryImpl(get()) }
    single<BackupNfcDataUseCase> { BackupNfcDataUseCaseImpl(get()) }
    single<GetBackupNfcDataUseCase> { GetBackupNfcDataUseCaseImpl(get()) }

    factory<BackupContract.Presenter> { (view: BackupContract.View) ->
        BackupPresenter(
            view,
            get()
        )
    }

}