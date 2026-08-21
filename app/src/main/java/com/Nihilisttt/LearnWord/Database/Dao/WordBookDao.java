package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.WordBookEntity;

import java.util.List;

@Dao
public interface WordBookDao {
    @Insert
    void insertWordBook(WordBookEntity book);

    @Insert
    void insertWordBooks(List<WordBookEntity> books);

    @Update
    void updateWordBook(WordBookEntity book);

    @Query("DELETE FROM WordBook WHERE bookId = :bookId")
    void deleteWordBook(String bookId);

    @Query("SELECT * FROM WordBook ORDER BY sortOrder ASC")
    LiveData<List<WordBookEntity>> getAllWordBooks();

    @Query("SELECT * FROM WordBook ORDER BY sortOrder ASC")
    List<WordBookEntity> getAllWordBooksSync();

    @Query("SELECT * FROM WordBook WHERE bookId = :bookId")
    WordBookEntity getWordBookByIdSync(String bookId);

    @Query("SELECT * FROM WordBook WHERE jlptLevel = :level LIMIT 1")
    WordBookEntity getWordBookByJlptLevelSync(int level);

    @Query("SELECT COUNT(*) FROM WordBook")
    int getWordBookCountSync();

    @Query("UPDATE WordBook SET learnedCount = :learned, masteredCount = :mastered, updatedAt = :time WHERE bookId = :bookId")
    void updateBookProgress(String bookId, int learned, int mastered, long time);

    @Query("UPDATE WordBook SET dailyNewTarget = :newTarget, dailyReviewTarget = :reviewTarget, updatedAt = :time WHERE bookId = :bookId")
    void updateBookDailyTargets(String bookId, int newTarget, int reviewTarget, long time);
}