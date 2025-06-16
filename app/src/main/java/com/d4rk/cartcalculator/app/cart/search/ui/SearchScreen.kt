package com.d4rk.android.apps.weddix.app.events.search.ui

import android.content.Intent
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.d4rk.android.apps.weddix.R
import com.d4rk.android.apps.weddix.app.events.details.ui.EventDetailsActivity
import com.d4rk.android.apps.weddix.app.events.list.domain.model.EventCategory
import com.d4rk.android.apps.weddix.app.events.list.ui.components.EventCategoriesRow
import com.d4rk.android.apps.weddix.app.events.list.ui.components.EventCategoryItem
import com.d4rk.cartcalculator.app.cart.search.domain.actions.SearchEvent
import com.d4rk.cartcalculator.app.cart.search.domain.data.model.ui.UiSearchData
import com.d4rk.android.apps.weddix.core.data.database.table.EventDetailsTable
import com.d4rk.android.apps.weddix.core.data.database.table.EventsListTable
import com.d4rk.android.apps.weddix.core.utils.extensions.toIcon
import com.d4rk.android.apps.weddix.core.utils.extensions.toLabelRes
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.LoadingScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.NoDataScreen
import com.d4rk.android.libs.apptoolkit.core.ui.components.layouts.ScreenStateHandler
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraSmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.MediumHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallHorizontalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQueryEncoded : String? , paddingValues : PaddingValues , searchViewModel : SearchViewModel = koinViewModel()
) {
    val screenStateValue by searchViewModel.uiState.collectAsState()
    val uiData = screenStateValue.data ?: UiSearchData()
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (! uiData.initialQueryProcessed) {
            searchViewModel.onEvent(SearchEvent.ProcessInitialQuery(initialQueryEncoded))
        }
    }

    LaunchedEffect(uiData.currentQuery , uiData.initialQueryProcessed) {
        if (uiData.currentQuery.isEmpty() && uiData.initialQueryProcessed) {
            focusRequester.requestFocus()
        }
    }

    ScreenStateHandler(screenState = screenStateValue , onLoading = {
        LoadingScreen()
    } , onEmpty = {
        NoDataScreen(text = R.string.no_events_found, icon = Icons.Outlined.EventBusy)
    } , onSuccess = { successData ->
        SearchScreenContent(
            paddingValues = paddingValues , searchData = successData , onItemClick = { eventId ->
                runCatching {
                    val intent = Intent(context , EventDetailsActivity::class.java).apply {
                        putExtra("eventId" , eventId)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            })
    })
}

@Composable
fun SearchScreenContent(
    paddingValues : PaddingValues , searchData : UiSearchData , onItemClick : (eventId : Int) -> Unit
) {
    val eventsToShow : MutableList<EventsListTable> = searchData.uiEventsWithDetails.events

    LazyColumn(
        modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = SizeConstants.MediumSize),
        verticalArrangement = Arrangement.spacedBy(space = SizeConstants.MediumSize)
    ) {
        items(
            items = eventsToShow , key = { event -> event.eventId }) { eventItem ->
            EventListItem(
                event = eventItem , details = searchData.uiEventsWithDetails.eventDetailsMap[eventItem.eventId] , onClick = { onItemClick(eventItem.eventId) })
        }
    }
}

@Composable
fun EventListItem(
    event : EventsListTable , details : List<EventDetailsTable>? , onClick : () -> Unit
) {
    val view = LocalView.current
    val dateString = remember(event.dateCreated) {
        SimpleDateFormat("dd MMM yyyy" , Locale.getDefault()).format(Date(event.dateCreated))
    }

    OutlinedCard(
        shape = RoundedCornerShape(size = SizeConstants.MediumSize) , modifier = Modifier.fillMaxWidth() , onClick = {
            view.playSoundEffect(SoundEffectConstants.CLICK)
            onClick()
        }) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(SizeConstants.MediumSize) , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f) , verticalArrangement = Arrangement.spacedBy(SizeConstants.SmallSize)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                                .size(SizeConstants.ExtraExtraLargeSize)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer) , contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = event.eventType.toIcon() , contentDescription = stringResource(event.eventType.toLabelRes()) , modifier = Modifier.size(SizeConstants.ExtraLargeIncreasedSize * 0.6f)
                        )
                    }
                    MediumHorizontalSpacer()

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = event.eventName , style = MaterialTheme.typography.titleLarge , maxLines = 1 , overflow = TextOverflow.Ellipsis , fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(event.eventType.toLabelRes()) , style = MaterialTheme.typography.labelMedium , textAlign = TextAlign.Start
                        )
                        ExtraSmallVerticalSpacer()

                        details?.forEach { detailEntry ->
                            if (detailEntry.eventLocation.isNotBlank()) {
                                Text(
                                    text = detailEntry.eventLocation , style = MaterialTheme.typography.labelMedium , textAlign = TextAlign.Start
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth() , horizontalArrangement = Arrangement.SpaceBetween , verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (event.eventDate !in listOf(0L , - 1L) && ! details.isNullOrEmpty()) {
                                val eventDateString = remember(event.eventDate) {
                                    SimpleDateFormat("dd MMM yyyy" , Locale.getDefault()).format(Date(event.eventDate))
                                }
                                Text(
                                    text = stringResource(R.string.event_date_prefix , eventDateString) , style = MaterialTheme.typography.labelMedium
                                )
                            }

                            if (event.eventDate in listOf(0L , - 1L) || details.isNullOrEmpty()) {
                                val hasPrice = details?.any { it.price.isNotBlank() && it.price != "0.0" } == true
                                if (hasPrice && event.eventDate in listOf(0L , - 1L)) {
                                    Box(Modifier.weight(1f))
                                }
                            }

                            details?.firstNotNullOfOrNull { entry ->
                                entry.price.takeIf { it.isNotEmpty() && it != "0.0" }
                            }?.let { price ->
                                Text(
                                    text = stringResource(R.string.event_price_prefix , price) , style = MaterialTheme.typography.labelLarge , color = Color.Green , fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth() , verticalAlignment = Alignment.CenterVertically , horizontalArrangement = Arrangement.SpaceBetween) {
                    EventCategoryItem(
                        eventCategory = EventCategory(
                            icon = Icons.Outlined.DateRange , text = stringResource(R.string.created_on , dateString)
                        )
                    )
                    SmallHorizontalSpacer()
                    EventCategoriesRow(event = event)
                }
            }
        }
    }
}