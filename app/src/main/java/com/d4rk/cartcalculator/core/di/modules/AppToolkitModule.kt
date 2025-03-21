package com.d4rk.cartcalculator.core.di.modules

import com.d4rk.android.libs.apptoolkit.app.help.domain.model.ui.HelpScreenConfig
import com.d4rk.android.libs.apptoolkit.app.help.domain.usecases.GetFAQsUseCase
import com.d4rk.android.libs.apptoolkit.app.help.domain.usecases.LaunchReviewFlowUseCase
import com.d4rk.android.libs.apptoolkit.app.help.domain.usecases.RequestReviewFlowUseCase
import com.d4rk.android.libs.apptoolkit.app.help.ui.HelpViewModel
import com.d4rk.android.libs.apptoolkit.app.support.domain.usecases.QuerySkuDetailsUseCase
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportViewModel
import com.d4rk.cartcalculator.BuildConfig
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appToolkitModule : Module = module {
    single<QuerySkuDetailsUseCase> { QuerySkuDetailsUseCase() }
    viewModel {
        SupportViewModel(querySkuDetailsUseCase = get() , dispatcherProvider = get())
    }

    single<HelpScreenConfig> { HelpScreenConfig(versionName = BuildConfig.VERSION_NAME , versionCode = BuildConfig.VERSION_CODE) }
    single<GetFAQsUseCase> { GetFAQsUseCase(application = get()) }
    single<RequestReviewFlowUseCase> { RequestReviewFlowUseCase(application = get()) }
    single<LaunchReviewFlowUseCase> { LaunchReviewFlowUseCase() }

    viewModel {
        HelpViewModel(getFAQsUseCase = get() , requestReviewFlowUseCase = get() , launchReviewFlowUseCase = get() , dispatcherProvider = get())
    }
}