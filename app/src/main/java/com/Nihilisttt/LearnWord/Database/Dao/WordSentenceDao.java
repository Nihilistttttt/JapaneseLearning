package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.WordSentenceEntity;

import java.util.List;

@Dao
public interface WordSentenceDao {
    @Insert
    void insertWords(WordSentenceEntity...wordSentenceEntities);
    @Update
    void updateWords(WordSentenceEntity...wordSentenceEntities);
//    @Delete
//    void deleteWords(WordEntity...wordEntities);
    @Query("DELETE FROM WORDSENTENCE")
    void deleteAllWordSentences();
    @Query("DELETE FROM WORDSENTENCE WHERE wordId = :word_id")
    void deleteWord(String word_id);
    @Query("SELECT * FROM WORDSENTENCE ORDER BY wordId")
    LiveData<List<WordSentenceEntity>> getAllWordSentence();
    @Query("SELECT * FROM WORDSENTENCE WHERE wordId = :word_id")
    LiveData<List<WordSentenceEntity>> getWordSentenceByWordId(String word_id);

    @Query("SELECT * FROM WORDSENTENCE WHERE wordMeaningId = :word_meaning_id")
    LiveData<List<WordSentenceEntity>> getWordSentenceByWordMeaningId(String word_meaning_id);

    @Query("SELECT * FROM WORDSENTENCE WHERE wordSentenceId = :word_sentence_id")
    LiveData<WordSentenceEntity> getWordSentenceByWordSentenceId(String word_sentence_id);

    @Query("SELECT * FROM WORDSENTENCE WHERE wordSentenceId IN (:wordSentenceIdList)")
    LiveData<List<WordSentenceEntity>> getWordSentencesByWordSentenceIdList(List<String> wordSentenceIdList);
}
