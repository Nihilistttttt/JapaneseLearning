package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "KanjiInfo")
public class KanjiInfoEntity {
    @PrimaryKey
    @NotNull
    private String kanjiInfoId;
    @ColumnInfo
    private String wordId;
    @ColumnInfo
    private String kanji;
    @ColumnInfo
    private String onyomi;
    @ColumnInfo
    private String kunyomi;
    @ColumnInfo
    private String sameKanjiWords;
    @NonNull
    @ColumnInfo(defaultValue = "")
    private String translation;

    public KanjiInfoEntity(@NotNull String kanjiInfoId, String wordId, String kanji, String onyomi, String kunyomi, String sameKanjiWords, @NonNull String translation) {
        this.kanjiInfoId = kanjiInfoId;
        this.wordId = wordId;
        this.kanji = kanji;
        this.onyomi = onyomi;
        this.kunyomi = kunyomi;
        this.sameKanjiWords = sameKanjiWords;
        this.translation = translation;
    }

    public String getKanjiInfoId() { return kanjiInfoId; }
    public void setKanjiInfoId(String id) { this.kanjiInfoId = id; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getKanji() { return kanji; }
    public void setKanji(String kanji) { this.kanji = kanji; }
    public String getOnyomi() { return onyomi; }
    public void setOnyomi(String onyomi) { this.onyomi = onyomi; }
    public String getKunyomi() { return kunyomi; }
    public void setKunyomi(String kunyomi) { this.kunyomi = kunyomi; }
    public String getSameKanjiWords() { return sameKanjiWords; }
    public void setSameKanjiWords(String w) { this.sameKanjiWords = w; }
    @NonNull
    public String getTranslation() { return translation; }
    public void setTranslation(@NonNull String t) { this.translation = t; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(kanjiInfoId, ((KanjiInfoEntity) o).kanjiInfoId);
    }

    @Override
    public int hashCode() { return Objects.hash(kanjiInfoId); }
}