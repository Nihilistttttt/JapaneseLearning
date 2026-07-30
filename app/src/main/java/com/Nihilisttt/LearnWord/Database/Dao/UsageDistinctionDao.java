package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.UsageDistinctionEntity;

import java.util.List;

@Dao
public interface UsageDistinctionDao {
    @Insert
    void insertUsageDistinctions(UsageDistinctionEntity... entities);
    @Update
    void updateUsageDistinctions(UsageDistinctionEntity... entities);
    @Query("DELETE FROM UsageDistinction")
    void deleteAllUsageDistinctions();
    @Query("DELETE FROM UsageDistinction WHERE usageDistinctionId = :id")
    void deleteUsageDistinction(String id);
    @Query("SELECT * FROM UsageDistinction ORDER BY usageDistinctionId")
    LiveData<List<UsageDistinctionEntity>> getAllUsageDistinctions();
    @Query("SELECT * FROM UsageDistinction WHERE usageDistinctionId = :id")
    LiveData<UsageDistinctionEntity> getUsageDistinctionById(String id);
    @Query("SELECT * FROM UsageDistinction WHERE wordId = :word_id")
    LiveData<List<UsageDistinctionEntity>> getUsageDistinctionsByWordId(String word_id);
    @Query("SELECT * FROM UsageDistinction WHERE usageDistinctionId IN (:idList)")
    LiveData<List<UsageDistinctionEntity>> getUsageDistinctionsByUsageDistinctionIdList(List<String> idList);
}