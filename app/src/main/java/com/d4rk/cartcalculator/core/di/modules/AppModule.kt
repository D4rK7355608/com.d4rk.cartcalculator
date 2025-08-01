package com.d4rk.cartcalculator.core.di.modules

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.d4rk.android.libs.apptoolkit.app.oboarding.utils.interfaces.providers.OnboardingProvider
import com.d4rk.android.libs.apptoolkit.data.client.KtorClient
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.cartcalculator.app.cart.details.domain.usecases.AddCartItemUseCase
import com.d4rk.cartcalculator.app.cart.details.domain.usecases.DeleteCartItemUseCase
import com.d4rk.cartcalculator.app.cart.details.domain.usecases.LoadCartUseCase
import com.d4rk.cartcalculator.app.cart.details.domain.usecases.UpdateCartItemUseCase
import com.d4rk.cartcalculator.app.cart.details.ui.CartViewModel
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.AddCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.DecryptSharedCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.DeleteCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.GetCartsUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.ImportSharedCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.OpenCartUseCase
import com.d4rk.cartcalculator.app.cart.list.domain.usecases.UpdateCartNameUseCase
import com.d4rk.cartcalculator.app.cart.list.ui.HomeViewModel
import com.d4rk.cartcalculator.app.cart.search.domain.usecases.SearchEventsUseCase
import com.d4rk.cartcalculator.app.cart.search.ui.SearchViewModel
import com.d4rk.cartcalculator.app.main.ui.MainViewModel
import com.d4rk.cartcalculator.app.onboarding.utils.interfaces.providers.AppOnboardingProvider
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule: Module = module {
    single<DataStore> { DataStore(context = get()) }
    single<AdsCoreManager> { AdsCoreManager(context = get(), buildInfoProvider = get()) }
    single<AppUpdateManager> { AppUpdateManagerFactory.create(get()) }
    single { KtorClient().createClient() }

    single<OnboardingProvider> { AppOnboardingProvider() }

    viewModel { (launcher: ActivityResultLauncher<IntentSenderRequest>) ->
        MainViewModel(dispatcherProvider = get())
    }

    single<GetCartsUseCase> { GetCartsUseCase(database = get()) }
    single<DecryptSharedCartUseCase> { DecryptSharedCartUseCase() }
    single<AddCartUseCase> { AddCartUseCase(database = get()) }
    single<DeleteCartUseCase> { DeleteCartUseCase(database = get()) }
    single<ImportSharedCartUseCase> {
        ImportSharedCartUseCase(
            database = get(),
            decryptSharedCartUseCase = get()
        )
    }
    single<OpenCartUseCase> { OpenCartUseCase(context = get()) }
    single<GenerateCartShareLinkUseCase> { GenerateCartShareLinkUseCase(database = get()) }
    single<UpdateCartNameUseCase> { UpdateCartNameUseCase(database = get()) }

    viewModel {
        HomeViewModel(
            getCartsUseCase = get(),
            addCartUseCase = get(),
            deleteCartUseCase = get(),
            importSharedCartUseCase = get(),
            openCartUseCase = get(),
            dispatcherProvider = get(),
            generateCartShareLinkUseCase = get(),
            updateCartNameUseCase = get(),
            dataStore = get()
        )
    }

    single<SearchEventsUseCase> { SearchEventsUseCase(database = get()) }
    viewModel {
        SearchViewModel(
            searchEventsUseCase = get(), dispatcherProvider = get()
        )
    }

    single<LoadCartUseCase> { LoadCartUseCase(database = get()) }
    single<AddCartItemUseCase> { AddCartItemUseCase(database = get()) }
    single<UpdateCartItemUseCase> { UpdateCartItemUseCase(database = get()) }
    single<DeleteCartItemUseCase> { DeleteCartItemUseCase(database = get()) }

    viewModel {
        CartViewModel(
            loadCartUseCase = get(),
            addCartItemUseCase = get(),
            updateCartItemUseCase = get(),
            deleteCartItemUseCase = get(),
            generateCartShareLinkUseCase = get(),
            dataStore = get(),
            dispatcherProvider = get()
        )
    }
}