package com.d4rk.cartcalculator.app.settings.settings.utils.providers

import android.content.Context
import com.d4rk.android.libs.apptoolkit.app.privacy.routes.permissions.utils.interfaces.PermissionsProvider
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsCategory
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsConfig
import com.d4rk.android.libs.apptoolkit.app.settings.settings.domain.model.SettingsPreference

class PermissionsSettingsProvider : PermissionsProvider {
    override fun providePermissionsConfig(context : Context) : SettingsConfig {
        return SettingsConfig(
            title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.permissions) , categories = listOf(
                SettingsCategory(
                    title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.normal) ,
                    preferences = listOf(
                        SettingsPreference(
                            key = "ad_id" ,
                            title = "sddsaddsadsa" ,
                            summary = "dsdaddsad" ,
                        ) ,
                        SettingsPreference(
                            key = "ad_id" ,
                            title = "sddsaddsadsa" ,
                            summary = "dsdaddsad" ,
                        ) ,
                        SettingsPreference(
                            key = "ad_id" ,
                            title = "sddsaddsadsa" ,
                            summary = "dsdaddsad" ,
                        ) ,
                    ) ,
                ),
                SettingsCategory(
                    title = context.getString(com.d4rk.android.libs.apptoolkit.R.string.normal) ,
                    preferences = listOf(
                        SettingsPreference(
                            key = "ad_id" ,
                            title = "sddsaddsadsa" ,
                            summary = "dsdaddsad" ,
                        ) ,
                        SettingsPreference(
                            key = "ad_id" ,
                            title = "sddsaddsadsa" ,
                            summary = "dsdaddsad" ,
                        ) ,
                        SettingsPreference(
                            key = "ad_id" ,
                            title = "sddsaddsadsa" ,
                            summary = "dsdaddsad" ,
                        ) ,
                    ) ,
                )
            )
        )
    }
}