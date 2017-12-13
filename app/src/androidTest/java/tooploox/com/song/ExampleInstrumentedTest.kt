package tooploox.com.song

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.tooploox.song.Util
import org.json.JSONArray

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("tooploox.com.song", appContext.packageName)
    }

    @Test
    fun localJsonValid() {

        val json = Util.loadJsonFromAsset(InstrumentationRegistry.getTargetContext(), "songs-list.json")
        val songs = JSONArray(json)
        assertEquals(2229, songs.length())
    }
}
