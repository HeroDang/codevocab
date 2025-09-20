package com.group20.codevocab.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.group20.codevocab.model.Word

@Database(entities = [Word::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
