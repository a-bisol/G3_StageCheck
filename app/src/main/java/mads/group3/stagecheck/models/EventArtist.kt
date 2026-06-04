package mads.group3.stagecheck.models

import com.google.firebase.firestore.PropertyName

data class EventArtist(
    @PropertyName("id")
    val id: String,

    @PropertyName("name")
    val name: String,

    @PropertyName("image3x2")
    val image3x2: String? = null,

    @PropertyName("image16x9")
    val image16x9: String? = null
)