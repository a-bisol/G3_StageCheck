package mads.group3.stagecheck.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mads.group3.stagecheck.common.repositories.VenueRepository
import mads.group3.stagecheck.models.Venue

class MapViewModel(
    private val venuesRepo: VenueRepository = VenueRepository()
) : ViewModel() {
    private val _venues = MutableStateFlow<List<Venue>>(emptyList())
    val venues: StateFlow<List<Venue>> = _venues.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadVenues()
    }

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
}