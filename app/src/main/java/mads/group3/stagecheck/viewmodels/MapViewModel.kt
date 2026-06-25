package mads.group3.stagecheck.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mads.group3.stagecheck.common.repositories.EventsRepository
import mads.group3.stagecheck.common.repositories.VenueRepository
import mads.group3.stagecheck.models.Event
import mads.group3.stagecheck.models.Venue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapViewModel(
    private val venuesRepo: VenueRepository = VenueRepository(),
    private val eventsRepo: EventsRepository = EventsRepository()
) : ViewModel() {
    private val _venues = MutableStateFlow<List<Venue>>(emptyList())
    val venues: StateFlow<List<Venue>> = _venues.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _selectedVenueId = MutableStateFlow<String?>(null)
    val selectedVenueId: StateFlow<String?> = _selectedVenueId.asStateFlow()

    private val _selectedVenueEvents = MutableStateFlow<List<Event>>(emptyList())
    val selectedVenueEvents: StateFlow<List<Event>> = _selectedVenueEvents.asStateFlow()

    private val _isLoadingEvents = MutableStateFlow(false)
    val isLoadingEvents: StateFlow<Boolean> = _isLoadingEvents.asStateFlow()

    private val _eventsError = MutableStateFlow<String?>(null)
    val eventsError: StateFlow<String?> = _eventsError.asStateFlow()

    private val dateFormat by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    fun loadVenues() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetched = venuesRepo.getVenues()
                _venues.value = fetched
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() = loadVenues()

    fun selectVenue(venueId: String) {
        if (_selectedVenueId.value == venueId) {
            clearSelectedVenue()
            return
        }
        _selectedVenueId.value = venueId
        _isLoadingEvents.value = true
        _eventsError.value = null
        viewModelScope.launch {
            try {
                val allEvents = eventsRepo.getEventsByVenueId(venueId)
                val filteredAndSorted = allEvents
                    .filter { event ->
                        val status = event.status?.lowercase()
                        status != "cancelled"
                    }
                    .filter { event ->
                        val dateStr = event.localDate
                        if (dateStr.isNullOrBlank()) return@filter false
                        try {
                            val eventDate = dateFormat.parse(dateStr)
                            val today = dateFormat.parse(dateFormat.format(Date()))
                            eventDate != null && !eventDate.before(today)
                        } catch (e: Exception) {
                            e.message?.let { Log.e("MapViewModel - selectVenue", it) }
                            false
                        }

                    }
                    .sortedWith(compareBy { event ->
                        try {
                            val date = dateFormat.parse(event.localDate ?: "")
                            date?.time ?: 0L
                        } catch (_: Exception) {
                            0L
                        }
                    })
                _selectedVenueEvents.value = filteredAndSorted
            } catch (e: Exception) {
                _eventsError.value = e.message
                _selectedVenueEvents.value = emptyList()
            } finally {
                _isLoadingEvents.value = false
            }
        }
    }

    fun clearSelectedVenue() {
        _selectedVenueId.value = null
        _selectedVenueEvents.value = emptyList()
        _isLoadingEvents.value = false
        _eventsError.value = null
    }
}