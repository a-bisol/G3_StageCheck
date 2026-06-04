package mads.group3.stagecheck.navigation

sealed class Screens(val route: String) {
    object Login : Screens("login")

    sealed class Main(val subRoute: String) : Screens("main/$subRoute") {
        object Dash : Main("dash")
        object Search : Main("search")
        object Map : Main("map")
        object Favourites : Main("favourites")
        object Profile : Main("profile")
    }

    object DetailEvent : Screens("event/{eventId}") {
        fun passEventId(eventId: String) = "event/$eventId"
    }

    object DetailArtist : Screens("artist/{artistId}") {
        fun passArtistId(artistId: String) = "artist/$artistId"
    }
}