package com.d4rk.cartcalculator.ui.components.navigation

import android.content.Context
import android.view.SoundEffectConstants
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.d4rk.cartcalculator.R
import com.d4rk.cartcalculator.data.datastore.DataStore
import com.d4rk.cartcalculator.data.model.ui.navigation.NavigationDrawerItem
import com.d4rk.cartcalculator.ui.components.ads.AdBanner
import com.d4rk.cartcalculator.ui.components.animations.bounceClick
import com.d4rk.cartcalculator.ui.components.animations.hapticDrawerSwipe
import com.d4rk.cartcalculator.ui.screens.help.HelpActivity
import com.d4rk.cartcalculator.ui.screens.home.HomeScreen
import com.d4rk.cartcalculator.ui.screens.home.HomeViewModel
import com.d4rk.cartcalculator.ui.screens.support.SupportActivity
import com.d4rk.cartcalculator.utils.IntentUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawer(
    drawerState : DrawerState ,
    view : View ,
    context : Context ,
) {
    val scope : CoroutineScope = rememberCoroutineScope()
    val drawerItems : List<NavigationDrawerItem> = listOf(
        NavigationDrawerItem(
            title = R.string.settings ,
            selectedIcon = Icons.Outlined.Settings ,
        ) ,
        NavigationDrawerItem(
            title = R.string.help_and_feedback ,
            selectedIcon = Icons.AutoMirrored.Outlined.HelpOutline ,
        ) ,
        NavigationDrawerItem(
            title = R.string.updates ,
            selectedIcon = Icons.AutoMirrored.Outlined.EventNote ,
        ) ,
        NavigationDrawerItem(
            title = R.string.share , selectedIcon = Icons.Outlined.Share
        ) ,
    )

    val selectedItemIndex : Int by rememberSaveable { mutableIntStateOf(value = - 1) }

    val viewModel : HomeViewModel = viewModel()

    ModalNavigationDrawer(modifier = Modifier.hapticDrawerSwipe(drawerState) ,
                          drawerState = drawerState ,
                          drawerContent = {
                              ModalDrawerSheet {
                                  Spacer(modifier = Modifier.height(16.dp))
                                  drawerItems.forEachIndexed { index , item ->
                                      val title : String = stringResource(id = item.title)
                                      NavigationDrawerItem(label = { Text(text = title) } ,
                                                           selected = index == selectedItemIndex ,
                                                           onClick = {
                                                               when (item.title) {
                                                                   R.string.settings -> {
                                                                       view.playSoundEffect(
                                                                           SoundEffectConstants.CLICK
                                                                       )
                                                                       IntentUtils.openActivity(
                                                                           context ,
                                                                           com.d4rk.cartcalculator.ui.screens.settings.SettingsActivity::class.java
                                                                       )
                                                                   }

                                                                   R.string.help_and_feedback -> {
                                                                       view.playSoundEffect(
                                                                           SoundEffectConstants.CLICK
                                                                       )
                                                                       IntentUtils.openActivity(
                                                                           context ,
                                                                           HelpActivity::class.java
                                                                       )
                                                                   }

                                                                   R.string.updates -> {
                                                                       view.playSoundEffect(
                                                                           SoundEffectConstants.CLICK
                                                                       )
                                                                       IntentUtils.openUrl(
                                                                           context ,
                                                                           url = "https://github.com/D4rK7355608/${context.packageName}/blob/master/CHANGELOG.md"
                                                                       )
                                                                   }

                                                                   R.string.share -> {
                                                                       view.playSoundEffect(
                                                                           SoundEffectConstants.CLICK
                                                                       )
                                                                       IntentUtils.shareApp(context)
                                                                   }
                                                               }
                                                               scope.launch { drawerState.close() }
                                                           } ,
                                                           icon = {
                                                               Icon(
                                                                   item.selectedIcon ,
                                                                   contentDescription = title
                                                               )
                                                           } ,
                                                           badge = {
                                                               item.badgeText.isNotBlank().let {
                                                                   Text(text = item.badgeText)
                                                               }
                                                           } ,
                                                           modifier = Modifier
                                                                   .padding(
                                                                       NavigationDrawerItemDefaults.ItemPadding
                                                                   )
                                                                   .bounceClick())
                                  }
                              }
                          } ,
                          content = {
                              Scaffold(topBar = {
                                  TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) } ,
                                            navigationIcon = {
                                                IconButton(modifier = Modifier.bounceClick() ,
                                                           onClick = {
                                                               view.playSoundEffect(
                                                                   SoundEffectConstants.CLICK
                                                               )
                                                               scope.launch {
                                                                   drawerState.apply {
                                                                       if (isClosed) open() else close()
                                                                   }
                                                               }
                                                           }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Menu ,
                                                        contentDescription = stringResource(id = R.string.navigation_drawer_open)
                                                    )
                                                }
                                            } ,
                                            actions = {
                                                IconButton(modifier = Modifier.bounceClick() ,
                                                           onClick = {
                                                               view.playSoundEffect(
                                                                   SoundEffectConstants.CLICK
                                                               )
                                                               IntentUtils.openActivity(
                                                                   context ,
                                                                   SupportActivity::class.java
                                                               )
                                                           }) {
                                                    Icon(
                                                        Icons.Outlined.VolunteerActivism ,
                                                        contentDescription = stringResource(id = R.string.support_us)
                                                    )
                                                }
                                            })
                              } , floatingActionButton = {
                                  ExtendedFloatingActionButton(modifier = Modifier.bounceClick() ,
                                                               text = {
                                                                   Text(
                                                                       text = stringResource(R.string.add_new_cart)
                                                                   )
                                                               } ,
                                                               onClick = {
                                                                   view.playSoundEffect(
                                                                       SoundEffectConstants.CLICK
                                                                   )
                                                                   viewModel.openNewCartDialog()
                                                               } ,
                                                               icon = {
                                                                   Icon(
                                                                       Icons.Outlined.AddShoppingCart ,
                                                                       contentDescription = null
                                                                   )
                                                               })
                              } , bottomBar = {
                                  val dataStore = DataStore.getInstance(context)
                                  AdBanner(
                                      dataStore = dataStore , modifier = Modifier.padding(
                                          bottom = (WindowInsets.navigationBars.asPaddingValues()
                                                  .calculateBottomPadding() + 8.dp)
                                      )
                                  )
                              }) { paddingValues ->
                                  Box(
                                      modifier = Modifier.padding(paddingValues)
                                  ) {
                                      HomeScreen(
                                          context = context , view = view , viewModel = viewModel
                                      )
                                  }
                              }
                          })
}