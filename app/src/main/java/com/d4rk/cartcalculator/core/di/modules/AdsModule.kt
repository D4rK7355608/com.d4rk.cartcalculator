package com.d4rk.cartcalculator.core.di.modules

import com.d4rk.android.libs.apptoolkit.core.domain.model.ads.AdsConfig
import com.d4rk.cartcalculator.core.utils.constants.ads.AdsConstants
import com.google.android.gms.ads.AdSize
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val adsModule : Module = module {
    single<AdsConfig>(named(name = "supportScreen")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.MEDIUM_RECTANGLE)
    }

    single<AdsConfig>(named(name = "mainScreen")) {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.BANNER)
    }

    single<AdsConfig> {
        AdsConfig(bannerAdUnitId = AdsConstants.BANNER_AD_UNIT_ID , adSize = AdSize.LARGE_BANNER)
    }
}