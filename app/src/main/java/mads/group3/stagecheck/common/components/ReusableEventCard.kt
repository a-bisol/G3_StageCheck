package mads.group3.stagecheck.common.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReusableEventCard(
    headliner: String,
    venue: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageUrl: String?,
    localDate: String? = null, // YYYY-MM-DD
    localTime: String? = null, // HH:MM:SS
    cardBackgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer
) {
    val formattedDateTime = formatEventDateTime(localDate, localTime)
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = cardBackgroundColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f, matchHeightConstraintsFirst = true)
                    .background(Color.LightGray)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Event Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = android.R.drawable.ic_menu_report_image)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = headliner,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        modifier = Modifier.width(16.dp),
                        tint = contentColor
                    )
                    Text(
                        text = venue,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = contentColor,
                            fontSize = 14.sp
                        ),
                        maxLines = 1
                    )
                }

                if (formattedDateTime != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formattedDateTime,
                        style = MaterialTheme.typography.bodySmall.copy(color = contentColor),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    thickness = 1.dp,
                    color = contentColor.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Navigate",
                    tint = contentColor,
                    modifier = Modifier
                        .width(40.dp)
                        .padding(end = 8.dp)
                )
            }
        }
    }
}

fun formatEventDateTime(dateStr: String?, timeStr: String?): String? {
    if (dateStr == null || timeStr == null) return null
    try {
        val combinedDateTime = "$dateStr $timeStr"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd ' @ ' hh:mm a", Locale.getDefault())
        val dateTime = inputFormat.parse(combinedDateTime)
        return dateTime?.let { outputFormat.format(it) }
    } catch (e: Exception) {
        e.message?.let { Log.e("EventCard - formatDT", it) }
        return null
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1A1A1A)
@Composable
fun ReusableEventCardPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(16.dp)
        ) {
            ReusableEventCard(
                imageUrl = null,
                headliner = "Super Duper Long Titled Headlining Band",
                venue = "Sneaky Dee's",
                localDate = "2026-06-12",
                localTime = "21:30:00",
                onClick = {}
            )
        }
    }
}