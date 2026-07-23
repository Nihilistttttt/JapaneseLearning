package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.AntonymWordEntity;

import java.util.List;

@Dao
public interface AntonymWordDao {
    @Insert
    void insertAntonymWords(AntonymWordEntity...antonymWordEntities);
    @Update
    void updateAntonymWords(AntonymWordEntity...antonymWordEntities);
//    @Delete
//    void deleteWords(WordEntity...wordEntities);
    @Query("DELETE FROM AntonymWord")
    void deleteAllAntonymWords();

    @Query("DELETE FROM AntonymWord WHERE wordId = :word_id")
    void deleteAntonymWord(String word_id);

    @Query("SELECT * FROM AntonymWord ORDER BY wordId")
    LiveData<List<AntonymWordEntity>> getAllAntonymWords();

    @Query("SELECT * FROM AntonymWord WHERE wordId = :word_id")
    LiveData<List<AntonymWordEntity>> getAntonymWordByWordId(String word_id);

    @Query("SELECT * FROM AntonymWord WHERE antonymWordId IN (:antonymWordList)")
    LiveData<List<AntonymWordEntity>> getAntonymWordsByAntonymIdList(List<String> antonymWordList);
}
