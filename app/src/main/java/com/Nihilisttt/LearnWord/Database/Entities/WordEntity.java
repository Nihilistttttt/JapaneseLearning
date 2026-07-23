package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "Word")
public class WordEntity {
    @PrimaryKey
    @NotNull
    private String wordId;
    @ColumnInfo
    private String meaningIdList;
    @ColumnInfo
    private String sentenceIdList;
    @ColumnInfo
    private String synonymWordIdList;
    @ColumnInfo
    private String antonymWordIdList;
    @ColumnInfo
    private String collocationIdList;

    @NonNull
    public String getWordId() {
        return wordId;
    }

    public void setWordId(@NonNull String word_id) {
        this.wordId = word_id;
    }

    public String getMeaningIdList() {
        return meaningIdList;
    }

    public void setMeaningIdList(String meaningIdList) {
        this.meaningIdList = meaningIdList;
    }

    public String getSentenceIdList() {
        return sentenceIdList;
    }

    public void setSentenceIdList(String sentenceIdList) {
        this.sentenceIdList = sentenceIdList;
    }

    public String getSynonymWordIdList() {
        return synonymWordIdList;
    }

    public void setSynonymWordIdList(String synonymWordIdList) {
        this.synonymWordIdList = synonymWordIdList;
    }

    public String getAntonymWordIdList() {
        return antonymWordIdList;
    }

    public void setAntonymWordIdList(String antonymWordIdList) {
        this.antonymWordIdList = antonymWordIdList;
    }

    public String getCollocationIdList() {
        return collocationIdList;
    }

    public void setCollocationIdList(String collocationIdList) {
        this.collocationIdList = collocationIdList;
    }

    public WordEntity(@NotNull String wordId, String antonymWordIdList, String synonymWordIdList, String collocationIdList, String meaningIdList, String sentenceIdList) {
        this.wordId = wordId;
        this.meaningIdList = meaningIdList;
        this.sentenceIdList = sentenceIdList;
        this.synonymWordIdList = synonymWordIdList;
        this.antonymWordIdList = antonymWordIdList;
        this.collocationIdList = collocationIdList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordEntity that = (WordEntity) o;
        return Objects.equals(wordId, that.wordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordId);
    }
}