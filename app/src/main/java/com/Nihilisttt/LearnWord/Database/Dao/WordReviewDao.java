package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.WordReviewEntity;

import java.util.List;

@Dao
public interface WordReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReview(WordReviewEntity review);

    @Update
    void updateReview(WordReviewEntity review);

    @Query("SELECT * FROM WordReview WHERE wordId = :wordId AND bookId = :bookId")
    WordReviewEntity getReviewSync(String wordId, String bookId);

    @Query("SELECT * FROM WordReview WHERE wordId = :wordId AND status != -1 LIMIT 1")
    WordReviewEntity getReviewByWordIdSync(String wordId);

    @Query("SELECT * FROM WordReview WHERE bookId = :bookId AND status = 0 ORDER BY createTime ASC LIMIT :limit")
    List<WordReviewEntity> getStudyingWordsSync(String bookId, int limit);

    @Query("SELECT * FROM WordReview WHERE bookId = :bookId AND status = 1 AND nextReviewTime <= :now AND nextReviewTime > 0 AND updateTime < :dayStart ORDER BY nextReviewTime ASC LIMIT :limit")
    List<WordReviewEntity> getDueReviewsSync(String bookId, long now, long dayStart, int limit);

    @Query("SELECT * FROM WordReview WHERE status = 1 AND nextReviewTime <= :now AND nextReviewTime > 0 AND updateTime < :dayStart ORDER BY nextReviewTime ASC LIMIT :limit")
    List<WordReviewEntity> getDueReviewsSyncAllBooks(long now, long dayStart, int limit);

    @Query("SELECT COUNT(*) FROM WordReview WHERE bookId = :bookId AND status = 0")
    int getStudyingCountSync(String bookId);

    @Query("SELECT COUNT(*) FROM WordReview WHERE bookId = :bookId AND status = 1")
    int getLearnedCountSync(String bookId);

    @Query("SELECT COUNT(*) FROM WordReview WHERE bookId = :bookId AND status = -1")
    int getDeletedCountSync(String bookId);

    @Query("SELECT COUNT(*) FROM WordReview WHERE bookId = :bookId AND status = 1 AND studyCycle >= :threshold")
    int getMasteredCountSync(String bookId, int threshold);

    @Query("SELECT COUNT(*) FROM WordReview WHERE bookId = :bookId AND status = 1 AND nextReviewTime <= :now AND nextReviewTime > 0")
    int getDueReviewCountSync(String bookId, long now);

    @Query("SELECT COUNT(*) FROM WordReview WHERE bookId = :bookId AND createTime >= :dayStart AND status != -1")
    int getDailyStudyCountSync(String bookId, long dayStart);

    @Query("SELECT COUNT(*) FROM WordReview WHERE bookId = :bookId AND updateTime >= :dayStart AND status = 1")
    int getDailyPassCountSync(String bookId, long dayStart);

    @Query("SELECT COUNT(*) FROM WordReview WHERE bookId = :bookId AND updateTime >= :dayStart AND studyCycle > 0 AND status != -1")
    int getDailyReviewCountSync(String bookId, long dayStart);

    @Query("UPDATE WordReview SET status = :status, updateTime = :time WHERE wordId = :wordId AND bookId = :bookId")
    void updateStatus(String wordId, String bookId, int status, long time);
}