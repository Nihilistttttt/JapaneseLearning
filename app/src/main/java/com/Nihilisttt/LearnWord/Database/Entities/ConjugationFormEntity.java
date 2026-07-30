package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "ConjugationForm")
public class ConjugationFormEntity {
    @PrimaryKey
    @NotNull
    private String conjugationFormId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String formName;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String formNameTranslation;

    public ConjugationFormEntity(@NotNull String conjugationFormId, String wordId, String formName, String kanjiComponents, String kanaComponents, @NonNull String formNameTranslation) {
        this.conjugationFormId = conjugationFormId;
        this.wordId = wordId;
        this.formName = formName;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.formNameTranslation = formNameTranslation;
    }

    public String getConjugationFormId() { return conjugationFormId; }
    public void setConjugationFormId(String id) { this.conjugationFormId = id; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getFormName() { return formName; }
    public void setFormName(String formName) { this.formName = formName; }
    public String getKanjiComponents() { return kanjiComponents; }
    public void setKanjiComponents(String kc) { this.kanjiComponents = kc; }
    public String getKanaComponents() { return kanaComponents; }
    public void setKanaComponents(String kc) { this.kanaComponents = kc; }
    @NonNull
    public String getFormNameTranslation() { return formNameTranslation; }
    public void setFormNameTranslation(@NonNull String t) { this.formNameTranslation = t; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(conjugationFormId, ((ConjugationFormEntity) o).conjugationFormId);
    }

    @Override
    public int hashCode() { return Objects.hash(conjugationFormId); }
}