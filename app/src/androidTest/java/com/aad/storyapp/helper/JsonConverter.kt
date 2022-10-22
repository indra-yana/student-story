package com.aad.storyapp.helper

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import java.io.IOException
import java.io.InputStreamReader

/****************************************************
 * Created by Indra Muliana
 * On Friday, 07/10/2022 20.55
 * Email: indra.ndra26@gmail.com
 * Github: https://github.com/indra-yana
 ****************************************************/

object JsonConverter {
    fun readStringFromFile(fileName: String): String {
        try {
            val applicationContext = ApplicationProvider.getApplicationContext<Context>()
            val inputStream = applicationContext.assets.open(fileName)
            val builder = StringBuilder()
            val reader = InputStreamReader(inputStream, "UTF-8")
            reader.readLines().forEach {
                builder.append(it)
            }
            return builder.toString()
        } catch (e: IOException) {
            throw e
        }
    }
}