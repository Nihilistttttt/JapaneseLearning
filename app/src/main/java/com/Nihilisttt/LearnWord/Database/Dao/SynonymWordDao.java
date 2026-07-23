package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.AntonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.SynonymWordEntity;

import java.util.List;

@Dao
public interface SynonymWordDao {
    @Insert
    void insertSynonymWords(SynonymWordEntity... synonymWordEntities);

    @Update
    void updateSynonymWords(SynonymWordEntity... synonymWordEntities);

    //    @Delete
//    void deleteWords(WordEntity...wordEntities);
    @Query("DELETE FROM SYNONYMWORD")
    void deleteAllSynonymWords();

    @Query("DELETE FROM SYNONYMWORD WHERE wordId = :word_id")
    void deleteSynonymWord(String word_id);

    @Query("SELECT * FROM SYNONYMWORD ORDER BY wordId")
    LiveData<List<SynonymWordEntity>> getAllSynonymWords();

    @Query("SELECT * FROM SYNONYMWORD WHERE wordId = :word_id")
    LiveData<List<SynonymWordEntity>> getSynonymWordByWordId(String word_id);

    @Query("SELECT * FROM SYNONYMWORD WHERE synonymWordId IN (:SynonymWordIdList)")
    LiveData<List<SynonymWordEntity>> getSynonymWordsBySynonymIdList(List<String> SynonymWordIdList);
}
