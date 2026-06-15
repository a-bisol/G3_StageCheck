package mads.group3.stagecheck.models

import com.google.firebase.Timestamp

data class User(
    val email: String,
    val createdAt: Timestamp = Timestamp.now()
)