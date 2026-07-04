package mads.group3.stagecheck.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Artist(
    @DocumentId
    val id: String = "",

    @PropertyName("name")
    val name: String? = null,

    @PropertyName("image3x2")
    val image3x2: String? = null,

    @PropertyName("image16x9")
    val image16x9: String? = null,

    @PropertyName("lastSyncedAt")
    val lastSyncedAt: Timestamp? = null,

    @PropertyName("bio")
    val bio: String? = null
)
