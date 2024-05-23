package com.d4rk.cartcalculator.ui.help

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.d4rk.cartcalculator.ui.settings.display.theme.style.AppTheme
import com.d4rk.cartcalculator.utils.Utils
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class HelpActivity : ComponentActivity() {
    private lateinit var reviewManager: ReviewManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    HelpComposable(this@HelpActivity)
                }
            }
        }

    }

    /**
     * Initiates the feedback process for the app.
     *
     * This function uses the Google Play In-App Review API to prompt the user for feedback.
     * If the request to launch the in-app review flow is successful, the review dialog is displayed.
     * If the request fails, it opens the Google Play Store page for the app's reviews.
     *
     * @see com.google.android.play.core.review.ReviewManagerFactory
     * @see com.google.android.play.core.review.ReviewManager
     * @param context The context used to create the ReviewManager instance and launch review flows.
     */
    fun feedback() {
        Log.d("Testing", "Step 1")
        reviewManager = ReviewManagerFactory.create(this)
        val task = reviewManager.requestReviewFlow()
        task.addOnSuccessListener { reviewInfo ->
            Log.d("Testing", "Step 1.1 - sssss")
            reviewManager.launchReviewFlow(this, reviewInfo)
        }.addOnFailureListener {
            Log.d("Testing", "Step 2 - failure")
            Utils.openUrl(
                this,
                "https://play.google.com/store/apps/details?id=${this.packageName}&showAllReviews=true"
            )
        }.addOnFailureListener {
            Log.d("Testing", "Step 3 - failure")
            Utils.sendEmailToDeveloper(this)
        }
    }
}