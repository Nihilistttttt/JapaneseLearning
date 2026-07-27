package com.Nihilisttt.LearnWord.UtilityClass;

import com.Nihilisttt.LearnWord.R;

public class Select {
    public static layoutParams selectLayout(int layoutType) {
        layoutParams lp = new layoutParams();
        lp.layoutType = layoutType;
        int clamped = Math.max(0, Math.min(Constants.FONT_SIZE_LARGE, layoutType));

        lp.kanji_size = Constants.getWordKanjiSize(clamped);
        lp.kana_size = Constants.getWordKanaSize(clamped);

        if (clamped <= 1) {
            lp.layout_1 = R.layout.view_small_char;
            lp.layout_2 = R.layout.view_small_small_char;
            lp.kanji_id_1 = R.id.small_kanji_text;
            lp.kanji_id_2 = R.id.small_small_kanji_text;
            lp.kana_id_1 = R.id.small_kana_text;
            lp.kana_id_2 = R.id.small_small_kana_text;
        } else if (clamped <= 3) {
            lp.layout_1 = R.layout.view_normal_char;
            lp.layout_2 = R.layout.view_normal_small_char;
            lp.kanji_id_1 = R.id.normal_kanji_text;
            lp.kanji_id_2 = R.id.normal_small_kanji_text;
            lp.kana_id_1 = R.id.normal_kana_text;
            lp.kana_id_2 = R.id.normal_small_kana_text;
        } else {
            lp.layout_1 = R.layout.view_large_char;
            lp.layout_2 = R.layout.view_large_small_char;
            lp.kanji_id_1 = R.id.large_kanji_text;
            lp.kanji_id_2 = R.id.large_small_kanji_text;
            lp.kana_id_1 = R.id.large_kana_text;
            lp.kana_id_2 = R.id.large_small_kana_text;
        }

        float t = (float) clamped / Constants.FONT_SIZE_LARGE;
        lp.currentIsSmallKanaMarginStart = t * 1.5f;
        lp.previousIsSmallKanaMarginStart = t * 1.5f;
        lp.elseMarginStart = t * 1.5f;

        return lp;
    }

    public static class layoutParams {
        private int layoutType;
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

        public int getLayoutType() {
            return layoutType;
        }

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
