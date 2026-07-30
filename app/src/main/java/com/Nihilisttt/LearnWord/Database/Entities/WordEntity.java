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
    @ColumnInfo(defaultValue = "[]")
    private String conjugationFormIdList;
    @NonNull
    @ColumnInfo(defaultValue = "[]")
    private String etymologyIdList;
    @NonNull
    @ColumnInfo(defaultValue = "[]")
    private String kanjiInfoIdList;
    @NonNull
    @ColumnInfo(defaultValue = "[]")
    private String usageDistinctionIdList;
    @NonNull
    @ColumnInfo(defaultValue = "[]")
    private String grammarPointIdList;
    @NonNull
    @ColumnInfo(defaultValue = "[]")
    private String idiomIdList;

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

    @NonNull
    public String getConjugationFormIdList() {
        return conjugationFormIdList;
    }

    public void setConjugationFormIdList(@NonNull String conjugationFormIdList) {
        this.conjugationFormIdList = conjugationFormIdList;
    }

    @NonNull
    public String getEtymologyIdList() {
        return etymologyIdList;
    }

    public void setEtymologyIdList(@NonNull String etymologyIdList) {
        this.etymologyIdList = etymologyIdList;
    }

    @NonNull
    public String getKanjiInfoIdList() {
        return kanjiInfoIdList;
    }

    public void setKanjiInfoIdList(@NonNull String kanjiInfoIdList) {
        this.kanjiInfoIdList = kanjiInfoIdList;
    }

    @NonNull
    public String getUsageDistinctionIdList() {
        return usageDistinctionIdList;
    }

    public void setUsageDistinctionIdList(@NonNull String usageDistinctionIdList) {
        this.usageDistinctionIdList = usageDistinctionIdList;
    }

    @NonNull
    public String getGrammarPointIdList() {
        return grammarPointIdList;
    }

    public void setGrammarPointIdList(@NonNull String grammarPointIdList) {
        this.grammarPointIdList = grammarPointIdList;
    }

    @NonNull
    public String getIdiomIdList() {
        return idiomIdList;
    }

    public void setIdiomIdList(@NonNull String idiomIdList) {
        this.idiomIdList = idiomIdList;
    }

    public WordEntity(@NotNull String wordId, String antonymWordIdList, String synonymWordIdList, String collocationIdList, String meaningIdList, String sentenceIdList,
                      @NonNull String conjugationFormIdList, @NonNull String etymologyIdList, @NonNull String kanjiInfoIdList, @NonNull String usageDistinctionIdList, @NonNull String grammarPointIdList, @NonNull String idiomIdList) {
        this.wordId = wordId;
        this.meaningIdList = meaningIdList;
        this.sentenceIdList = sentenceIdList;
        this.synonymWordIdList = synonymWordIdList;
        this.antonymWordIdList = antonymWordIdList;
        this.collocationIdList = collocationIdList;
        this.conjugationFormIdList = conjugationFormIdList;
        this.etymologyIdList = etymologyIdList;
        this.kanjiInfoIdList = kanjiInfoIdList;
        this.usageDistinctionIdList = usageDistinctionIdList;
        this.grammarPointIdList = grammarPointIdList;
        this.idiomIdList = idiomIdList;
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