package mads.group3.stagecheck.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mads.group3.stagecheck.models.Event

class EventListViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        startRealtimeListener()
    }

    private fun startRealtimeListener() {
        _isLoading.value = true
        firestore.collection("events")
            .orderBy("localDate")
            .limit(25)
            .addSnapshotListener { snapshot: QuerySnapshot?, error: Exception? ->
                _isLoading.value = false

                if (error != null) {
                    _error.value = error.message
                    return@addSnapshotListener
                }

                snapshot?.let {
                    val eventList = it.toObjects(Event::class.java)
                    _events.value = eventList
                    _error.value = null
                } ?: run {
                    _events.value = emptyList()
                }
            }
    }

    fun refresh() {
        _isLoading.value = true
        firestore.collection("events")
            .orderBy("localDate")
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                _events.value = snapshot.toObjects(Event::class.java)
                _isLoading.value = false
                _error.value = null
            }
            .addOnFailureListener { e ->
                _error.value = e.message
                _isLoading.value = false
            }
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
        listenerRegistration = null
    }
}