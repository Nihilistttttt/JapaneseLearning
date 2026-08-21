package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.Nihilisttt.LearnWord.Database.Entities.WordDistractorEntity;

import java.util.List;

@Dao
public interface WordDistractorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDistractors(List<WordDistractorEntity> distractors);

    @Query("SELECT distractorWordId FROM WordDistractor WHERE wordId = :wordId ORDER BY score ASC LIMIT :limit")
    List<String> getDistractorWordIdsSync(String wordId, int limit);

    @Query("SELECT COUNT(*) FROM WordDistractor")
    int getCountSync();

    @Query("DELETE FROM WordDistractor")
    void deleteAll();
}