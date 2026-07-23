package com.Nihilisttt.LearnWord.UtilityClass;

import android.util.Log;

import java.util.HashSet;

public class Constants {
    // 私有构造函数，防止实例化
    private Constants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    // 常量
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

    public static enum PartOfSpeech {
        NOUN, VERB, ADJECTIVE, ADVERB, UNKNOWN
    }

    public static float getKanaLength(String kanaString) {
        float kanaLength = 0;
        if (kanaString.equals("")) {
            Log.d("Constants.getKanaLength", "KanaLength: " + kanaLength);
            return kanaLength;
        }
        char[] chars = kanaString.toCharArray();
        for (char kana : chars) {
            kanaLength = Judge.isSmallKana(kana) ? (kanaLength + 0.8f) : (kanaLength + 1f);
        }
        Log.d("Constants.getKanaLength", "KanaLength: " + kanaLength);
        return kanaLength;
    }
}