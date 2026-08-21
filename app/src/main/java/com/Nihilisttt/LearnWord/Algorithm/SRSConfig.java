package com.Nihilisttt.LearnWord.Algorithm;

public class SRSConfig {
    public static final int STATUS_STUDYING = 0;
    public static final int STATUS_COMPLETED = 1;
    public static final int STATUS_DELETED = -1;

    public static final int MASTERED_THRESHOLD = 6;

    private static final long MINUTE = 60 * 1000L;
    private static final long HOUR = 60 * MINUTE;
    private static final long DAY = 24 * HOUR;

    public static final long[] REVIEW_INTERVALS = {
            5 * MINUTE,
            30 * MINUTE,
            12 * HOUR,
            1 * DAY,
            2 * DAY,
            4 * DAY,
            7 * DAY,
            15 * DAY,
            30 * DAY,
            60 * DAY,
            90 * DAY,
            180 * DAY,
    };

    public static final String[] INTERVAL_DISPLAYS = {
            "5分钟",
            "30分钟",
            "12小时",
            "1天",
            "2天",
            "4天",
            "7天",
            "15天",
            "1月",
            "2月",
            "3月",
            "6月",
    };

    public static long getInterval(int studyCycle) {
        int index = Math.min(studyCycle, REVIEW_INTERVALS.length - 1);
        return REVIEW_INTERVALS[index];
    }

    public static String getIntervalDisplay(int studyCycle) {
        int index = Math.min(studyCycle, INTERVAL_DISPLAYS.length - 1);
        return INTERVAL_DISPLAYS[index];
    }

    public static long getDayStartTime(long now) {
        return now - (now % DAY);
    }
}