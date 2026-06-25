package mads.group3.stagecheck.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Event(
    @DocumentId
    val docId: String = "",

    @PropertyName("ticketmasterUrl")
    val ticketmasterUrl: String? = null,

    @PropertyName("name")
    val name: String? = null,

    @PropertyName("genre")
    val genre: String? = null,

    @PropertyName("subGenre")
    val subGenre: String? = null,

    @PropertyName("artistIds")
    val artistIds: List<String> = emptyList(),

    @PropertyName("headliner")
    val headliner: String? = null,

    @PropertyName("venueName")
    val venueName: String? = null,

    @PropertyName("venueCity")
    val venueCity: String? = null,

    @PropertyName("venueState")
    val venueState: String? = null,

    @PropertyName("venueCountry")
    val venueCountry: String? = null,

    @PropertyName("venueAddress")
    val venueAddress: String? = null,

    @PropertyName("venueLat")
    val venueLat: Double? = null,

    @PropertyName("venueLong")
    val venueLong: Double? = null,

    @PropertyName("localDate")
    val localDate: String? = null,  // YYYY-MM-DD

    @PropertyName("localTime")
    val localTime: String? = null,  // HH:MM:SS

    @PropertyName("status")
    val status: String? = null,

    @PropertyName("eventImage3x2")
    val eventImage3x2: String? = null,

    @PropertyName("eventImage16x9")
    val eventImage16x9: String? = null,

    @PropertyName("venueDirectoryId")
    val venueDirectoryId: String? = null,

    @PropertyName("lastSyncedAt")
    val lastSyncedAt: Timestamp? = null
)