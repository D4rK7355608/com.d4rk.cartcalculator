package com.d4rk.cartcalculator.app.onboarding.utils.interfaces.providers

import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Star
import com.d4rk.cartcalculator.app.onboarding.ui.tabs.CartBehaviorOnboardingTab
import com.d4rk.cartcalculator.app.onboarding.utils.constants.OnboardingKeys
import com.d4rk.android.libs.apptoolkit.app.oboarding.domain.data.model.ui.OnboardingPage
import com.d4rk.android.libs.apptoolkit.app.oboarding.ui.components.pages.CrashlyticsOnboardingPageTab
import com.d4rk.android.libs.apptoolkit.app.oboarding.ui.components.pages.FinalOnboardingPageTab
import com.d4rk.android.libs.apptoolkit.app.oboarding.ui.components.pages.ThemeOnboardingPageTab
import com.d4rk.android.libs.apptoolkit.app.oboarding.utils.interfaces.providers.OnboardingProvider
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.main.ui.MainActivity

class AppOnboardingProvider : OnboardingProvider {

    override fun getOnboardingPages(context: Context): List<OnboardingPage> {
        return listOf(
            OnboardingPage.DefaultPage(
                key = OnboardingKeys.WELCOME,
                title = context.getString(R.string.onboarding_welcome_title),
                description = context.getString(R.string.onboarding_welcome_description),
                imageVector = Icons.Outlined.Star
            ),
            OnboardingPage.DefaultPage(
                key = OnboardingKeys.PERSONALIZATION_OPTIONS,
                title = context.getString(R.string.onboarding_personalization_title),
                description = context.getString(R.string.onboarding_personalization_description),
                imageVector = Icons.Outlined.AccountCircle
            ),
            OnboardingPage.CustomPage(
                key = OnboardingKeys.CART_BEHAVIOR_OPTIONS,
                content = {
                    CartBehaviorOnboardingTab()
                }
            ),
            OnboardingPage.CustomPage(
                key = OnboardingKeys.THEME_OPTIONS,
                content = {
                    ThemeOnboardingPageTab()
                }
            ),
            OnboardingPage.CustomPage(
                key = OnboardingKeys.CRASHLYTICS_OPTIONS,
                content = {
                    CrashlyticsOnboardingPageTab()
                }
            ),
            OnboardingPage.CustomPage(
                key = OnboardingKeys.ONBOARDING_COMPLETE,
                content = {
                    FinalOnboardingPageTab()
                }
            ),

        ).filter {
            when (it) {
                is OnboardingPage.DefaultPage -> it.isEnabled
                is OnboardingPage.CustomPage -> it.isEnabled
            }
        }
    }

    override fun onOnboardingFinished(context: Context) {
        context.startActivity(Intent(context, MainActivity::class.java))
    }
}