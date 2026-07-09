package mads.group3.stagecheck.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath.documentId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mads.group3.stagecheck.common.repositories.FavouritesRepository
import mads.group3.stagecheck.models.Event

class FavouritesViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val favouritesRepo: FavouritesRepository = FavouritesRepository()
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadFavourites() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val favouriteIds = favouritesRepo.getFavouriteEventIds()
                if (favouriteIds.isEmpty()) {
                    _events.value = emptyList()
                } else {
                    val eventsList = mutableListOf<Event>()
                    favouriteIds.chunked(30).forEach { chunk ->
                        val snapshot = firestore.collection("events")
                            .whereIn(documentId(), chunk)
                            .get()
                            .await()
                        eventsList.addAll(snapshot.toObjects(Event::class.java))
                    }
                    val orderedEvents = favouriteIds.mapNotNull { id ->
                        eventsList.find { it.docId == id }
                    }
                    _events.value = orderedEvents
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() = loadFavourites()
}