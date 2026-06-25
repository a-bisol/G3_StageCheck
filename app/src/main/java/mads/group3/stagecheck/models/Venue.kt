package mads.group3.stagecheck.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class Venue(
    @DocumentId
    val id: String = "",

    @PropertyName("name")
    val name: String? = null,

    @PropertyName("location")
    val location: GeoPoint? = null
)
