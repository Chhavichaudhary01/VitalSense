package com.vitalsense.app.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.vitalsense.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val isUpdateAvailable: Boolean,
    val latestVersionName: String,
    val releaseNotes: String,
    val downloadUrl: String
)

object AppUpdateChecker {

    val CURRENT_VERSION_NAME: String = BuildConfig.VERSION_NAME
    const val GITHUB_REPO = "alexansh/VitalSense"
    const val DEFAULT_DOWNLOAD_URL = "https://github.com/alexansh/VitalSense/releases/download/version/VitalSense.1.0.1.apk"

    /**
     * Checks GitHub Releases API in the background for new version releases.
     * Only returns isUpdateAvailable = true if the remote version is strictly greater
     * than the currently installed app version.
     */
    suspend fun checkForUpdates(): AppUpdateInfo = withContext(Dispatchers.IO) {
        try {
            val apiUrl = "https://api.github.com/repos/$GITHUB_REPO/releases"
            val url = URL(apiUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 4000
                readTimeout = 4000
                setRequestProperty("User-Agent", "VitalSense-Android")
            }

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonArray = JSONArray(responseText)
                if (jsonArray.length() > 0) {
                    val latestRelease = jsonArray.getJSONObject(0)
                    val tagName = latestRelease.optString("tag_name", "")
                    val releaseTitle = latestRelease.optString("name", "")
                    val body = latestRelease.optString("body", "Bug fixes and improvements")

                    val remoteVersion = extractVersionString(tagName, releaseTitle)

                    // Check if remote version is strictly greater than current installed version
                    val isNewer = isVersionGreater(remoteVersion, CURRENT_VERSION_NAME)

                    val assets = latestRelease.optJSONArray("assets")
                    var apkUrl = DEFAULT_DOWNLOAD_URL
                    if (assets != null && assets.length() > 0) {
                        apkUrl = assets.getJSONObject(0).optString("browser_download_url", DEFAULT_DOWNLOAD_URL)
                    }

                    return@withContext AppUpdateInfo(
                        isUpdateAvailable = isNewer,
                        latestVersionName = if (remoteVersion.isNotBlank()) remoteVersion else CURRENT_VERSION_NAME,
                        releaseNotes = body,
                        downloadUrl = apkUrl
                    )
                }
            }
        } catch (e: Exception) {
            // Silently ignore network failures when offline
        }

        return@withContext AppUpdateInfo(
            isUpdateAvailable = false,
            latestVersionName = CURRENT_VERSION_NAME,
            releaseNotes = "",
            downloadUrl = DEFAULT_DOWNLOAD_URL
        )
    }

    /**
     * Extracts clean semantic version (e.g. "1.0.1") from tag names like "v1.0.1", "version-1.0.1", etc.
     */
    fun extractVersionString(tag: String, name: String): String {
        val regex = Regex("""\b\d+\.\d+(\.\d+)?\b""")
        val tagMatch = regex.find(tag)?.value
        if (!tagMatch.isNullOrBlank()) return tagMatch

        val nameMatch = regex.find(name)?.value
        if (!nameMatch.isNullOrBlank()) return nameMatch

        return tag.trim()
    }

    /**
     * Compares two semantic version strings (e.g. "1.0.1" vs "1.0.0").
     * Returns true ONLY if remote is strictly greater than current.
     */
    fun isVersionGreater(remote: String, current: String): Boolean {
        val remoteNumbers = remote.split(".").mapNotNull { it.replace(Regex("[^0-9]"), "").toIntOrNull() }
        val currentNumbers = current.split(".").mapNotNull { it.replace(Regex("[^0-9]"), "").toIntOrNull() }

        if (remoteNumbers.isEmpty() || currentNumbers.isEmpty()) return false

        val maxLen = maxOf(remoteNumbers.size, currentNumbers.size)
        for (i in 0 until maxLen) {
            val r = remoteNumbers.getOrElse(i) { 0 }
            val c = currentNumbers.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    /**
     * Launches browser to download the updated APK directly
     */
    fun openDownloadLink(context: Context, url: String = DEFAULT_DOWNLOAD_URL) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback
        }
    }
}
