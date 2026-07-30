package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "UsageDistinction")
public class UsageDistinctionEntity {
    @PrimaryKey
    @NotNull
    private String usageDistinctionId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String distinctionText;
    @ColumnInfo
    private String comparedWordIds;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String kanjiComponents;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String kanaComponents;

    public UsageDistinctionEntity(@NotNull String usageDistinctionId, String wordId, String distinctionText, String comparedWordIds, @NonNull String kanjiComponents, @NonNull String kanaComponents) {
        this.usageDistinctionId = usageDistinctionId;
        this.wordId = wordId;
        this.distinctionText = distinctionText;
        this.comparedWordIds = comparedWordIds;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
    }

    public String getUsageDistinctionId() { return usageDistinctionId; }
    public void setUsageDistinctionId(String id) { this.usageDistinctionId = id; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getDistinctionText() { return distinctionText; }
    public void setDistinctionText(String t) { this.distinctionText = t; }
    public String getComparedWordIds() { return comparedWordIds; }
    public void setComparedWordIds(String ids) { this.comparedWordIds = ids; }
    public String getKanjiComponents() { return kanjiComponents; }
    public void setKanjiComponents(String kc) { this.kanjiComponents = kc; }
    public String getKanaComponents() { return kanaComponents; }
    public void setKanaComponents(String kc) { this.kanaComponents = kc; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(usageDistinctionId, ((UsageDistinctionEntity) o).usageDistinctionId);
    }

    @Override
    public int hashCode() { return Objects.hash(usageDistinctionId); }
}