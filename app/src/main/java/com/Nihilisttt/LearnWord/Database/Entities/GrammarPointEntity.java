package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "GrammarPoint")
public class GrammarPointEntity {
    @PrimaryKey
    @NotNull
    private String grammarPointId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String grammarName;
    @ColumnInfo
    private String grammarDescription;
    @ColumnInfo
    private String exampleKanji;
    @ColumnInfo
    private String exampleKana;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String nameKanjiComponents;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String nameKanaComponents;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String descKanjiComponents;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String descKanaComponents;

    public GrammarPointEntity(@NotNull String grammarPointId, String wordId, String grammarName, String grammarDescription, String exampleKanji, String exampleKana, @NonNull String nameKanjiComponents, @NonNull String nameKanaComponents, @NonNull String descKanjiComponents, @NonNull String descKanaComponents) {
        this.grammarPointId = grammarPointId;
        this.wordId = wordId;
        this.grammarName = grammarName;
        this.grammarDescription = grammarDescription;
        this.exampleKanji = exampleKanji;
        this.exampleKana = exampleKana;
        this.nameKanjiComponents = nameKanjiComponents;
        this.nameKanaComponents = nameKanaComponents;
        this.descKanjiComponents = descKanjiComponents;
        this.descKanaComponents = descKanaComponents;
    }

    public String getGrammarPointId() { return grammarPointId; }
    public void setGrammarPointId(String id) { this.grammarPointId = id; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getGrammarName() { return grammarName; }
    public void setGrammarName(String n) { this.grammarName = n; }
    public String getGrammarDescription() { return grammarDescription; }
    public void setGrammarDescription(String d) { this.grammarDescription = d; }
    public String getExampleKanji() { return exampleKanji; }
    public void setExampleKanji(String k) { this.exampleKanji = k; }
    public String getExampleKana() { return exampleKana; }
    public void setExampleKana(String k) { this.exampleKana = k; }
    public String getNameKanjiComponents() { return nameKanjiComponents; }
    public void setNameKanjiComponents(String k) { this.nameKanjiComponents = k; }
    public String getNameKanaComponents() { return nameKanaComponents; }
    public void setNameKanaComponents(String k) { this.nameKanaComponents = k; }
    public String getDescKanjiComponents() { return descKanjiComponents; }
    public void setDescKanjiComponents(String k) { this.descKanjiComponents = k; }
    public String getDescKanaComponents() { return descKanaComponents; }
    public void setDescKanaComponents(String k) { this.descKanaComponents = k; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(grammarPointId, ((GrammarPointEntity) o).grammarPointId);
    }

    @Override
    public int hashCode() { return Objects.hash(grammarPointId); }
}