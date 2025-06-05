package hr.pet.handler

import android.content.Context
import android.util.Log

import hr.pet.factory.createGetHttpUrlConnection
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.util.UUID
import kotlin.text.substring

fun downloadImage(context: Context, url: String) : String? {
    if (url.isBlank()) {
        return null
    }
    val urlObject = URL(url)
    val path = urlObject.path
    val lastDot = path.lastIndexOf('.')
    val extension = if (lastDot > 0 && lastDot < path.length - 1) {
        path.substring(lastDot)
    } else {
        ".jpg" // Default
    }
    val uniqueID = UUID.randomUUID().toString()
    val filename ="${uniqueID}${extension}"
    val file: File = createFile(context, filename)
    try {
        val con: HttpURLConnection = createGetHttpUrlConnection(url)
        Files.copy(con.inputStream, file.toPath())
        return file.absolutePath
    } catch (e: Exception) {
        Log.e("IMAGES_HANDLER", e.message, e)
    }
    return null
}

fun createFile(context: Context, filename: String) : File {
    val dir= context.applicationContext.getExternalFilesDir(null)
    val file = File(dir, filename)
    if(file.exists()) file.delete()
    return file
}