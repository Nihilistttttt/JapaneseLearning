package com.Nihilisttt.LearnWord.Database.Entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Entity(tableName = "WordBookItem",
        indices = {@Index("bookId"), @Index("wordId"),
                   @Index(value = {"bookId", "wordId"}, unique = true),
                   @Index(value = {"bookId", "position"})})
public class WordBookItemEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo
    @NotNull
    private String bookId;
    @ColumnInfo
    @NotNull
    private String wordId;
    @ColumnInfo
    private int position;

    public WordBookItemEntity(long id, @NotNull String bookId, @NotNull String wordId, int position) {
        this.id = id;
        this.bookId = bookId;
        this.wordId = wordId;
        this.position = position;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getWordId() { return wordId; }
    public void setWordId(String wordId) { this.wordId = wordId; }
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordBookItemEntity that = (WordBookItemEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}