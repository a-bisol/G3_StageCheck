package mads.group3.stagecheck.common.network

import com.google.gson.JsonElement

data class SearchResponse(
    val success: Boolean,
    val count: Int,
    val events: List<EventResponse>
)

data class EventResponse(
    val id: String,
    val ticketmasterUrl: String?,
    val name: String?,
    val genre: String?,
    val subGenre: String?,
    val artistIds: List<String>?,
    val headliner: String?,
    val venueName: String?,
    val venueCity: String?,
    val venueState: String?,
    val venueCountry: String?,
    val venueAddress: String?,
    val venueID: String?,
    val venueLat: Double?,
    val venueLong: Double?,
    val localDate: String?,
    val localTime: String?,
    val dateTime: String?,
    val status: String?,
    val eventImage3x2: String?,
    val eventImage16x9: String?,
    val lastSyncedAt: JsonElement? = null
)
