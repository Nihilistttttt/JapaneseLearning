package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "Idiom")
public class IdiomEntity {
    @PrimaryKey
    @NotNull
    private String idiomId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;
    @ColumnInfo
    private String translation;
    @ColumnInfo
    private String wordIdList;

    public IdiomEntity(@NotNull String idiomId, String wordId, String kanjiComponents, String kanaComponents, String translation, String wordIdList) {
        this.idiomId = idiomId;
        this.wordId = wordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.translation = translation;
        this.wordIdList = wordIdList;
    }

    public String getIdiomId() { return idiomId; }
    public void setIdiomId(String id) { this.idiomId = id; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getKanjiComponents() { return kanjiComponents; }
    public void setKanjiComponents(String kc) { this.kanjiComponents = kc; }
    public String getKanaComponents() { return kanaComponents; }
    public void setKanaComponents(String kc) { this.kanaComponents = kc; }
    public String getTranslation() { return translation; }
    public void setTranslation(String t) { this.translation = t; }
    public String getWordIdList() { return wordIdList; }
    public void setWordIdList(String ids) { this.wordIdList = ids; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(idiomId, ((IdiomEntity) o).idiomId);
    }

    @Override
    public int hashCode() { return Objects.hash(idiomId); }
}