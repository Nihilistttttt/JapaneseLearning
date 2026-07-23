package com.Nihilisttt.LearnWord.UtilityClass;

import com.Nihilisttt.LearnWord.R;

public class Select {
    public static layoutParams selectLayout(int layoutType) {
        layoutParams LayoutParams = new layoutParams();
        switch (layoutType) {
            case Constants.LARGE:
                LayoutParams.layout_1 = R.layout.view_large_char;
                LayoutParams.layout_2 = R.layout.view_large_small_char;
                LayoutParams.kanji_id_1 = R.id.large_kanji_text;
                LayoutParams.kanji_id_2 = R.id.large_small_kanji_text;
                LayoutParams.kana_id_1 = R.id.large_kana_text;
                LayoutParams.kana_id_2 = R.id.large_small_kana_text;
                LayoutParams.kanji_size = Constants.LARGE_KANJI;
                LayoutParams.kana_size = Constants.LARGE_KANA;
                LayoutParams.currentIsSmallKanaMarginStart = -2f;
                LayoutParams.previousIsSmallKanaMarginStart = 0f;
                LayoutParams.elseMarginStart = 1f;
                break;
            case Constants.NORMAL:
                LayoutParams.layout_1 = R.layout.view_normal_char;
                LayoutParams.layout_2 = R.layout.view_normal_small_char;
                LayoutParams.kanji_id_1 = R.id.normal_kanji_text;
                LayoutParams.kanji_id_2 = R.id.normal_small_kanji_text;
                LayoutParams.kana_id_1 = R.id.normal_kana_text;
                LayoutParams.kana_id_2 = R.id.normal_small_kana_text;
                LayoutParams.kanji_size = Constants.NORMAL_KANJI;
                LayoutParams.kana_size = Constants.NORMAL_KANA;
                LayoutParams.currentIsSmallKanaMarginStart = -1f;
                LayoutParams.previousIsSmallKanaMarginStart = 0f;
                LayoutParams.elseMarginStart = 0.5f;
                break;
            case Constants.SMALL:
                LayoutParams.layout_1 = R.layout.view_small_char;
                LayoutParams.layout_2 = R.layout.view_small_small_char;
                LayoutParams.kanji_id_1 = R.id.small_kanji_text;
                LayoutParams.kanji_id_2 = R.id.small_small_kanji_text;
                LayoutParams.kana_id_1 = R.id.small_kana_text;
                LayoutParams.kana_id_2 = R.id.small_small_kana_text;
                LayoutParams.kanji_size = Constants.SMALL_KANJI;
                LayoutParams.kana_size = Constants.SMALL_KANA;
                LayoutParams.currentIsSmallKanaMarginStart = -0.5f;
                LayoutParams.previousIsSmallKanaMarginStart = 0f;
                LayoutParams.elseMarginStart = 0f;
                break;
        }
        return LayoutParams;
    }

    public static class layoutParams {
        private int layout_1;
        private int layout_2;
        private int kanji_id_1;
        private int kanji_id_2;
        private int kana_id_1;
        private int kana_id_2;
        private float kanji_size;
        private float kana_size;
        private float currentIsSmallKanaMarginStart;
        private float previousIsSmallKanaMarginStart;
        private float elseMarginStart;

        public int getLayout_1() {
            return layout_1;
        }

        public int getLayout_2() {
            return layout_2;
        }

        public int getKanjiId_1() {
            return kanji_id_1;
        }

        public int getKanjiId_2() {
            return kanji_id_2;
        }

        public int getKanaId_1() {
            return kana_id_1;
        }

        public int getKanaId_2() {
            return kana_id_2;
        }

        public float getKanjiSize() {
            return kanji_size;
        }

        public float getKanaSize() {
            return kana_size;
        }

        public float getCurrentIsSmallKanaMarginStart() {
            return currentIsSmallKanaMarginStart;
        }

        public float getPreviousIsSmallKanaMarginStart() {
            return previousIsSmallKanaMarginStart;
        }

        public float getElseMarginStart() {
            return elseMarginStart;
        }
    }


}
