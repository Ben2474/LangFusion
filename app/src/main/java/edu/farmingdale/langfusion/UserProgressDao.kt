package edu.farmingdale.langfusion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: UserProgress): Long

    @Update
    suspend fun updateProgress(progress: UserProgress)

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getProgressForUser(userId: Long): List<UserProgress>

    @Query("SELECT * FROM user_progress WHERE userId = :userId and lessonId = :lessonId LIMIT 1")
    suspend fun getProgressForLesson(userId: Long, lessonId: String): UserProgress?
}