package com.Nihilisttt.LearnWord.UtilityClass;


public class Select {
    public static layoutParams selectLayout(int layoutType) {
        layoutParams lp = new layoutParams();
        lp.layoutType = layoutType;
        int clamped = Math.max(0, Math.min(Constants.FONT_SIZE_LARGE, layoutType));

        lp.kanji_size = Constants.getWordKanjiSize(clamped);
        lp.kana_size = Constants.getWordKanaSize(clamped);

        float t = (float) clamped / Constants.FONT_SIZE_LARGE;
        lp.currentIsSmallKanaMarginStart = t * 1.5f;
        lp.previousIsSmallKanaMarginStart = t * 1.5f;
        lp.elseMarginStart = t * 1.5f;

        return lp;
    }

    public static class layoutParams {
        private int layoutType;
        private float kanji_size;
        private float kana_size;
        private float currentIsSmallKanaMarginStart;
        private float previousIsSmallKanaMarginStart;
        private float elseMarginStart;

        public int getLayoutType() {
            return layoutType;
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
