package com.tooploox.song

import android.content.Context

/**
 * Created by mohammed on 12/13/17.
 */
class Util
{
    companion object
    {
        fun loadJsonFromAsset(context: Context, filename: String): String {
            // Loads any file from the assets folder
            val stream = context.assets.open(filename)
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            return String(buffer, charset("UTF-8"))
        }
    }
}