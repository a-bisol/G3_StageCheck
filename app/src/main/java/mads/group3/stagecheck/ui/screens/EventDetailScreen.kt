package mads.group3.stagecheck.ui.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import mads.group3.stagecheck.models.Artist
import mads.group3.stagecheck.models.Event
import mads.group3.stagecheck.navigation.Screens
import mads.group3.stagecheck.viewmodels.EventDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    eventId: String,
    onBack: () -> Unit,
    navController: NavController,
    viewModel: EventDetailViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val event by viewModel.event.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    event?.let {
                        val formattedDate = formatDateForTitle(it.localDate)
                        Text("$formattedDate - ${it.headliner ?: it.name ?: "Event"}")
                    } ?: Text("Event Details")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("Error: $error")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadEvent(eventId) }) {
                            Text("Retry")
                        }
                    }
                }

                event != null -> {
                    EventDetailContent(
                        event = event!!,
                        artists = artists,
                        onShareClick = { shareEvent(context, event!!) },
                        onTicketClick = { event!!.ticketmasterUrl?.let { openUrl(context, it) } },
                        onArtistClick = { artistId ->
                            navController.navigate(
                                Screens.DetailArtist.passArtistId(
                                    artistId
                                )
                            )
                        }
                    )
                }

                else -> {
                    Text("Event not found", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun EventDetailContent(
    event: Event,
    artists: List<Artist>,
    onShareClick: () -> Unit,
    onTicketClick: () -> Unit,
    onArtistClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AsyncImage(
            model = event.eventImage16x9 ?: event.eventImage3x2,
            contentDescription = "Event image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
            error = painterResource(id = android.R.drawable.ic_menu_report_image)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = event.name ?: event.headliner ?: "Untitled Event",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onShareClick) {
                Icon(Icons.Default.Share, contentDescription = "Share")
            }
        }

        VenueDateSection(event)

        if (artists.isNotEmpty()) {
            Text(
                text = "Lineup",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .height(120.dp)
            ) {
                items(artists, key = { it.id }) { artist ->
                    ArtistCard(
                        artist = artist,
                        onClick = { onArtistClick(artist.id) }
                    )
                }
            }
        }

        Button(
            onClick = onTicketClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            enabled = event.ticketmasterUrl != null
        ) {
            Text("Buy Tickets")
        }
    }
}

@Composable
fun VenueDateSection(event: Event) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = event.venueName ?: "Venue TBD",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buildString {
                event.venueAddress?.let { append(it) }
                if (event.venueCity != null) {
                    if (isNotEmpty()) append(", ")
                    append(event.venueCity)
                }
                if (event.venueState != null) {
                    if (isNotEmpty()) append(", ")
                    append(event.venueState)
                }
                if (event.venueCountry != null) {
                    if (isNotEmpty()) append(", ")
                    append(event.venueCountry)
                }
            }.ifEmpty { "Address not available" },
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = formatEventDateTimeFull(event.localDate, event.localTime) ?: "Date & time TBD",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun ArtistCard(
    artist: Artist,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = artist.image3x2 ?: artist.image16x9,
            contentDescription = artist.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop,
            error = painterResource(id = android.R.drawable.ic_menu_gallery)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = artist.name ?: "Unknown",
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

//Helper functions
fun formatDateForTitle(dateStr: String?): String {
    if (dateStr == null) return "TBD"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        val date = inputFormat.parse(dateStr)
        date?.let { outputFormat.format(it) } ?: "TBD"
    } catch (e: Exception) {
        e.message?.let { Log.e("EventDetail - formatDateForTitle", it) }
        "TBD"
    }
}

fun formatEventDateTimeFull(dateStr: String?, timeStr: String?): String? {
    if (dateStr == null) return null
    return try {
        val datePart = formatDateForTitle(dateStr)
        if (timeStr != null) {
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val outputTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val time = timeFormat.parse(timeStr)
            val formattedTime = time?.let { outputTimeFormat.format(it) } ?: timeStr
            "$datePart at $formattedTime"
        } else {
            datePart
        }
    } catch (e: Exception) {
        e.message?.let { Log.e("EventDetail - formatEventDateTimeFull", it) }
        dateStr
    }
}

fun shareEvent(context: Context, event: Event) {
    val shareText = buildString {
        append("🎫 ${event.headliner ?: event.name ?: "Event"}\n")
        append("📍 ${event.venueName ?: "Venue TBD"}\n")
        append("📅 ${formatEventDateTimeFull(event.localDate, event.localTime) ?: "Date TBD"}\n")
        event.ticketmasterUrl?.let { append("\n🎟️ Get tickets: $it") }
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    context.startActivity(Intent.createChooser(intent, "Share Event"))
}

fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}