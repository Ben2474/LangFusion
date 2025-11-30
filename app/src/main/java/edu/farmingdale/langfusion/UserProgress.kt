package edu.farmingdale.langfusion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val language: String,
    val lessonId: String,
    val completed: Boolean,
    val lastScore: Int
)