package com.Nihilisttt.LearnWord.Algorithm;

public class StudyQueueEntry {
    private final String wordId;
    private final StudyStage stage;
    private int correctCount;
    private boolean completed;

    public StudyQueueEntry(String wordId, StudyStage stage) {
        this.wordId = wordId;
        this.stage = stage;
        this.correctCount = 0;
        this.completed = false;
    }

    public String getWordId() { return wordId; }
    public StudyStage getStage() { return stage; }
    public int getCorrectCount() { return correctCount; }
    public boolean isCompleted() { return completed; }

    public void incrementCorrectCount() { correctCount++; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}