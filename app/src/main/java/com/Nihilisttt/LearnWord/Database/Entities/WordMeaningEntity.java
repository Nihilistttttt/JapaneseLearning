package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "WordMeaning", indices = {@Index("wordId")})
public class WordMeaningEntity {
    @PrimaryKey
    @NotNull
    private String wordMeaningId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String originalDefinition;
    @ColumnInfo
    private String translationDefinition;
    @ColumnInfo
    private String partOfSpeech;

    public WordMeaningEntity(@NonNull String wordMeaningId, String wordId, String originalDefinition, String translationDefinition, String partOfSpeech) {
        this.wordMeaningId = wordMeaningId;
        this.wordId = wordId;
        this.originalDefinition = originalDefinition;
        this.translationDefinition = translationDefinition;
        this.partOfSpeech = partOfSpeech;
    }

    public String getWordMeaningId() {
        return wordMeaningId;
    }

    public void setWordMeaningId(String word_meaning_id) {
        this.wordMeaningId = word_meaning_id;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String word_id) {
        this.wordId = word_id;
    }

    public String getOriginalDefinition() {
        return originalDefinition;
    }

    public void setOriginalDefinition(String original_definition) {
        this.originalDefinition = original_definition;
    }

    public String getTranslationDefinition() {
        return translationDefinition;
    }

    public void setTranslationDefinition(String translationDefinition) {
        this.translationDefinition = translationDefinition;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String part_of_speech) {
        this.partOfSpeech = part_of_speech;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordMeaningEntity that = (WordMeaningEntity) o;
        return Objects.equals(wordMeaningId, that.wordMeaningId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordMeaningId);
    }

}