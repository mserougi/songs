package tooploox.com.song

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.tooploox.song.ApiService
import com.tooploox.song.Util
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException

class MainActivity : AppCompatActivity()
{
    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
            Manifest.permission.INTERNET)

    private val localSongs = ArrayList<Song>()
    private val remoteSongs = ArrayList<Song>()
    private val songsList = ArrayList<Song>()

    private val songsListViewAdapter by lazy {
        SongListAdapter(this, songsList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()
    }

    /**
     * Checks the dynamically controlled permissions and requests missing permissions from end user.
     */
    private fun checkPermissions()
    {
        val missingPermissions = ArrayList<String>()

        // Check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS)
        {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }

        if (!missingPermissions.isEmpty())
        {
            // Request all missing permissions
            val permissions = missingPermissions.toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        }
        else
        {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS, grantResults)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        when (requestCode)
        {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                for (index in permissions.indices.reversed())
                {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED)
                    {
                        // Exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index] + "' not granted, exiting", Toast.LENGTH_LONG).show()
                        finish()
                        return
                    }
                }
                // All permissions were granted
                initialize()
            }
        }
    }

    private fun initialize()
    {
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        songsListView.adapter = songsListViewAdapter

        loadLocalSongs()
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                loadLocalSongs()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {

                loadRemoteSongsByArtist("Taylor")

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                loadLocalAndRemote()

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun loadRemoteSongsByArtist(artist: String)
    {
        Toast.makeText(this, R.string.load_remote_songs, Toast.LENGTH_SHORT).show()

        val api = ApiService.create()
        api.getSongs(artist).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe({ result ->

            remoteSongs.clear()
            for (song in result.results) {
                remoteSongs.add(song)
            }

            songsList.clear()
            songsList.addAll(remoteSongs)

            songsListViewAdapter.notifyDataSetChanged()

        }, { error ->
            error.printStackTrace()

            Toast.makeText(this, R.string.error_load_remote_songs, Toast.LENGTH_SHORT).show()
        })
    }

    private fun loadLocalSongs()
    {
        LoadLocalJsonTask().execute("songs-list.json")
    }

    private inner class LoadLocalJsonTask : AsyncTask<String, Unit, Unit>() {
        // Start a background task to load the JSON file and parse it
        override fun doInBackground(vararg params: String) {

            localSongs.clear()

            val songs = JSONArray(Util.loadJsonFromAsset(applicationContext, params[0]))
            try {
                for (i in 0 until songs.length()) {
                    val s = songs[i] as JSONObject

                    var date: Date? = null
                    val dtStart = s.getString("Release Year")
                    if (!dtStart.isNullOrBlank()) {
                        val format = SimpleDateFormat("yyyy")
                        try {
                            date = format.parse(dtStart)
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    }

                    // Parse JSON
                    val song = Song(s.getString("Song Clean"), s.getString("ARTIST CLEAN"), date)
                    localSongs.add(song)
                }
            }
            catch (e: JSONException) {
                e.printStackTrace()
            }

            return Unit
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            songsList.clear()
            songsList.addAll(localSongs)

            songsListViewAdapter.notifyDataSetChanged()
        }
    }

    private fun loadLocalAndRemote()
    {
        songsList.clear()
        songsList.addAll(localSongs)
        songsList.addAll(remoteSongs)

        songsListViewAdapter.notifyDataSetChanged()
    }
}
