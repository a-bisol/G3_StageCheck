package mads.group3.stagecheck.models

data class ExtendedSearchOptions(
    val artist: String = "",
    val city: String = "",
    val venue: String = "",
    val distance: Float = 25f,
    val unit: String = "none",
    val startDate: Long? = null,
    val endDate: Long? = null
)