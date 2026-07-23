package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "BasicWord")
public class BasicWordEntity {
    @PrimaryKey
    @NotNull
    private String wordId;
    @ColumnInfo
    private String kanjiComponents;
    @ColumnInfo
    private String kanaComponents;
    @ColumnInfo
    private String audioUrl;
    @ColumnInfo
    private String accentMark;
    @ColumnInfo
    private String mnemonic;

    public BasicWordEntity(String wordId, String kanjiComponents, String kanaComponents, String audioUrl, String accentMark, String mnemonic) {
        this.wordId = wordId;
        this.kanjiComponents = kanjiComponents;
        this.kanaComponents = kanaComponents;
        this.audioUrl = audioUrl;
        this.accentMark = accentMark;
        this.mnemonic = mnemonic;
    }

    public String getWordId() {
        return wordId;
    }

    public void setWordId(String word_id) {
        this.wordId = word_id;
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

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getAccentMark() {
        return accentMark;
    }

    public void setAccentMark(String accentMark) {
        this.accentMark = accentMark;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicWordEntity that = (BasicWordEntity) o;
        return Objects.equals(wordId, that.wordId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordId);
    }
}