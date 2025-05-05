package com.d4rk.cartcalculator.core.utils.helpers

import android.content.Context
import android.content.Intent

object ShareHelper {

    fun shareText(context : Context , link : String , chooserTitleResId : Int = com.d4rk.android.libs.apptoolkit.R.string.share) {
        val sendIntent : Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT , link)
            type = "content/plain"
        }
        val shareIntent : Intent? = Intent.createChooser(sendIntent , context.getString(chooserTitleResId))

        shareIntent?.let {
            context.startActivity(it)
        }
    }
}