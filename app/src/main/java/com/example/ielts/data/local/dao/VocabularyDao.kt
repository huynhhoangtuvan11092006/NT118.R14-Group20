package com.example.ielts.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ielts.data.local.entity.VocabularyEntity

@Dao
interface VocabularyDao {

    // Thêm hoặc cập nhật một từ vựng
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(vocab: VocabularyEntity)

    // Thêm danh sách nhiều từ vựng cùng lúc
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vocabList: List<VocabularyEntity>)

    // Lấy toàn bộ từ vựng đã lưu trong máy
    @Query("SELECT * FROM vocabularies ORDER BY nextReviewDate ASC")
    suspend fun getAllVocabularies(): List<VocabularyEntity>

    // Lấy danh sách từ đến hạn cần ôn tập hôm nay theo thuật toán SRS
    @Query("SELECT * FROM vocabularies WHERE nextReviewDate <= :currentTimestamp")
    suspend fun getVocabulariesDueForReview(currentTimestamp: Long): List<VocabularyEntity>

    // Xóa từ vựng
    @Delete
    suspend fun deleteVocabulary(vocab: VocabularyEntity)
}
