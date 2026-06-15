package mads.group3.stagecheck.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mads.group3.stagecheck.common.repositories.FavouritesRepository
import mads.group3.stagecheck.models.Artist
import mads.group3.stagecheck.models.Event

class EventDetailViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val favouritesRepo: FavouritesRepository = FavouritesRepository()
) : ViewModel() {
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isFavourited = MutableStateFlow(false)
    val isFavourited: StateFlow<Boolean> = _isFavourited.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private var artistsListener: ListenerRegistration? = null

    fun loadEvent(eventId: String) {
        _isLoading.value = true
        listenerRegistration?.remove()
        listenerRegistration = firestore.collection("events")
            .document(eventId)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false
                if (error != null) {
                    _error.value = error.message
                    return@addSnapshotListener
                }
                val event = snapshot?.toObject(Event::class.java)
                _event.value = event
                _error.value = if (event == null && snapshot != null) {
                    "Event $eventId not found"
                } else {
                    null
                }

                event?.artistIds?.let { artistIds ->
                    if (artistIds.isNotEmpty()) {
                        loadArtists(artistIds)
                    } else {
                        _artists.value = emptyList()
                    }
                }
            }
        viewModelScope.launch {
            _isFavourited.value = favouritesRepo.isFavourite(eventId)
        }
    }

    private fun loadArtists(artistIds: List<String>) {
        artistsListener?.remove()
        firestore.collection("artists")
            .whereIn(FieldPath.documentId(), artistIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val artistList = snapshot.toObjects(Artist::class.java)
                val orderedArtists = artistIds.mapNotNull { id ->
                    artistList.find { it.id == id }
                }
                _artists.value = orderedArtists
            }
            .addOnFailureListener {
                _artists.value = emptyList()
            }
    }

    fun toggleFavourite(eventId: String) {
        viewModelScope.launch {
            try {
                if (_isFavourited.value) {
                    favouritesRepo.removeFavourite(eventId)
                    _isFavourited.value = false
                } else {
                    favouritesRepo.addFavourite(eventId)
                    _isFavourited.value = true
                }
            } catch (e: Exception) {
                _error.value = "Failed to updated favourites: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        artistsListener?.remove()
    }
}