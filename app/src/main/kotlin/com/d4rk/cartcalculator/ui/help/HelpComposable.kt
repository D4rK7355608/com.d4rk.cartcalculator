package com.d4rk.cartcalculator.ui.help

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.d4rk.cartcalculator.BuildConfig
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.utils.Utils
import com.d4rk.cartcalculator.utils.bounceClick
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpComposable(activity: HelpActivity) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val showDialog = remember { mutableStateOf(false) }
    Scaffold(modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
        LargeTopAppBar(title = { Text(stringResource(R.string.help)) }, navigationIcon = {
            IconButton(onClick = {
                activity.finish()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }, actions = {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text(stringResource(R.string.view_in_google_play_store)) },
                    onClick = {
                        Utils.openUrl(
                            context,
                            "https://play.google.com/store/apps/details?id=${activity.packageName}"
                        )
                    })
                DropdownMenuItem(text = { Text(stringResource(R.string.version_info)) },
                    onClick = { showDialog.value = true })
                DropdownMenuItem(text = { Text(stringResource(R.string.beta_program)) },
                    onClick = {
                        Utils.openUrl(
                            context,
                            "https://play.google.com/apps/testing/${activity.packageName}"
                        )
                    })
                DropdownMenuItem(text = { Text(stringResource(R.string.terms_of_service)) },
                    onClick = {
                        Utils.openUrl(
                            context,
                            "https://sites.google.com/view/d4rk7355608/more/apps/terms-of-service"
                        )
                    })
                DropdownMenuItem(text = { Text(stringResource(R.string.privacy_policy)) },
                    onClick = {
                        Utils.openUrl(
                            context,
                            "https://sites.google.com/view/d4rk7355608/more/apps/privacy-policy"
                        )
                    })
                DropdownMenuItem(text = { Text(stringResource(com.google.android.gms.oss.licenses.R.string.oss_license_title)) },
                    onClick = {
                        Utils.openActivity(
                            context,
                            OssLicensesMenuActivity::class.java
                        )
                    })
            }
            if (showDialog.value) {
                VersionInfoDialog(onDismiss = { showDialog.value = false })
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            ConstraintLayout(modifier = Modifier.padding(paddingValues)) {
                val (faqTitle, faqCard, fabButton) = createRefs()
                Text(text = stringResource(R.string.faq),
                    modifier = Modifier
                        .padding(bottom = 24.dp)
                        .constrainAs(faqTitle) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        })
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(faqCard) {
                        top.linkTo(faqTitle.bottom)
                        bottom.linkTo(parent.bottom)
                    }) {
                    FAQComposable()
                }
                ExtendedFloatingActionButton(
                    text = { Text("Feedback") },
                    onClick = {
                        activity.feedback()
                    },
                    icon = {
                        Icon(
                            Icons.Default.MailOutline, contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .padding(6.dp)
                        .bounceClick()
                        .constrainAs(fabButton) {
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        },
                )
            }
        }
    }
}

@Composable
fun FAQComposable() {
    LazyColumn {
        item {
            QuestionComposable(
                title = stringResource(R.string.question_1),
                summary = stringResource(R.string.summary_preference_faq_1)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_2),
                summary = stringResource(R.string.summary_preference_faq_2)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_3),
                summary = stringResource(R.string.summary_preference_faq_3)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_4),
                summary = stringResource(R.string.summary_preference_faq_5)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_5),
                summary = stringResource(R.string.summary_preference_faq_5)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_6),
                summary = stringResource(R.string.summary_preference_faq_6)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_7),
                summary = stringResource(R.string.summary_preference_faq_7)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_8),
                summary = stringResource(R.string.summary_preference_faq_8)
            )
        }
        item {
            QuestionComposable(
                title = stringResource(R.string.question_9),
                summary = stringResource(R.string.summary_preference_faq_9)
            )
        }
    }
}

@Composable
fun QuestionComposable(title: String, summary: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = summary)
    }
}

@Composable
fun VersionInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = { VersionInfoContent() },
        confirmButton = {},
        dismissButton = {})
}

@Composable
fun VersionInfoContent() {
    val context = LocalContext.current
    val appName = context.getString(R.string.app_name)
    val version = String.format(context.getString(R.string.version), BuildConfig.VERSION_NAME)
    val copyright = context.getString(R.string.copyright)

    val appIcon = context.packageManager.getApplicationIcon(context.packageName)
    val bitmapDrawable = convertAdaptiveIconDrawableToBitmap(appIcon)

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            bitmap = bitmapDrawable.bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(24.dp))
        Column {
            Text(
                text = appName,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = version, style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = copyright, style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun convertAdaptiveIconDrawableToBitmap(drawable: Drawable): BitmapDrawable {
    return when (drawable) {
        is BitmapDrawable -> {
            drawable
        }

        is AdaptiveIconDrawable -> {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            BitmapDrawable(Resources.getSystem(), bitmap)
        }

        else -> {
            throw IllegalArgumentException("Unsupported drawable type")
        }
    }
}