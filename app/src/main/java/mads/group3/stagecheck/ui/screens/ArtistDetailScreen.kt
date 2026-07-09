package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import mads.group3.stagecheck.models.Artist
import mads.group3.stagecheck.viewmodels.ArtistDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistId: String,
    onBack: () -> Unit,
    viewModel: ArtistDetailViewModel = viewModel()
) {
    LaunchedEffect(artistId) {
        viewModel.loadArtist(artistId)
    }

    val artist by viewModel.artist.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(artist?.name ?: "Artist Details")
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
                        Button(onClick = { viewModel.loadArtist(artistId) }) {
                            Text("Retry")
                        }
                    }
                }

                artist != null -> {
                    ArtistDetailContent(
                        artist = artist!!,
                        isFollowing = viewModel.isFollowing.collectAsState().value,
                        onFollowClick = { viewModel.toggleFollow(artist!!.id) }
                    )
                }

                else -> {
                    Text("Artist $artistId not found", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun ArtistDetailContent(
    artist: Artist,
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            AsyncImage(
                model = artist.image16x9 ?: artist.image3x2,
                contentDescription = artist.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                error = painterResource(id = android.R.drawable.ic_menu_report_image)
            )

            IconButton(
                onClick = onFollowClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isFollowing) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (isFollowing) "Unfollow artist" else "Follow artist",
                    tint = if (isFollowing) Color.Yellow else Color.White,
                    modifier = Modifier
                        .size(28.dp)
                        .shadow(4.dp, CircleShape)
                )
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "About The Band",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.background
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (artist.bio.isNullOrBlank()) {
                Text(
                    text = "Bio for ${artist.name ?: "the artist"} will be added soon.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            } else {
                Text(
                    text = artist.bio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
            }
        }
    }
}