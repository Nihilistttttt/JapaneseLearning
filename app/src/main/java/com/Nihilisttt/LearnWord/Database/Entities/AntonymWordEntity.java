package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "AntonymWord", indices = {@Index("wordId")})
public class AntonymWordEntity {
    @PrimaryKey
    @NotNull
    private String antonymWordId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String correspondingWordId;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;


    public AntonymWordEntity(@NotNull String antonymWordId, String wordId, String correspondingWordId, String kanjiComponents, String kanaComponents) {
        this.antonymWordId = antonymWordId;
        this.wordId = wordId;
        this.correspondingWordId = correspondingWordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
    }

    public String getAntonymWordId() {
        return antonymWordId;
    }

    public void setAntonymWordId(String antonym_word_id) {
        this.antonymWordId = antonym_word_id;
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
        AntonymWordEntity that = (AntonymWordEntity) o;
        return Objects.equals(antonymWordId, that.antonymWordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(antonymWordId);
    }
}