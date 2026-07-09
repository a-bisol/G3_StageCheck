package mads.group3.stagecheck.common.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FollowingRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val currentUserId: String?
        get() = auth.currentUser?.uid

    private fun followingCollection(userId: String) = firestore
        .collection("users")
        .document(userId)
        .collection("following")

    suspend fun addFollow(artistId: String) {
        val userId = currentUserId ?: throw IllegalStateException("User not logged in")
        followingCollection(userId).document(artistId).set(mapOf("artistId" to artistId))
        firestore.collection("artists")
            .document(artistId)
            .update("following", FieldValue.arrayUnion(userId))
            .await()
    }

    suspend fun removeFollow(artistId: String) {
        val userId = currentUserId ?: throw IllegalStateException("User not logged in")
        followingCollection(userId).document(artistId).delete()
        firestore.collection("artists")
            .document(artistId)
            .update("following", FieldValue.arrayRemove(userId))
            .await()
    }

    suspend fun isFollowing(artistId: String): Boolean {
        val userId = currentUserId ?: return false
        val snapshot = followingCollection(userId).document(artistId).get().await()
        return snapshot.exists()
    }

    suspend fun getFollowedArtistIds(): List<String> {
        val userId = currentUserId ?: return emptyList()
        val snapshot = followingCollection(userId).get().await()
        return snapshot.documents.mapNotNull { it.id }
    }
}