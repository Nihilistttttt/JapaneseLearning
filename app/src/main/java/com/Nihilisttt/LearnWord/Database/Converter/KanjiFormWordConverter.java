package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;

import com.Nihilisttt.LearnWord.Database.Entities.KanjiFormWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.KanjiFormWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.google.gson.Gson;

import java.util.Collections;

public class KanjiFormWordConverter {
    private static final Gson gson = new Gson();
    private static final String TAG = "KanjiFormWordConverter";

    private static final KanjiFormWord DEFAULT = createDefault();

    private KanjiFormWordConverter() {}

    public static KanjiFormWord KanjiFormWordEntityToKanjiFormWord(KanjiFormWordEntity entity) {
        if (entity == null) {
            Log.d(TAG, "输入KanjiFormWordEntity为null，返回默认KanjiFormWord");
            return DEFAULT;
        }
        try {
            return new KanjiFormWord.Builder()
                    .kanjiFormId(entity.getKanjiFormId())
                    .wordId(entity.getWordId())
                    .kanjiComponents(Convert.jsonToList(entity.getKanjiComponents()))
                    .kanaComponents(Convert.jsonToList(entity.getKanaComponents()))
                    .build();
        } catch (Exception e) {
            Log.d(TAG, "转换KanjiFormWordEntity失败, ID: " + entity.getKanjiFormId(), e);
            return DEFAULT;
        }
    }

    public static KanjiFormWordEntity KanjiFormWordToKanjiFormWordEntity(KanjiFormWord model) {
        if (model == null) throw new IllegalArgumentException("KanjiFormWord不能为null");
        try {
            return new KanjiFormWordEntity(
                    model.getKanjiFormId(),
                    model.getWordId(),
                    Convert.listToJson(model.getKanjiComponents()),
                    Convert.listToJson(model.getKanaComponents()));
        } catch (Exception e) {
            Log.d(TAG, "KanjiFormWord转KanjiFormWordEntity失败, ID: " + model.getKanjiFormId(), e);
            throw new RuntimeException("转换失败", e);
        }
    }

    private static KanjiFormWord createDefault() {
        return new KanjiFormWord.Builder()
                .kanjiFormId("null")
                .wordId("null")
                .kanjiComponents(Collections.singletonList("null"))
                .kanaComponents(Collections.singletonList("null"))
                .build();
    }
}