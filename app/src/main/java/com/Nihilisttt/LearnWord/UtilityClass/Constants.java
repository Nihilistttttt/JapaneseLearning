package com.Nihilisttt.LearnWord.UtilityClass;

import android.util.Log;

public class Constants {
    private Constants() {
        throw new UnsupportedOperationException("Cannot instantiate utility class");
    }

    public static final int SMALL = 0;       // 小尺寸
    public static final int NORMAL = 1;      // 正常尺寸
    public static final int LARGE = 2;       // 大尺寸

    public static final float COLLOCATION_ROW_MARGIN_START = 10f;  // 词组搭配行起始margin(dp)
    public static final float SENTENCE_ROW_MARGIN_START = 10f;     // 例句行起始margin(dp)
    public static final float BASIC_WORD_LAYOUT_MARGIN_START = 24f; // 主页面单词布局起始margin(dp)
    public static final int SHOW_SENTENCE_POPUP = 0;   // 释义模式：弹窗显示例句
    public static final int TURN_TO_DETAIL_PAGE = 1;   // 释义模式：跳转详情页

    // CardView卡片样式
    public static final float CARD_RADIUS_DP = 12f;            // 卡片圆角(dp)
    public static final float CARD_ELEVATION_DP = 1f;         // 卡片阴影(dp)
    public static final float CARD_CONTENT_PADDING_DP = 8f;    // 卡片内容内边距(dp)
    public static final float CARD_MARGIN_DP = 12f;            // 卡片外边距(dp)

    // 分区样式
    public static final float SECTION_DIVIDER_HEIGHT_DP = 8f;          // 分区分隔线高度(dp)
    public static final float SECTION_TITLE_BOTTOM_MARGIN_DP = 4f;     // 分区标题底部margin(dp)
    public static final float SENTENCE_ITEM_MARGIN_DP = 8f;            // 例句条目间距(dp)
    public static final float COLLOCATION_ROW_MARGIN_DP = 8f;          // 词组搭配行间距(dp)

    // BottomSheet详情页样式
    public static final float HEADER_PADDING_H_DP = 16f;              // header左右padding(dp)
    public static final float HEADER_PADDING_BOTTOM_DP = 4f;          // header底部padding(dp)
    public static final float ROOT_TOP_PADDING_DP = 8f;               // 根布局顶部padding(dp)
    public static final float TOOLBAR_HEIGHT_DP = 56f;                // 工具栏高度(dp)
    public static final float TAB_MIN_WIDTH_DP = 40f;                 // Tab最小宽度(dp)
    public static final float BOTTOM_SHEET_HEIGHT_RATIO = 0.92f;      // BottomSheet高度占屏幕比例
    public static final float MEANING_BOTTOM_MARGIN_DP = 8f;          // 释义底部margin(dp)

    // 弹窗样式
    public static final float POPUP_WIDTH_RATIO = 0.95f;      // 弹窗宽度占屏幕比例
    public static final float POPUP_ELEVATION_DP = 16f;       // 弹窗阴影(dp)
    public static final float POPUP_RADIUS_DP = 16f;          // 弹窗圆角(dp)
    public static final float POPUP_PADDING_DP = 16f;         // 弹窗内容内边距(dp)
    public static final float POPUP_Y_OFFSET_DP = 8f;         // 弹窗Y偏移量(dp)

    // 可用宽度计算
    public static final float AVAILABLE_WIDTH_DEDUCTION_DP = 40f;              // 例句可用宽度扣除量(dp)
    public static final float BASIC_WORD_AVAILABLE_WIDTH_DEDUCTION_DP = 32f;  // 单词可用宽度扣除量(dp)

    // 字号级别
    public static final int FONT_SIZE_SMALL = 0;    // 最小字号级别
    public static final int FONT_SIZE_NORMAL = 3;   // 正常字号级别
    public static final int FONT_SIZE_LARGE = 6;    // 最大字号级别
    public static final int FONT_SIZE_COUNT = 7;    // 字号级别总数

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

    public static int getSentenceCardMinSentences(int subLevel) {
        if (subLevel >= 3) return 1;
        if (subLevel >= 2) return 2;
        return 3;
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
