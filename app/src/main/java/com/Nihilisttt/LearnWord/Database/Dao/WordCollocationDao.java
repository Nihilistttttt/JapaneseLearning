package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.Nihilisttt.LearnWord.Database.Entities.WordCollocationEntity;

import java.util.List;

@Dao
public interface WordCollocationDao {
    @Insert
    void insertWordCollocations(WordCollocationEntity...wordCollocationEntities);
    @Update
    void updateWordCollocations(WordCollocationEntity...wordCollocationEntities);
//    @Delete
//    void deleteWords(WordEntity...wordEntities);
    @Query("DELETE FROM WORDCOLLOCATION")
    void deleteAllWordCollocations();
    @Query("DELETE FROM WORDCOLLOCATION WHERE wordId = :word_id")
    void deleteWordCollocation(String word_id);

    @Query("SELECT * FROM WORDCOLLOCATION ORDER BY wordId")
    LiveData<List<WordCollocationEntity>> getAllWordCollocations();

    @Query("SELECT * FROM WORDCOLLOCATION WHERE wordId = :word_id")
    LiveData<List<WordCollocationEntity>> getWordCollocationByWordId(String word_id);

    @Query("SELECT * FROM WORDCOLLOCATION WHERE wordCollocationId = :word_collocation_id")
    LiveData<WordCollocationEntity> getWordCollocationByWordCollocationId(String word_collocation_id);

    @Query("SELECT * FROM WORDCOLLOCATION WHERE wordCollocationId IN (:wordCollocationList)")
    LiveData<List<WordCollocationEntity>> getWordCollocationsByWordCollocationIdList(List<String> wordCollocationList);
}
