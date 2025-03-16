package com.d4rk.cartcalculator.core.di

import android.content.Context
import com.d4rk.android.libs.apptoolkit.core.di.DispatcherProvider
import com.d4rk.android.libs.apptoolkit.core.di.StandardDispatchers
import com.d4rk.android.libs.apptoolkit.data.core.ads.AdsCoreManager
import com.d4rk.android.libs.apptoolkit.ui.screens.help.domain.model.ui.HelpScreenConfig
import com.d4rk.android.libs.apptoolkit.ui.screens.help.domain.usecases.GetFAQsUseCase
import com.d4rk.android.libs.apptoolkit.ui.screens.help.domain.usecases.LaunchReviewFlowUseCase
import com.d4rk.android.libs.apptoolkit.ui.screens.help.domain.usecases.RequestReviewFlowUseCase
import com.d4rk.android.libs.apptoolkit.ui.screens.help.ui.HelpViewModel
import com.d4rk.cartcalculator.BuildConfig
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
import com.d4rk.cartcalculator.app.home.ui.HomeViewModel
import com.d4rk.cartcalculator.app.main.ui.MainViewModel
import com.d4rk.cartcalculator.core.data.database.AppDatabase
import com.d4rk.cartcalculator.core.data.database.DataBaseImplementation
import com.d4rk.cartcalculator.core.data.database.DatabaseInterface
import com.d4rk.cartcalculator.core.data.datastore.DataStore
import com.d4rk.cartcalculator.core.domain.usecases.cart.GenerateCartShareLinkUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule : Module = module {
    single<DispatcherProvider> { StandardDispatchers() }
    single<AppDatabase> { AppDatabase.getInstance(context = get()) }
    single<DatabaseInterface> { DataBaseImplementation(database = get()) }
    single<DataStore> { DataStore.getInstance(context = get()) }
    single<AdsCoreManager> { AdsCoreManager(context = get()) }

    viewModel {
        MainViewModel()
    }

    single<GetCartsUseCase> { GetCartsUseCase(database = get()) }
    single<DecryptSharedCartUseCase> { DecryptSharedCartUseCase() }
    single<AddCartUseCase> { AddCartUseCase(database = get()) }
    single<DeleteCartUseCase> { DeleteCartUseCase(database = get()) }
    single<ImportSharedCartUseCase> { ImportSharedCartUseCase(database = get() , decryptSharedCartUseCase = get()) }
    single<OpenCartUseCase> { OpenCartUseCase(context = get()) }
    single<GenerateCartShareLinkUseCase> { GenerateCartShareLinkUseCase(database = get()) }

    viewModel {
        HomeViewModel(getCartsUseCase = get() , addCartUseCase = get() , deleteCartUseCase = get() , importSharedCartUseCase = get() , openCartUseCase = get() , dispatcherProvider = get() , generateCartShareLinkUseCase = get())
    }

    single<LoadCartUseCase> { LoadCartUseCase(database = get()) }
    single<AddCartItemUseCase> { AddCartItemUseCase(database = get()) }
    single<UpdateCartItemUseCase> { UpdateCartItemUseCase(database = get()) }
    single<DeleteCartItemUseCase> { DeleteCartItemUseCase(database = get()) }

    viewModel {
        CartViewModel(loadCartUseCase = get() , addCartItemUseCase = get() , updateCartItemUseCase = get() , deleteCartItemUseCase = get() , generateCartShareLinkUseCase = get() , dispatcherProvider = get())
    }
}

val appToolkitModule : Module = module {
    single<HelpScreenConfig> { HelpScreenConfig(versionName = BuildConfig.VERSION_NAME , versionCode = BuildConfig.VERSION_CODE) }
    single<GetFAQsUseCase> { GetFAQsUseCase(application = get()) }
    single<RequestReviewFlowUseCase> { RequestReviewFlowUseCase(application = get()) }
    single<LaunchReviewFlowUseCase> { LaunchReviewFlowUseCase() }

    viewModel {
        HelpViewModel(getFAQsUseCase = get() , requestReviewFlowUseCase = get() , launchReviewFlowUseCase = get() , dispatcherProvider = get())
    }
}

fun initializeKoin(context : Context) {
    startKoin {
        androidContext(androidContext = context)
        modules(modules = listOf(appModule, appToolkitModule))
    }
}