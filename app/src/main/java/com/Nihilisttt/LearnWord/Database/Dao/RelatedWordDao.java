package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.RelatedWordEntity;

import java.util.List;

@Dao
public interface RelatedWordDao {
    @Insert
    void insertRelatedWords(RelatedWordEntity... relatedWordEntities);

    @Update
    void updateRelatedWords(RelatedWordEntity... relatedWordEntities);

    @Query("DELETE FROM RELATEDWORD")
    void deleteAllRelatedWords();

    @Query("DELETE FROM RELATEDWORD WHERE wordId = :word_id")
    void deleteRelatedWord(String word_id);

    @Query("SELECT * FROM RELATEDWORD ORDER BY wordId")
    LiveData<List<RelatedWordEntity>> getAllRelatedWords();

    @Query("SELECT * FROM RELATEDWORD WHERE wordId = :word_id")
    LiveData<List<RelatedWordEntity>> getRelatedWordByWordId(String word_id);

    @Query("SELECT * FROM RELATEDWORD WHERE relatedWordId IN (:RelatedWordIdList)")
    LiveData<List<RelatedWordEntity>> getRelatedWordsByRelatedIdList(List<String> RelatedWordIdList);
}