package com.ciberssh.liki.data.repository

import com.ciberssh.liki.data.models.Homework
import com.ciberssh.liki.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeworkRepository {
    private val supabase = SupabaseClient.client

    suspend fun getHomework(): List<Homework> = withContext(Dispatchers.IO) {
        try {
            supabase.from("homework")
                .select()
                .decodeList<Homework>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addHomework(homework: Homework) = withContext(Dispatchers.IO) {
        try {
            supabase.from("homework").insert(homework)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateHomework(homework: Homework) = withContext(Dispatchers.IO) {
        try {
            supabase.from("homework")
                .update(homework) {
                    filter {
                        eq("id", homework.id)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteHomework(id: String) = withContext(Dispatchers.IO) {
        try {
            supabase.from("homework").delete {
                filter {
                    eq("id", id)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
