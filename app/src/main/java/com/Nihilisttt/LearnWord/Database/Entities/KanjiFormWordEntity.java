package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "KanjiFormWord")
public class KanjiFormWordEntity {
    @PrimaryKey
    @NotNull
    private String kanjiFormId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;

    public KanjiFormWordEntity(@NotNull String kanjiFormId, String wordId, String kanjiComponents, String kanaComponents) {
        this.kanjiFormId = kanjiFormId;
        this.wordId = wordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
    }

    public String getKanjiFormId() { return kanjiFormId; }
    public void setKanjiFormId(String kanjiFormId) { this.kanjiFormId = kanjiFormId; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getKanjiComponents() { return kanjiComponents; }
    public void setKanjiComponents(String kanjiComponents) { this.kanjiComponents = kanjiComponents; }
    public String getKanaComponents() { return kanaComponents; }
    public void setKanaComponents(String kanaComponents) { this.kanaComponents = kanaComponents; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KanjiFormWordEntity that = (KanjiFormWordEntity) o;
        return Objects.equals(kanjiFormId, that.kanjiFormId);
    }

    @Override
    public int hashCode() { return Objects.hash(kanjiFormId); }
}