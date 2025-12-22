package com.group20.codevocab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.group20.codevocab.data.local.dao.FlashcardProgressDao
import com.group20.codevocab.data.local.dao.ModuleDao
import com.group20.codevocab.data.local.dao.QuizResultDao
import com.group20.codevocab.data.local.dao.WordDao
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.local.entity.QuizResultEntity
import com.group20.codevocab.data.local.entity.WordEntity

@Database(entities = [
    WordEntity::class,
    ModuleEntity::class,
    FlashcardProgressEntity::class,
    QuizResultEntity::class],
    version = 11, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun moduleDao(): ModuleDao
    abstract fun flashcardDao(): FlashcardProgressDao
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                // Code First: Room sẽ tự tạo file db và bảng dựa trên Entity
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "codevocab.db" // Đặt tên mới để không dính dáng đến vocab.db cũ
                )
                    .fallbackToDestructiveMigration() // Nếu sửa Entity, nó sẽ xóa db cũ tạo lại mới
                    .build()

                INSTANCE = instance
                instance
            }
    }
}