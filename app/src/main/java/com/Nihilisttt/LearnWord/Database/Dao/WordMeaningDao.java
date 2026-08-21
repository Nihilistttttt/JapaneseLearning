package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;

import java.util.List;

@Dao
public interface WordMeaningDao {
    @Insert
    void insertWords(WordMeaningEntity... wordMeaningEntities);

    @Update
    void updateWords(WordMeaningEntity... wordMeaningEntities);

    //    @Delete
//    void deleteWords(WordEntity...wordEntities);
    @Query("DELETE FROM WordMeaning")
    void deleteAllWordMeanings();

    @Query("DELETE FROM WordMeaning WHERE wordId = :word_id")
    void deleteWord(String word_id);

    @Query("SELECT * FROM WordMeaning ORDER BY wordId")
    LiveData<List<WordMeaningEntity>> getAllWordMeanings();

    @Query("SELECT * FROM WordMeaning WHERE wordId = :word_id")
    LiveData<List<WordMeaningEntity>> getWordMeaningByWordId(String word_id);

    @Query("SELECT * FROM WordMeaning WHERE wordMeaningId = :word_meaning_id")
    LiveData<WordMeaningEntity> getWordMeaningByWordMeaningId(String word_meaning_id);

    @Query("SELECT * FROM WordMeaning WHERE wordMeaningId IN (:wordMeaningIdList)")
    LiveData<List<WordMeaningEntity>> getWordMeaningsByWordMeaningIdList(List<String> wordMeaningIdList);

    @Query("SELECT * FROM WordMeaning WHERE wordMeaningId IN (:wordMeaningIdList)")
    List<WordMeaningEntity> getWordMeaningsByWordMeaningIdListSync(List<String> wordMeaningIdList);

    @Query("SELECT * FROM WordMeaning WHERE wordId IN (:wordIdList)")
    List<WordMeaningEntity> getWordMeaningsByWordIdListSync(List<String> wordIdList);

    @Query("SELECT * FROM WordMeaning WHERE wordId != :excludeWordId AND translationDefinition IS NOT NULL AND translationDefinition != '' ORDER BY RANDOM() LIMIT :limit")
    List<WordMeaningEntity> getRandomMeaningsSync(String excludeWordId, int limit);

    @Query("SELECT * FROM WordMeaning WHERE wordId = :wordId LIMIT 1")
    WordMeaningEntity getFirstMeaningByWordIdSync(String wordId);
}
