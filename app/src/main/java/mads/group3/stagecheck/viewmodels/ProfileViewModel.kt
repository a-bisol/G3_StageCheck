package mads.group3.stagecheck.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath.documentId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mads.group3.stagecheck.common.repositories.FollowingRepository
import mads.group3.stagecheck.models.Artist

class ProfileViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val followingRepo: FollowingRepository = FollowingRepository()
) : ViewModel() {

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadFollowedArtists() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val artistIds = followingRepo.getFollowedArtistIds()
                if (artistIds.isEmpty()) {
                    _artists.value = emptyList()
                } else {
                    val artistList = mutableListOf<Artist>()
                    artistIds.chunked(30).forEach { chunk ->
                        val snapshot = firestore.collection("artists")
                            .whereIn(documentId(), chunk)
                            .get()
                            .await()
                        artistList.addAll(snapshot.toObjects(Artist::class.java))
                    }
                    val sortedArtists = artistList
                        .filter { it.name != null }
                        .sortedBy { it.name?.lowercase() }
                    _artists.value = sortedArtists
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("ProfileVM - loadFollowed", it) }
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() = loadFollowedArtists()

}