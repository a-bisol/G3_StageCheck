package mads.group3.stagecheck.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mads.group3.stagecheck.common.network.EventResponse
import mads.group3.stagecheck.common.network.RetrofitClient
import mads.group3.stagecheck.common.network.SearchRequest
import mads.group3.stagecheck.models.Event
import mads.group3.stagecheck.models.ExtendedSearchOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.collections.emptyList

class SearchViewModel : ViewModel() {
    private val api = RetrofitClient.api

    private val _searchResults = MutableStateFlow<List<Event>>(emptyList())
    val searchResults: StateFlow<List<Event>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun search(query: String, settings: ExtendedSearchOptions) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val artistParts = mutableListOf<String>()
                if (query.isNotBlank()) artistParts.add(query)
                if (settings.venue.isNotBlank()) artistParts.add(settings.venue)
                if (settings.artist.isNotBlank()) artistParts.add(settings.artist)
                val artist = if (artistParts.isNotEmpty()) artistParts.joinToString(" ") else null

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val startDate = settings.startDate?.let { dateFormat.format(Date(it)) }
                val endDate = settings.endDate?.let {dateFormat.format(Date(it))}

                val request = SearchRequest(
                    city = settings.city.takeIf{it.isNotBlank()},
                    radius = if (settings.unit != "none") settings.distance else null,
                    unit = if (settings.unit != "none") settings.unit else null,
                    startDate = startDate,
                    endDate = endDate,
                    limit = 25,
                    classificationName = "Music",
                    artist = artist
                )

                val response = api.searchEvents(request)
                if (response.success) {
                    val events = response.events.map{mapToEvent(it)}
                    _searchResults.value = events
                } else {
                    _error.value = "Search failed"
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("SearchViewModel-search", it) }
                Log.e("SearchViewModel", e.toString())
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun mapToEvent(response: EventResponse): Event {
        return Event(
            docId = response.id,
            ticketmasterUrl = response.ticketmasterUrl,
            name = response.name,
            genre = response.genre,
            subGenre = response.subGenre,
            artistIds = response.artistIds ?: emptyList(),
            headliner = response.headliner,
            venueName = response.venueName,
            venueCity = response.venueCity,
            venueState = response.venueState,
            venueCountry = response.venueCountry,
            venueAddress = response.venueAddress,
            venueLat = response.venueLat,
            venueLong = response.venueLong,
            localDate = response.localDate,
            localTime = response.localTime,
            status = response.status,
            eventImage3x2 = response.eventImage3x2,
            eventImage16x9 = response.eventImage16x9,
            lastSyncedAt = null
        )
    }

    fun clearResults() {
        _searchResults.value = emptyList()
        _error.value = null
    }
}