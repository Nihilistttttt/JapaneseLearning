package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.GrammarPointEntity;

import java.util.List;

@Dao
public interface GrammarPointDao {
    @Insert
    void insertGrammarPoints(GrammarPointEntity... entities);
    @Update
    void updateGrammarPoints(GrammarPointEntity... entities);
    @Query("DELETE FROM GrammarPoint")
    void deleteAllGrammarPoints();
    @Query("DELETE FROM GrammarPoint WHERE grammarPointId = :id")
    void deleteGrammarPoint(String id);
    @Query("SELECT * FROM GrammarPoint ORDER BY grammarPointId")
    LiveData<List<GrammarPointEntity>> getAllGrammarPoints();
    @Query("SELECT * FROM GrammarPoint WHERE grammarPointId = :id")
    LiveData<GrammarPointEntity> getGrammarPointById(String id);
    @Query("SELECT * FROM GrammarPoint WHERE wordId = :word_id")
    LiveData<List<GrammarPointEntity>> getGrammarPointsByWordId(String word_id);
    @Query("SELECT * FROM GrammarPoint WHERE grammarPointId IN (:idList)")
    LiveData<List<GrammarPointEntity>> getGrammarPointsByGrammarPointIdList(List<String> idList);
}
