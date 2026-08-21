package com.Nihilisttt.LearnWord.Algorithm;

public enum StudyStage {
    NEW(0, "初学"),
    REVIEW(1, "复习"),
    FINAL(2, "最后一关");

    private final int value;
    private final String displayName;

    StudyStage(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() { return value; }
    public String getDisplayName() { return displayName; }

    public static StudyStage fromValue(int value) {
        for (StudyStage stage : values()) {
            if (stage.value == value) return stage;
        }
        return NEW;
    }
}