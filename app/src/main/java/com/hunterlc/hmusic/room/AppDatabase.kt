package com.hunterlc.hmusic.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    version = AppDatabase.DATABASE_VERSION,
    entities = [PlayQueueData::class],
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun playQueueDao(): PlayQueueDao

    companion object {

        // 数据库版本
        const val DATABASE_VERSION = 2

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table PlayQueueData add column reason TEXT")
            }
        }

        private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "hmusic_db"
            ).addMigrations(
                MIGRATION_1_2
            ).build().apply {
                    instance = this
                }
        }
    }

}