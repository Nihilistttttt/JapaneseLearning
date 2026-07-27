package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.KanjiFormWordEntity;

import java.util.List;

@Dao
public interface KanjiFormWordDao {
    @Insert
    void insertKanjiFormWords(KanjiFormWordEntity...kanjiFormWordEntities);
    @Update
    void updateKanjiFormWords(KanjiFormWordEntity...kanjiFormWordEntities);
    @Query("DELETE FROM KanjiFormWord")
    void deleteAllKanjiFormWords();
    @Query("DELETE FROM KanjiFormWord WHERE wordId = :word_id")
    void deleteKanjiFormWord(String word_id);
    @Query("SELECT * FROM KanjiFormWord ORDER BY wordId")
    LiveData<List<KanjiFormWordEntity>> getAllKanjiFormWords();
    @Query("SELECT * FROM KanjiFormWord WHERE wordId = :word_id")
    LiveData<List<KanjiFormWordEntity>> getKanjiFormWordByWordId(String word_id);
    @Query("SELECT * FROM KanjiFormWord WHERE kanjiFormId IN (:kanjiFormIdList)")
    LiveData<List<KanjiFormWordEntity>> getKanjiFormWordsByKanjiFormIdList(List<String> kanjiFormIdList);
}