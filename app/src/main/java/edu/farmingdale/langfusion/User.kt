package edu.farmingdale.langfusion.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,
    val email: String,
    val passwordHash: String
)