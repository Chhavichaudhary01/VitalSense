package com.vitalsense.app.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AppUpdateInfo(
    val isUpdateAvailable: Boolean,
    val latestVersionName: String,
    val releaseNotes: String,
    val downloadUrl: String
)

object AppUpdateChecker {

    const val CURRENT_VERSION_CODE = 1
    const val CURRENT_VERSION_NAME = "1.0.0"
    const val GITHUB_REPO = "alexansh/VitalSense"
    const val DIRECT_APK_DOWNLOAD_URL = "https://github.com/alexansh/VitalSense/releases/download/version/VitalSense.apk"

    /**
     * Checks GitHub Releases API in the background for new version releases
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
                val jsonArray = org.json.JSONArray(responseText)
                if (jsonArray.length() > 0) {
                    val latestRelease = jsonArray.getJSONObject(0)
                    val tagName = latestRelease.optString("tag_name", "1.0.0")
                    val releaseTitle = latestRelease.optString("name", "New Update")
                    val body = latestRelease.optString("body", "Bug fixes and improvements")

                    // If tag or release title indicates newer version than 1.0.0
                    val isNewer = tagName.contains("1.0.1") || tagName.contains("1.1") || releaseTitle.contains("v1.0.1") || releaseTitle.contains("Interactive Map")

                    val assets = latestRelease.optJSONArray("assets")
                    var apkUrl = DIRECT_APK_DOWNLOAD_URL
                    if (assets != null && assets.length() > 0) {
                        apkUrl = assets.getJSONObject(0).optString("browser_download_url", DIRECT_APK_DOWNLOAD_URL)
                    }

                    return@withContext AppUpdateInfo(
                        isUpdateAvailable = isNewer,
                        latestVersionName = if (tagName.isNotBlank()) tagName else "1.0.1",
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
            downloadUrl = DIRECT_APK_DOWNLOAD_URL
        )
    }

    /**
     * Launches browser to download the updated APK directly
     */
    fun openDownloadLink(context: Context, url: String = DIRECT_APK_DOWNLOAD_URL) {
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
