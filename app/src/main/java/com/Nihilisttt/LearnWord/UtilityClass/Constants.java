package com.Nihilisttt.LearnWord.UtilityClass;

import android.util.Log;

public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static final int SMALL = 0;
    public static final int NORMAL = 1;
    public static final int LARGE = 2;
    public static final float LARGE_KANJI = 32f;
    public static final float LARGE_KANA = 16f;
    public static final float NORMAL_KANJI = 18f;
    public static final float NORMAL_KANA = 9f;
    public static final float SMALL_KANJI = 8f;
    public static final float SMALL_KANA = 4f;
    public static final float LARGE_SMALL_KANJI = 30f;
    public static final float LARGE_SMALL_KANA = 18f;
    public static final float NORMAL_SMALL_KANJI = 16.875f;
    public static final float NORMAL_SMALL_KANA = 10.125f;
    public static final float SMALL_SMALL_KANJI = 7.5f;
    public static final float SMALL_SMALL_KANA = 4.5f;
    public static final float COLLOCATION_ROW_MARGIN_START = 10f;
    public static final float SENTENCE_ROW_MARGIN_START = 10f;
    public static final float BASIC_WORD_LAYOUT_MARGIN_START = 24f;
    public static final int SHOW_SENTENCE_POPUP = 0;
    public static final int TURN_TO_DETAIL_PAGE = 1;

    public static final int FONT_SIZE_SMALL = 0;
    public static final int FONT_SIZE_NORMAL = 1;
    public static final int FONT_SIZE_LARGE = 2;

    private static final float[] KANJI_SIZES = {14f, 18f, 24f};
    private static final float[] KANA_SIZES = {7f, 9f, 12f};
    private static final float[] DEFINITION_SIZES = {12f, 14f, 17f};
    private static final float[] SENTENCE_KANJI_SIZES = {14f, 18f, 22f};
    private static final float[] SENTENCE_KANA_SIZES = {7f, 9f, 11f};

    public static float getKanjiSize(int fontSizeLevel) {
        return KANJI_SIZES[Math.max(0, Math.min(2, fontSizeLevel))];
    }

    public static float getKanaSize(int fontSizeLevel) {
        return KANA_SIZES[Math.max(0, Math.min(2, fontSizeLevel))];
    }

    public static float getDefinitionSize(int fontSizeLevel) {
        return DEFINITION_SIZES[Math.max(0, Math.min(2, fontSizeLevel))];
    }

    public static float getSentenceKanjiSize(int fontSizeLevel) {
        return SENTENCE_KANJI_SIZES[Math.max(0, Math.min(2, fontSizeLevel))];
    }

    public static float getSentenceKanaSize(int fontSizeLevel) {
        return SENTENCE_KANA_SIZES[Math.max(0, Math.min(2, fontSizeLevel))];
    }

    public static enum PartOfSpeech {
        NOUN("n."), VERB("v."), ADJECTIVE("adj."), ADVERB("adv."), UNKNOWN("");

        private final String abbreviation;

        PartOfSpeech(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {
            return abbreviation;
        }
    }

    public static float getKanaLength(String kanaString) {
        float kanaLength = 0;
        if (kanaString.equals("")) {
            return kanaLength;
        }
        char[] chars = kanaString.toCharArray();
        for (char kana : chars) {
            kanaLength = Judge.isSmallKana(kana) ? (kanaLength + 0.8f) : (kanaLength + 1f);
        }
        return kanaLength;
    }
}
