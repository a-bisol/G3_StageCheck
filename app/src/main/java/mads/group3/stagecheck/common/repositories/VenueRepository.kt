package mads.group3.stagecheck.common.repositories

import com.google.firebase.firestore.FirebaseFirestore
import mads.group3.stagecheck.models.Venue
import kotlinx.coroutines.tasks.await
import android.util.Log

class VenueRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun getVenues(): List<Venue> {
        return try {
            val snapshot = firestore.collection("venues").get().await()
            snapshot.documents.mapNotNull { doc ->
                val name = doc.getString("name") ?: return@mapNotNull null
                val geoPoint = doc.getGeoPoint("location") ?: return@mapNotNull null
                Venue(
                    id = doc.id,
                    name = name,
                    location = geoPoint
                )
            }
        } catch (e: Exception) {
            e.message?.let {
                Log.e("VenueRepository - getVenues", it)
            }
            emptyList()
        }
    }
}