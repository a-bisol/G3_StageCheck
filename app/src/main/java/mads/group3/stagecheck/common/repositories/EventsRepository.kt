package mads.group3.stagecheck.common.repositories

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import mads.group3.stagecheck.models.Event

class EventsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getEventsByVenueId(venueId: String): List<Event> {
        return try {
            val snapshot = firestore.collection("events")
                .whereEqualTo("venueDirectoryId", venueId)
                .get()
                .await()
            snapshot.toObjects(Event::class.java)
        } catch (_: Exception) {
            emptyList()
        }
    }
}