package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;

import java.util.List;

@Dao
public interface BasicWordDao {
    @Insert
    void insertBasicWords(BasicWordEntity... basicWordEntities);
    @Update
    void updateBasicWords(BasicWordEntity... basicWordEntities);
//    @Delete
//    void deleteWords(WordEntity...wordEntities);
    @Query("DELETE FROM BasicWord")
    void deleteAllBasicWords();
    @Query("DELETE FROM BasicWord WHERE wordId = :word_id")
    void deleteBasicWord(String word_id);
    @Query("SELECT * FROM BasicWord ORDER BY wordId")
    LiveData<List<BasicWordEntity>> getAllBasicWords();
    @Query("SELECT * FROM BasicWord WHERE wordId = :word_id")
    LiveData<BasicWordEntity> getBasicWordByWordId(String word_id);
    @Query("SELECT * FROM BasicWord WHERE wordId = :word_id")
    BasicWordEntity getBasicWordByWordIdSync(String word_id);

    @Query("SELECT * FROM BasicWord WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(kanjiComponents,'[',''),']',''),'\"',''),',',''),' ','') LIKE '%' || :query || '%' OR REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(kanaComponents,'[',''),']',''),'\"',''),',',''),' ','') LIKE '%' || :query || '%' ORDER BY wordFrequency DESC LIMIT :limit")
    List<BasicWordEntity> searchBasicWordsSync(String query, int limit);

    @Query("SELECT wordId FROM BasicWord WHERE jlptLevel = :level ORDER BY wordFrequency DESC")
    List<String> getWordIdsByJlptLevelSync(int level);

    @Query("SELECT COUNT(*) FROM BasicWord WHERE jlptLevel = :level")
    int getWordCountByJlptLevelSync(int level);

    @Query("SELECT DISTINCT jlptLevel FROM BasicWord WHERE jlptLevel > 0 ORDER BY jlptLevel ASC")
    List<Integer> getAvailableJlptLevelsSync();
}
