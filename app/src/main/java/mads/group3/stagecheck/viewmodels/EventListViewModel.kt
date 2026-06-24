package mads.group3.stagecheck.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import mads.group3.stagecheck.models.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventListViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val todayDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingExtra = MutableStateFlow(false)
    val isLoadingExtra: StateFlow<Boolean> = _isLoadingExtra.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private var lastDoc: DocumentSnapshot? = null

    init {
        loadInitial()
    }

    fun loadInitial() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _hasMore.value = true
            lastDoc = null

            try {
                val query = firestore.collection("events")
                    .whereGreaterThanOrEqualTo("localDate", todayDateString)
                    .orderBy("localDate")
                    .limit(50)

                val snapshot = query.get().await()
                val eventList = snapshot.toObjects(Event::class.java)
                    .filter { it.status?.lowercase() != "cancelled" }
                _events.value = eventList

                lastDoc = snapshot.documents.lastOrNull()
                _hasMore.value = snapshot.documents.size == 50 && lastDoc != null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMore() {
        if (_isLoadingExtra.value || !_hasMore.value) return

        viewModelScope.launch {
            _isLoadingExtra.value = true

            try {
                var query = firestore.collection("events")
                    .whereGreaterThanOrEqualTo("localDate", todayDateString)
                    .orderBy("localDate")
                    .limit(25)

                if (lastDoc != null) {
                    query = query.startAfter(lastDoc!!)
                }

                val snapshot = query.get().await()
                val newEvents = snapshot.toObjects(Event::class.java)
                    .filter { it.status?.lowercase() != "cancelled" }

                if (newEvents.isNotEmpty()) {
                    _events.value += newEvents
                    lastDoc = snapshot.documents.lastOrNull()
                    _hasMore.value = snapshot.documents.size == 25 && lastDoc != null
                } else {
                    _hasMore.value = false
                }
            } catch (e: Exception) {
                _hasMore.value = false
                e.message?.let { Log.e("EventListVM - loadMore", it) }
            } finally {
                _isLoadingExtra.value = false
            }
        }
    }

    fun refresh() {
        loadInitial()
    }
}