package com.group20.codevocab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.group20.codevocab.data.local.dao.ModuleDao
import com.group20.codevocab.data.local.dao.WordDao
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.local.entity.WordEntity

@Database(entities = [WordEntity::class, ModuleEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun moduleDao(): ModuleDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vocab.db"
                )
                    .createFromAsset("databases/vocab.db") // preload data
                    .build()
                INSTANCE = instance
                instance
            }
    }
}
