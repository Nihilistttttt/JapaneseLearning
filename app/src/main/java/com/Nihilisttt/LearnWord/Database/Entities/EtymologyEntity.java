package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "Etymology")
public class EtymologyEntity {
    @PrimaryKey
    @NotNull
    private String etymologyId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String etymologyType;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;
    @ColumnInfo
    private String wordIdList;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String translation;

    public EtymologyEntity(@NotNull String etymologyId, String wordId, String etymologyType,
                           String kanjiComponents, String kanaComponents, String wordIdList,
                           @NonNull String translation) {
        this.etymologyId = etymologyId;
        this.wordId = wordId;
        this.etymologyType = etymologyType;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.wordIdList = wordIdList;
        this.translation = translation;
    }

    public String getEtymologyId() { return etymologyId; }
    public void setEtymologyId(String etymologyId) { this.etymologyId = etymologyId; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getEtymologyType() { return etymologyType; }
    public void setEtymologyType(String etymologyType) { this.etymologyType = etymologyType; }
    public String getKanjiComponents() { return kanjiComponents; }
    public void setKanjiComponents(String kanjiComponents) { this.kanjiComponents = kanjiComponents; }
    public String getKanaComponents() { return kanaComponents; }
    public void setKanaComponents(String kanaComponents) { this.kanaComponents = kanaComponents; }
    public String getWordIdList() { return wordIdList; }
    public void setWordIdList(String wordIdList) { this.wordIdList = wordIdList; }
    @NonNull
    public String getTranslation() { return translation; }
    public void setTranslation(@NonNull String translation) { this.translation = translation; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EtymologyEntity that = (EtymologyEntity) o;
        return Objects.equals(etymologyId, that.etymologyId);
    }

    @Override
    public int hashCode() { return Objects.hash(etymologyId); }
}