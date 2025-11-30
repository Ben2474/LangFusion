package edu.farmingdale.langfusion.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, UserProgress::class],
    version = 1,
    exportSchema = false
)

abstract class LangFusionDatabase : RoomDatabase(){
    abstract fun userDao(): UserDao
    abstract fun userProgressDao(): UserProgressDao

    companion object {
        @Volatile
        private var INSTANCE: LangFusionDatabase? = null

        fun getInstance(context: Context): LangFusionDatabase {
            return INSTANCE ?: synchronized(this){
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    LangFusionDatabase::class.java,
                    "langfusion_db"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}