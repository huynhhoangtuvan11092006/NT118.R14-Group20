package com.example.ielts.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ielts.data.local.dao.VocabularyDao
import com.example.ielts.data.local.entity.VocabularyEntity

@Database(entities = [VocabularyEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Cung cấp DAO để truy vấn từ vựng
    abstract fun vocabularyDao(): VocabularyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Mẫu thiết kế Singleton: Chỉ tạo 1 kết nối Database duy nhất trong toàn app
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ielts_master_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
