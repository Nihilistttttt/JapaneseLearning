package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "SynonymWord")
public class SynonymWordEntity {
    @PrimaryKey
    @NotNull
    private String synonymWordId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String correspondingWordId;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;


    public SynonymWordEntity(@NotNull String synonymWordId, String wordId, String correspondingWordId, String kanjiComponents, String kanaComponents) {
        this.synonymWordId = synonymWordId;
        this.wordId = wordId;
        this.correspondingWordId = correspondingWordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
    }

    public String getSynonymWordId() {
        return synonymWordId;
    }

    public void setSynonymWordId(String synonym_word_id) {
        this.synonymWordId = synonym_word_id;
    }

    public String getKanjiComponents() {
        return kanjiComponents;
    }

    public void setKanjiComponents(String kanjiComponents) {
        this.kanjiComponents = kanjiComponents;
    }

    public String getKanaComponents() {
        return kanaComponents;
    }

    public void setKanaComponents(String kanaComponents) {
        this.kanaComponents = kanaComponents;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String word_id) {
        this.wordId = word_id;
    }

    public String getCorrespondingWordId() {
        return correspondingWordId;
    }

    public void setCorrespondingWordId(String corresponding_word_id) {
        this.correspondingWordId = corresponding_word_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SynonymWordEntity that = (SynonymWordEntity) o;
        return Objects.equals(synonymWordId, that.synonymWordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(synonymWordId);
    }
}