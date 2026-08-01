package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.DerivedWordEntity;

import java.util.List;

@Dao
public interface DerivedWordDao {
    @Insert
    void insertDerivedWords(DerivedWordEntity... derivedWordEntities);

    @Update
    void updateDerivedWords(DerivedWordEntity... derivedWordEntities);

    @Query("DELETE FROM DERIVEDWORD")
    void deleteAllDerivedWords();

    @Query("DELETE FROM DERIVEDWORD WHERE wordId = :word_id")
    void deleteDerivedWord(String word_id);

    @Query("SELECT * FROM DERIVEDWORD ORDER BY wordId")
    LiveData<List<DerivedWordEntity>> getAllDerivedWords();

    @Query("SELECT * FROM DERIVEDWORD WHERE wordId = :word_id")
    LiveData<List<DerivedWordEntity>> getDerivedWordByWordId(String word_id);

    @Query("SELECT * FROM DERIVEDWORD WHERE derivedWordId IN (:DerivedWordIdList)")
    LiveData<List<DerivedWordEntity>> getDerivedWordsByDerivedIdList(List<String> DerivedWordIdList);
}