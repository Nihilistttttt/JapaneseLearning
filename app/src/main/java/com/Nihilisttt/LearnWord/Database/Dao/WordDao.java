package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordEntity;

import java.util.List;

@Dao
public interface WordDao {
    @Insert
    void insertWords(WordEntity...wordEntities);
    @Update
    void updateWords(WordEntity...wordEntities);
//    @Delete
//    void deleteWords(WordEntity...wordEntities);
    @Query("DELETE FROM Word")
    void deleteAllWords();

    @Query("DELETE FROM Word WHERE wordId = :word_id")
    void deleteWord(String word_id);

    @Query("SELECT * FROM Word ORDER BY wordId")
    LiveData<List<WordEntity>> getAllWords();

    @Query("SELECT * FROM Word WHERE wordId = :word_id")
    LiveData<WordEntity> getWordByWordId(String word_id);

    // 批量查询
    @Query("SELECT * FROM WORD WHERE wordId IN (:idList)")
    LiveData<List<WordEntity>> getWordsByIdList(List<String> idList);
}
