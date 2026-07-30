package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.IdiomEntity;

import java.util.List;

@Dao
public interface IdiomDao {
    @Insert
    void insertIdioms(IdiomEntity... entities);
    @Update
    void updateIdioms(IdiomEntity... entities);
    @Query("DELETE FROM Idiom")
    void deleteAllIdioms();
    @Query("DELETE FROM Idiom WHERE idiomId = :id")
    void deleteIdiom(String id);
    @Query("SELECT * FROM Idiom ORDER BY idiomId")
    LiveData<List<IdiomEntity>> getAllIdioms();
    @Query("SELECT * FROM Idiom WHERE idiomId = :id")
    LiveData<IdiomEntity> getIdiomById(String id);
    @Query("SELECT * FROM Idiom WHERE wordId = :word_id")
    LiveData<List<IdiomEntity>> getIdiomsByWordId(String word_id);
    @Query("SELECT * FROM Idiom WHERE idiomId IN (:idList)")
    LiveData<List<IdiomEntity>> getIdiomsByIdiomIdList(List<String> idList);
}
