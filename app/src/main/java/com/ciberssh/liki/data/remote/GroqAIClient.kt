package com.ciberssh.liki.data.remote

import com.ciberssh.liki.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GroqAIClient {
    private val apiKey = BuildConfig.GROQ_API_KEY
    private val baseUrl = "https://api.groq.com/openai/v1/chat/completions"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun sendMessage(userMessage: String, imageBase64: String? = null): String {
        return try {
            val messages = JSONArray()

            val userContent = if (imageBase64 != null) {
                JSONArray().apply {
                    put(JSONObject().apply {
                        put("type", "text")
                        put("text", userMessage)
                    })
                    put(JSONObject().apply {
                        put("type", "image_url")
                        put("image_url", JSONObject().apply {
                            put("url", "data:image/jpeg;base64,$imageBase64")
                        })
                    })
                }
            } else {
                userMessage
            }

            messages.put(JSONObject().apply {
                put("role", "user")
                put("content", userContent)
            })

            val requestBody = JSONObject().apply {
                put("model", "llama-3.2-90b-vision-preview")
                put("messages", messages)
                put("temperature", 0.7)
                put("max_tokens", 2048)
            }

            val request = Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonResponse = JSONObject(responseBody)
                jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
            } else {
                "Ошибка: ${response.code} - $responseBody"
            }
        } catch (e: Exception) {
            "Ошибка: ${e.message}"
        }
    }
}
