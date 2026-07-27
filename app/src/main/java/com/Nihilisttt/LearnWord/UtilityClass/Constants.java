package com.Nihilisttt.LearnWord.UtilityClass;

import android.util.Log;

public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static final int SMALL = 0;
    public static final int NORMAL = 1;
    public static final int LARGE = 2;

    public static final float COLLOCATION_ROW_MARGIN_START = 10f;
    public static final float SENTENCE_ROW_MARGIN_START = 10f;
    public static final float BASIC_WORD_LAYOUT_MARGIN_START = 24f;
    public static final int SHOW_SENTENCE_POPUP = 0;
    public static final int TURN_TO_DETAIL_PAGE = 1;

    public static final int FONT_SIZE_SMALL = 0;
    public static final int FONT_SIZE_NORMAL = 3;
    public static final int FONT_SIZE_LARGE = 6;
    public static final int FONT_SIZE_COUNT = 7;

    private static final float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private static final float[] WORD_KANJI_RANGE = {8f, 32f};
    private static final float[] WORD_KANA_RANGE = {4f, 16f};
    private static final float[] WORD_SMALL_KANJI_RANGE = {7.5f, 30f};
    private static final float[] WORD_SMALL_KANA_RANGE = {4.5f, 18f};
    private static final float[] SUB_DEFINITION_RANGE = {12f, 17f};

    public static float getWordKanjiSize(int level) {
        float t = (float) Math.max(0, Math.min(FONT_SIZE_LARGE, level)) / FONT_SIZE_LARGE;
        return lerp(WORD_KANJI_RANGE[0], WORD_KANJI_RANGE[1], t);
    }

    public static float getWordKanaSize(int level) {
        float t = (float) Math.max(0, Math.min(FONT_SIZE_LARGE, level)) / FONT_SIZE_LARGE;
        return lerp(WORD_KANA_RANGE[0], WORD_KANA_RANGE[1], t);
    }

    public static float getWordSmallKanjiSize(int level) {
        float t = (float) Math.max(0, Math.min(FONT_SIZE_LARGE, level)) / FONT_SIZE_LARGE;
        return lerp(WORD_SMALL_KANJI_RANGE[0], WORD_SMALL_KANJI_RANGE[1], t);
    }

    public static float getWordSmallKanaSize(int level) {
        float t = (float) Math.max(0, Math.min(FONT_SIZE_LARGE, level)) / FONT_SIZE_LARGE;
        return lerp(WORD_SMALL_KANA_RANGE[0], WORD_SMALL_KANA_RANGE[1], t);
    }

    public static float getSubDefinitionSize(int level) {
        float t = (float) Math.max(0, Math.min(FONT_SIZE_LARGE, level)) / FONT_SIZE_LARGE;
        return lerp(SUB_DEFINITION_RANGE[0], SUB_DEFINITION_RANGE[1], t);
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
