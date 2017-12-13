package tooploox.com.song

import android.content.Context
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import java.text.ParseException
import java.util.*

/**
 * Created by mohammed on 12/13/17.
 */
class SongListAdapter(context: Context, objects: List<Song>) : ArrayAdapter<Song>(context, android.R.layout.simple_list_item_2, android.R.id.text1, objects) {

    private val songs = objects

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = super.getView(position, convertView, parent)

        val text1 = view.findViewById<View>(android.R.id.text1) as TextView
        val text2 = view.findViewById<View>(android.R.id.text2) as TextView

        val song = songs[position]

        text1.text = song.trackName

        if (song.releaseDate != null) {
            try {
                val cal = Calendar.getInstance()
                cal.time = song.releaseDate
                val year = cal.get(Calendar.YEAR)
                text2.text = String.format("%s - %d", song.artistName, year)
            } catch (e: ParseException) {
                text2.text = song.artistName
            }
        }
        else {
            text2.text = song.artistName
        }

        return view
    }
}
