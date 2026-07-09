package mads.group3.stagecheck.common.network

data class SearchRequest(
    val city: String?,
    val radius: Float?,
    val unit: String?,
    val startDate: String?,
    val endDate: String?,
    val limit: Int = 20,
    val classificationName: String = "Music",
    val artist: String?
)
