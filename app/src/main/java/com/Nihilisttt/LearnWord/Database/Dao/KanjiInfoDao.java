package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.KanjiInfoEntity;

import java.util.List;

@Dao
public interface KanjiInfoDao {
    @Insert
    void insertKanjiInfos(KanjiInfoEntity... entities);
    @Update
    void updateKanjiInfos(KanjiInfoEntity... entities);
    @Query("DELETE FROM KanjiInfo")
    void deleteAllKanjiInfos();
    @Query("DELETE FROM KanjiInfo WHERE kanjiInfoId = :id")
    void deleteKanjiInfo(String id);
    @Query("SELECT * FROM KanjiInfo ORDER BY kanjiInfoId")
    LiveData<List<KanjiInfoEntity>> getAllKanjiInfos();
    @Query("SELECT * FROM KanjiInfo WHERE kanjiInfoId = :id")
    LiveData<KanjiInfoEntity> getKanjiInfoById(String id);
    @Query("SELECT * FROM KanjiInfo WHERE wordId = :word_id")
    LiveData<List<KanjiInfoEntity>> getKanjiInfosByWordId(String word_id);
    @Query("SELECT * FROM KanjiInfo WHERE kanjiInfoId IN (:idList)")
    LiveData<List<KanjiInfoEntity>> getKanjiInfosByKanjiInfoIdList(List<String> idList);
}