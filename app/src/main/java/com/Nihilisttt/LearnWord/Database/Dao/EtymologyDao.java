package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.EtymologyEntity;

import java.util.List;

@Dao
public interface EtymologyDao {
    @Insert
    void insertEtymologies(EtymologyEntity... entities);
    @Update
    void updateEtymologies(EtymologyEntity... entities);
    @Query("DELETE FROM Etymology")
    void deleteAllEtymologies();
    @Query("DELETE FROM Etymology WHERE etymologyId = :id")
    void deleteEtymology(String id);
    @Query("SELECT * FROM Etymology ORDER BY etymologyId")
    LiveData<List<EtymologyEntity>> getAllEtymologies();
    @Query("SELECT * FROM Etymology WHERE etymologyId = :id")
    LiveData<EtymologyEntity> getEtymologyById(String id);
    @Query("SELECT * FROM Etymology WHERE etymologyId IN (:etymologyIdList)")
    LiveData<List<EtymologyEntity>> getEtymologiesByIdList(List<String> etymologyIdList);
}