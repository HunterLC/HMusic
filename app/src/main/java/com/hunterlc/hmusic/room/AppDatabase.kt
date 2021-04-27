package com.hunterlc.hmusic.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = AppDatabase.DATABASE_VERSION,
    entities = [PlayQueueData::class],
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun playQueueDao(): PlayQueueDao

    companion object {

        // 数据库版本
        const val DATABASE_VERSION = 1

        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "hmusic_db"
            ).build().apply {
                    instance = this
                }
        }
    }

}