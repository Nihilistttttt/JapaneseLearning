package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "WordBook")
public class WordBookEntity {
    @PrimaryKey
    @NotNull
    private String bookId;
    @ColumnInfo
    private String name;
    @ColumnInfo(defaultValue = "")
    private String description;
    @ColumnInfo(defaultValue = "0")
    private int jlptLevel;
    @ColumnInfo(defaultValue = "0")
    private boolean isPreset;
    @ColumnInfo(defaultValue = "0")
    private int totalWordCount;
    @ColumnInfo(defaultValue = "0")
    private int learnedCount;
    @ColumnInfo(defaultValue = "0")
    private int masteredCount;
    @ColumnInfo(defaultValue = "20")
    private int dailyNewTarget;
    @ColumnInfo(defaultValue = "200")
    private int dailyReviewTarget;
    @ColumnInfo(defaultValue = "0")
    private int sortOrder;
    @ColumnInfo(defaultValue = "0")
    private int color;
    @ColumnInfo
    private long createdAt;
    @ColumnInfo
    private long updatedAt;

    public WordBookEntity(@NotNull String bookId, String name, String description, int jlptLevel,
                          boolean isPreset, int totalWordCount, int learnedCount, int masteredCount,
                          int dailyNewTarget, int dailyReviewTarget, int sortOrder, int color,
                          long createdAt, long updatedAt) {
        this.bookId = bookId;
        this.name = name;
        this.description = description;
        this.jlptLevel = jlptLevel;
        this.isPreset = isPreset;
        this.totalWordCount = totalWordCount;
        this.learnedCount = learnedCount;
        this.masteredCount = masteredCount;
        this.dailyNewTarget = dailyNewTarget;
        this.dailyReviewTarget = dailyReviewTarget;
        this.sortOrder = sortOrder;
        this.color = color;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getJlptLevel() { return jlptLevel; }
    public void setJlptLevel(int jlptLevel) { this.jlptLevel = jlptLevel; }
    public boolean isPreset() { return isPreset; }
    public void setPreset(boolean preset) { isPreset = preset; }
    public int getTotalWordCount() { return totalWordCount; }
    public void setTotalWordCount(int totalWordCount) { this.totalWordCount = totalWordCount; }
    public int getLearnedCount() { return learnedCount; }
    public void setLearnedCount(int learnedCount) { this.learnedCount = learnedCount; }
    public int getMasteredCount() { return masteredCount; }
    public void setMasteredCount(int masteredCount) { this.masteredCount = masteredCount; }
    public int getDailyNewTarget() { return dailyNewTarget; }
    public void setDailyNewTarget(int dailyNewTarget) { this.dailyNewTarget = dailyNewTarget; }
    public int getDailyReviewTarget() { return dailyReviewTarget; }
    public void setDailyReviewTarget(int dailyReviewTarget) { this.dailyReviewTarget = dailyReviewTarget; }
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordBookEntity that = (WordBookEntity) o;
        return Objects.equals(bookId, that.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }
}