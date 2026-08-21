package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "WordDistractor",
        indices = {@Index("wordId"), @Index(value = {"wordId", "distractorWordId"}, unique = true)})
public class WordDistractorEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo
    private String wordId;

    @NonNull
    @ColumnInfo
    private String distractorWordId;

    @ColumnInfo
    private float score;

    public WordDistractorEntity(long id, @NonNull String wordId, @NonNull String distractorWordId, float score) {
        this.id = id;
        this.wordId = wordId;
        this.distractorWordId = distractorWordId;
        this.score = score;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    @NonNull public String getWordId() { return wordId; }
    public void setWordId(@NonNull String wordId) { this.wordId = wordId; }
    @NonNull public String getDistractorWordId() { return distractorWordId; }
    public void setDistractorWordId(@NonNull String distractorWordId) { this.distractorWordId = distractorWordId; }
    public float getScore() { return score; }
    public void setScore(float score) { this.score = score; }
}