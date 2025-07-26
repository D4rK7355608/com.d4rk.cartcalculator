package com.d4rk.cartcalculator.app.main.ui.components.navigation

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.navigation.NavHostController
import com.d4rk.android.libs.apptoolkit.app.support.ui.SupportActivity
import com.d4rk.android.libs.apptoolkit.core.ui.components.buttons.AnimatedIconButtonDirection
import com.d4rk.android.libs.apptoolkit.core.ui.components.dropdown.CommonDropdownMenuItem
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import com.d4rk.android.libs.apptoolkit.core.utils.helpers.IntentsHelper
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.app.main.utils.constants.NavigationRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    navigationIcon: ImageVector,
    onNavigationIconClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    currentSearchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    navController: NavHostController,
    currentRoute: String?
) {
    var expandedMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isSearchTextFieldFocused by remember { mutableStateOf(false) }

    val isCurrentlyOnSearchScreenPage =
        currentRoute?.startsWith(NavigationRoutes.ROUTE_SEARCH.substringBefore("/{")) == true

    val voiceLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val spokenText =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        ?.firstOrNull()
                if (!spokenText.isNullOrEmpty()) {
                    onSearchQueryChange(spokenText)

                    if (!isCurrentlyOnSearchScreenPage) {
                        navController.navigate(NavigationRoutes.searchScreenRoute(spokenText)) {
                            launchSingleTop = true
                        }
                    }
                    focusRequester.requestFocus()
                }
            }
        }

    TopAppBar(
        modifier = Modifier.fillMaxWidth(), title = {
            BasicTextField(
                value = currentSearchQuery,
                onValueChange = {
                    onSearchQueryChange(it)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SizeConstants.ExtraSmallSize)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        isSearchTextFieldFocused = focusState.isFocused
                    },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (currentSearchQuery.isNotEmpty()) {
                        navController.navigate(NavigationRoutes.searchScreenRoute(currentSearchQuery)) {
                            launchSingleTop = true
                        }
                    }
                    focusManager.clearFocus()
                }),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        innerTextField()
                        if (currentSearchQuery.isEmpty()) {
                            val placeholderText = when {
                                isSearchTextFieldFocused -> stringResource(id = R.string.search_placeholder_focused)
                                isCurrentlyOnSearchScreenPage -> stringResource(id = R.string.search_placeholder_refine)
                                else -> stringResource(id = R.string.search_placeholder_default)
                            }
                            Text(
                                modifier = Modifier.animateContentSize(),
                                text = placeholderText,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                })
        }, navigationIcon = {
            AnimatedIconButtonDirection(
                icon = navigationIcon,
                contentDescription = if (isCurrentlyOnSearchScreenPage) stringResource(id = R.string.content_description_back) else stringResource(
                    id = R.string.content_description_open_menu
                ),
                onClick = {
                    onNavigationIconClick()
                    focusManager.clearFocus()
                },
                vibrate = false
            )
        }, actions = {
            if (isSearchTextFieldFocused || (isCurrentlyOnSearchScreenPage)) {
                AnimatedIconButtonDirection(
                    fromRight = true,
                    icon = Icons.Default.Clear,
                    contentDescription = stringResource(id = R.string.content_description_clear_search),
                    onClick = {
                        onSearchQueryChange("")
                        focusManager.clearFocus()
                    })
            } else {
                val voiceSearchPromptText = stringResource(id = R.string.voice_search_prompt)
                val voiceRecognitionNotAvailableMessage =
                    stringResource(id = R.string.voice_recognition_not_available)
                AnimatedIconButtonDirection(
                    fromRight = true,
                    icon = Icons.Outlined.MicNone,
                    durationMillis = 400,
                    contentDescription = stringResource(id = R.string.content_description_voice_search),
                    onClick = {
                        runCatching {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(
                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                )
                                putExtra(RecognizerIntent.EXTRA_PROMPT, voiceSearchPromptText)
                            }
                            voiceLauncher.launch(intent)
                        }.onFailure {
                            Toast.makeText(
                                context, voiceRecognitionNotAvailableMessage, Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                )
                AnimatedIconButtonDirection(
                    fromRight = true,
                    icon = Icons.Outlined.MoreVert,
                    contentDescription = stringResource(id = R.string.content_description_more_options),
                    onClick = { expandedMenu = true },
                )
                DropdownMenu(expanded = expandedMenu, onDismissRequest = { expandedMenu = false }) {
                    CommonDropdownMenuItem(
                        textResId = com.d4rk.android.libs.apptoolkit.R.string.support_us,
                        icon = Icons.Outlined.VolunteerActivism,
                        onClick = {
                            expandedMenu = false
                            IntentsHelper.openActivity(context, SupportActivity::class.java)
                        }
                    )
                }
            }
        }, scrollBehavior = scrollBehavior, windowInsets = TopAppBarDefaults.windowInsets
    )
}
