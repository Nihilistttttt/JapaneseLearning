package com.Nihilisttt.LearnWord.Algorithm;

import com.Nihilisttt.LearnWord.Database.Entities.WordReviewEntity;

public class EbbinghausAlgorithm {

    public static WordReviewEntity pass(WordReviewEntity review, long now) {
        int cycle = review.getStudyCycle();
        long interval = SRSConfig.getInterval(cycle);
        review.setStatus(SRSConfig.STATUS_COMPLETED);
        review.setStudyCycle(cycle + 1);
        review.setNextReviewTime(now + interval);
        review.setUpdateTime(now);
        return review;
    }

    public static WordReviewEntity fuzzyPass(WordReviewEntity review, long now) {
        int cycle = review.getStudyCycle();
        long interval = SRSConfig.getInterval(cycle);
        review.setStatus(SRSConfig.STATUS_COMPLETED);
        review.setNextReviewTime(now + interval);
        review.setUpdateTime(now);
        return review;
    }

    public static WordReviewEntity fail(WordReviewEntity review, long now) {
        review.setStatus(SRSConfig.STATUS_STUDYING);
        review.setStudyCycle(0);
        review.setNextReviewTime(0);
        review.setLapses(review.getLapses() + 1);
        review.setUpdateTime(now);
        return review;
    }

    public static WordReviewEntity delete(WordReviewEntity review, long now) {
        review.setStatus(SRSConfig.STATUS_DELETED);
        review.setUpdateTime(now);
        return review;
    }

    public static WordReviewEntity createNewReview(String wordId, String bookId, long now) {
        return new WordReviewEntity(
                0, wordId, bookId,
                SRSConfig.STATUS_STUDYING, 0, 0, 0,
                now, now
        );
    }

    public static boolean isDue(WordReviewEntity review, long now) {
        return review.getStatus() == SRSConfig.STATUS_COMPLETED
                && review.getNextReviewTime() > 0
                && review.getNextReviewTime() <= now;
    }

    public static boolean isMastered(WordReviewEntity review) {
        return review.getStatus() == SRSConfig.STATUS_COMPLETED
                && review.getStudyCycle() >= SRSConfig.MASTERED_THRESHOLD;
    }

    public static long previewNextReviewTime(int studyCycle, long now) {
        return now + SRSConfig.getInterval(studyCycle);
    }

    public static String previewIntervalDisplay(int studyCycle) {
        return SRSConfig.getIntervalDisplay(studyCycle);
    }
}