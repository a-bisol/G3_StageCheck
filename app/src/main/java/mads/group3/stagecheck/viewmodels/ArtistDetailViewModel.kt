package mads.group3.stagecheck.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mads.group3.stagecheck.models.Artist

class ArtistDetailViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null

    fun loadArtist(artistId: String) {
        _isLoading.value = true
        listenerRegistration?.remove()
        listenerRegistration = firestore.collection("artists")
            .document(artistId)
            .addSnapshotListener { snapshot, error ->
                _isLoading.value = false
                if (error != null) {
                    _error.value = error.message
                    return@addSnapshotListener
                }
                val artist = snapshot?.toObject(Artist::class.java)
                _artist.value = artist
                _error.value = if (artist == null && snapshot != null) {
                    "Artist $artistId not found"
                } else {
                    null
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}