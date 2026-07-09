package mads.group3.stagecheck.common.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavouritesRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val currentUserId: String?
        get() = auth.currentUser?.uid

    private fun favouritesCollection(userId: String) = firestore
        .collection("users")
        .document(userId)
        .collection("favourites")

    fun addFavourite(eventId: String) {
        val userId = currentUserId ?: throw IllegalStateException("User not logged in")
        favouritesCollection(userId).document(eventId).set(mapOf("eventId" to eventId))
    }

    fun removeFavourite(eventId: String) {
        val userId = currentUserId ?: throw IllegalStateException("User not logged in")
        favouritesCollection(userId).document(eventId).delete()
    }

    suspend fun isFavourite(eventId: String): Boolean {
        val userId = currentUserId ?: return false
        val snapshot = favouritesCollection(userId).document(eventId).get().await()
        return snapshot.exists()
    }

    suspend fun getFavouriteEventIds(): List<String> {
        val userId = currentUserId ?: return emptyList()
        val snapshot = favouritesCollection(userId).get().await()
        return snapshot.documents.mapNotNull { it.id }
    }
}