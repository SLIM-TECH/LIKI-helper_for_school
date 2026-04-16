package com.ciberssh.liki.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object UpdateChecker {
    private const val GITHUB_API_URL = "https://api.github.com/repos/SLIM-TECH/LIKI-helper_for_school/releases/latest"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    suspend fun checkForUpdates(currentVersion: String): UpdateInfo? {
        return try {
            val request = Request.Builder()
                .url(GITHUB_API_URL)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val latestVersion = json.getString("tag_name").removePrefix("v")
                val downloadUrl = json.getString("html_url")

                if (isNewerVersion(currentVersion, latestVersion)) {
                    UpdateInfo(latestVersion, downloadUrl)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isNewerVersion(current: String, latest: String): Boolean {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }

        for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
            val currentPart = currentParts.getOrNull(i) ?: 0
            val latestPart = latestParts.getOrNull(i) ?: 0

            if (latestPart > currentPart) return true
            if (latestPart < currentPart) return false
        }

        return false
    }
}

data class UpdateInfo(
    val version: String,
    val downloadUrl: String
)
