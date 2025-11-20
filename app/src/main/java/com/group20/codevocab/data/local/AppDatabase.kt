package com.group20.codevocab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.group20.codevocab.data.local.dao.FlashcardProgressDao
import com.group20.codevocab.data.local.dao.ModuleDao
import com.group20.codevocab.data.local.dao.QuizResultDao
import com.group20.codevocab.data.local.dao.VocabDao
import com.group20.codevocab.data.local.dao.WordDao
import com.group20.codevocab.data.local.entity.FlashcardProgressEntity
import com.group20.codevocab.data.local.entity.ModuleEntity
import com.group20.codevocab.data.local.entity.QuizResultEntity
import com.group20.codevocab.data.local.entity.VocabularyEntity
import com.group20.codevocab.data.local.entity.WordEntity

@Database(entities = [
    WordEntity::class,
    ModuleEntity::class,
    VocabularyEntity::class,
    FlashcardProgressEntity::class,
    QuizResultEntity::class],
    version = 7, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun moduleDao(): ModuleDao
    abstract fun vocabDao(): VocabDao
    abstract fun flashcardDao(): FlashcardProgressDao
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                val dbFile = context.getDatabasePath("vocab.db")

                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vocab.db"
                )

//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    AppDatabase::class.java,
//                    "vocab.db"
//                )
//                    .createFromAsset("databases/vocab.db") // preload data
//                    .fallbackToDestructiveMigration()
//                    .build()

                // ✅ Chỉ tạo từ asset nếu DB chưa tồn tại
                if (!dbFile.exists()) {
                    builder.createFromAsset("databases/vocab.db")
                }

                val instance = builder
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
    }
}
