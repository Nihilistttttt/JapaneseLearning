package com.Nihilisttt.LearnWord.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.Nihilisttt.LearnWord.Database.Entities.WordBookItemEntity;

import java.util.List;

@Dao
public interface WordBookItemDao {
    @Insert
    void insertItems(List<WordBookItemEntity> items);

    @Query("DELETE FROM WordBookItem WHERE bookId = :bookId")
    void deleteItemsByBookId(String bookId);

    @Query("SELECT wordId FROM WordBookItem WHERE bookId = :bookId ORDER BY position ASC")
    List<String> getWordIdsByBookIdSync(String bookId);

    @Query("SELECT wordId FROM WordBookItem WHERE bookId = :bookId ORDER BY position ASC LIMIT :limit OFFSET :offset")
    List<String> getWordIdsByBookIdPagedSync(String bookId, int limit, int offset);

    @Query("SELECT COUNT(*) FROM WordBookItem WHERE bookId = :bookId")
    int getWordCountByBookIdSync(String bookId);

    @Query("SELECT wbi.wordId FROM WordBookItem wbi WHERE wbi.bookId = :bookId AND wbi.wordId NOT IN (SELECT wordId FROM WordReview WHERE bookId = :bookId AND status != -1) ORDER BY wbi.position ASC LIMIT :limit")
    List<String> getNewWordIdsSync(String bookId, int limit);
}