package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "RelatedWord")
public class RelatedWordEntity {
    @PrimaryKey
    @NotNull
    private String relatedWordId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String correspondingWordId;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;


    public RelatedWordEntity(@NotNull String relatedWordId, String wordId, String correspondingWordId, String kanjiComponents, String kanaComponents) {
        this.relatedWordId = relatedWordId;
        this.wordId = wordId;
        this.correspondingWordId = correspondingWordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
    }

    public String getRelatedWordId() {
        return relatedWordId;
    }

    public void setRelatedWordId(String related_word_id) {
        this.relatedWordId = related_word_id;
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
        RelatedWordEntity that = (RelatedWordEntity) o;
        return Objects.equals(relatedWordId, that.relatedWordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relatedWordId);
    }
}