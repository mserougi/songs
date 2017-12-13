package tooploox.com.song

import java.util.*

/**
 * Created by mohammed on 12/13/17.
 */

data class Song (
    val trackName: String?,
    val artistName: String?,
    val releaseDate: Date?
)

data class Songs (
    val results: List<Song>
)
