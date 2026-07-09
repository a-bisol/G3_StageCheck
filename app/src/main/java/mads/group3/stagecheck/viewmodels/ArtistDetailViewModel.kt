package mads.group3.stagecheck.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mads.group3.stagecheck.common.repositories.FollowingRepository
import mads.group3.stagecheck.models.Artist

class ArtistDetailViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val followingRepo: FollowingRepository = FollowingRepository()
) : ViewModel() {
    private val _artist = MutableStateFlow<Artist?>(null)
    val artist: StateFlow<Artist?> = _artist.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

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

                if (artist != null && currentUserId != null) {
                    _isFollowing.value = artist.following?.contains(currentUserId) ?: false
                } else {
                    _isFollowing.value = false
                }
            }
    }

    fun toggleFollow(artistId: String) {
        viewModelScope.launch {
            try {
                if (_isFollowing.value) {
                    followingRepo.removeFollow(artistId)
                    _isFollowing.value = false
                } else {
                    followingRepo.addFollow(artistId)
                    _isFollowing.value = true
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("ArtistDetailVM - toggleFollow", it) }
                _error.value = "Failed to update follow: ${e.message}"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}