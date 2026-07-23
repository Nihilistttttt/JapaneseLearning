package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "WordSentence")
public class WordSentenceEntity {
    @PrimaryKey
    @NotNull
    private String wordSentenceId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String wordMeaningId;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;
    @ColumnInfo
    private String wordIdList;
    @ColumnInfo
    private String translation;
    @ColumnInfo
    private String source;
    @ColumnInfo
    private String audioUrl;

    public WordSentenceEntity(@NotNull String wordSentenceId, String wordId, String wordMeaningId, String kanjiComponents, String kanaComponents, String wordIdList, String translation, String source, String audioUrl) {
        this.wordSentenceId = wordSentenceId;
        this.wordId = wordId;
        this.wordMeaningId = wordMeaningId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.wordIdList = wordIdList;
        this.translation = translation;
        this.source = source;
        this.audioUrl = audioUrl;
    }

    @NonNull
    public String getWordSentenceId() {
        return wordSentenceId;
    }

    public void setWordSentenceId(@NonNull String word_sentence_id) {
        this.wordSentenceId = word_sentence_id;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String word_id) {
        this.wordId = word_id;
    }

    public String getWordMeaningId() {
        return wordMeaningId;
    }

    public void setWordMeaningId(String word_meaning_id) {
        this.wordMeaningId = word_meaning_id;
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

    public String getWordIdList() {
        return wordIdList;
    }

    public void setWordIdList(String wordIdList) {
        this.wordIdList = wordIdList;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordSentenceEntity that = (WordSentenceEntity) o;
        return Objects.equals(wordSentenceId, that.wordSentenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordSentenceId);
    }
}