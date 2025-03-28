package com.d4rk.cartcalculator.core.di.modules

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.cartcalculator.app.cart.domain.usecases.AddCartItemUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.DeleteCartItemUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.LoadCartUseCase
import com.d4rk.cartcalculator.app.cart.domain.usecases.UpdateCartItemUseCase
import com.d4rk.cartcalculator.app.cart.ui.CartViewModel
import com.d4rk.cartcalculator.app.home.domain.usecases.AddCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.DecryptSharedCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.DeleteCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.GetCartsUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.ImportSharedCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.OpenCartUseCase
import com.d4rk.cartcalculator.app.home.domain.usecases.UpdateCartNameUseCase
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel
import com.d4rk.cartcalculator.app.main.domain.usecases.PerformInAppUpdateUseCase
import com.d4rk.cartcalculator.app.main.ui.MainViewModel
import com.d4rk.cartcalculator.core.data.database.AppDatabase
import com.d4rk.cartcalculator.core.data.database.DataBaseImplementation
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule : Module = module {
    single<DataStore> { DataStore.getInstance(context = get()) }
    single<AppDatabase> { AppDatabase.getInstance(context = get()) }
    single<DatabaseInterface> { DataBaseImplementation(database = get()) }
    single<AdsCoreManager> { AdsCoreManager(context = get()) }

    single<AppUpdateManager> { AppUpdateManagerFactory.create(get()) }

    factory { (launcher : ActivityResultLauncher<IntentSenderRequest>) ->
        PerformInAppUpdateUseCase(appUpdateManager = get() , updateResultLauncher = launcher)
    }

    viewModel { (launcher : ActivityResultLauncher<IntentSenderRequest>) ->
        MainViewModel(performInAppUpdateUseCase = get { parametersOf(launcher) })
    }

    single<GetCartsUseCase> { GetCartsUseCase(database = get()) }
    single<DecryptSharedCartUseCase> { DecryptSharedCartUseCase() }
    single<AddCartUseCase> { AddCartUseCase(database = get()) }
    single<DeleteCartUseCase> { DeleteCartUseCase(database = get()) }
    single<ImportSharedCartUseCase> { ImportSharedCartUseCase(database = get() , decryptSharedCartUseCase = get()) }
    single<OpenCartUseCase> { OpenCartUseCase(context = get()) }
    single<GenerateCartShareLinkUseCase> { GenerateCartShareLinkUseCase(database = get()) }
    single<UpdateCartNameUseCase> { UpdateCartNameUseCase(database = get()) }

    viewModel {
        HomeViewModel(getCartsUseCase = get() , addCartUseCase = get() , deleteCartUseCase = get() , importSharedCartUseCase = get() , openCartUseCase = get() , dispatcherProvider = get() , generateCartShareLinkUseCase = get() , updateCartNameUseCase = get())
    }

    single<LoadCartUseCase> { LoadCartUseCase(database = get()) }
    single<AddCartItemUseCase> { AddCartItemUseCase(database = get()) }
    single<UpdateCartItemUseCase> { UpdateCartItemUseCase(database = get()) }
    single<DeleteCartItemUseCase> { DeleteCartItemUseCase(database = get()) }

    viewModel {
        CartViewModel(loadCartUseCase = get() , addCartItemUseCase = get() , updateCartItemUseCase = get() , deleteCartItemUseCase = get() , generateCartShareLinkUseCase = get() , dataStore = get() , dispatcherProvider = get())
    }
}