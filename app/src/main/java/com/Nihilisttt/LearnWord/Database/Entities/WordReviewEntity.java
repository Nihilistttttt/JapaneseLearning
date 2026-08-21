package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "WordReview",
        indices = {@Index("bookId"), @Index("wordId"),
                   @Index(value = {"wordId", "bookId"}, unique = true),
                   @Index(value = {"bookId", "nextReviewTime"})})
public class WordReviewEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo
    @NotNull
    private String wordId;
    @ColumnInfo
    @NotNull
    private String bookId;
    @ColumnInfo(defaultValue = "0")
    private int status;
    @ColumnInfo(defaultValue = "0")
    private int studyCycle;
    @ColumnInfo(defaultValue = "0")
    private long nextReviewTime;
    @ColumnInfo(defaultValue = "0")
    private int lapses;
    @ColumnInfo
    private long createTime;
    @ColumnInfo
    private long updateTime;

    public WordReviewEntity(long id, @NotNull String wordId, @NotNull String bookId, int status,
                            int studyCycle, long nextReviewTime, int lapses,
                            long createTime, long updateTime) {
        this.id = id;
        this.wordId = wordId;
        this.bookId = bookId;
        this.status = status;
        this.studyCycle = studyCycle;
        this.nextReviewTime = nextReviewTime;
        this.lapses = lapses;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public int getStudyCycle() { return studyCycle; }
    public void setStudyCycle(int studyCycle) { this.studyCycle = studyCycle; }
    public long getNextReviewTime() { return nextReviewTime; }
    public void setNextReviewTime(long nextReviewTime) { this.nextReviewTime = nextReviewTime; }
    public int getLapses() { return lapses; }
    public void setLapses(int lapses) { this.lapses = lapses; }
    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordReviewEntity that = (WordReviewEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}