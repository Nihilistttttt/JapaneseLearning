package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;


import com.Nihilisttt.LearnWord.Database.Entities.SynonymWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.google.gson.Gson;

import java.util.Collections;

public class SynonymWordConverter {
    private static final Gson gson = new Gson();
    private static final String TAG = "SynonymWordConverter"; // 统一日志标签

    private static final SynonymWord DEFAULT_SYNONYM_WORD = createDefaultSynonymWord();

    private SynonymWordConverter() {
    }

    public static SynonymWord SynonymWordEntityToSynonymWord(SynonymWordEntity entity) {
        if (entity == null) {
            Log.d(TAG, "输入SynonymWordEntity为null，返回默认SynonymWord");
            return DEFAULT_SYNONYM_WORD;
        }

        try {
            SynonymWord model = new SynonymWord.Builder()
                    .synonymWordId(entity.getSynonymWordId())
                    .wordId(entity.getWordId())
                    .correspondingWordId(entity.getCorrespondingWordId())
                    .kanjiComponents(Convert.jsonToList(entity.getKanjiComponents()))
                    .kanaComponents(Convert.jsonToList(entity.getKanaComponents()))
                    .build();

            Log.d(TAG, "成功转换SynonymWordEntity ID: " + entity.getSynonymWordId());
            return model;
        } catch (Exception e) {
            Log.d(TAG, "转换SynonymWordEntity失败, ID: " + entity.getSynonymWordId(), e);
            return DEFAULT_SYNONYM_WORD;
        }
    }

    public static SynonymWordEntity SynonymWordToSynonymWordEntity(SynonymWord model) {
        if (model == null) {
            Log.d(TAG, "输入SynonymWord为null，抛出异常");
            throw new IllegalArgumentException("SynonymWord不能为null");
        }

        try {
            SynonymWordEntity entity = new SynonymWordEntity(
                    model.getSynonymWordId(),
                    model.getWordId(),
                    model.getCorrespondingWordId(),
                    Convert.listToJson(model.getKanjiComponents()),
                    Convert.listToJson(model.getKanaComponents())
                    );
            Log.d(TAG, "成功转换SynonymWord为SynonymWordEntity ID: " + model.getSynonymWordId());
            return entity;
        } catch (Exception e) {
            Log.d(TAG, "SynonymWord转SynonymWordEntity失败, ID: " + model.getSynonymWordId(), e);
            throw new RuntimeException("转换失败", e);
        }
    }


    private static SynonymWord createDefaultSynonymWord() {
        Log.d(TAG, "创建默认SynonymWord实例");
        return new SynonymWord.Builder()
                .synonymWordId("null")
                .wordId("null")
                .correspondingWordId("null")
                .kanjiComponents(Collections.singletonList("null"))
                .kanaComponents(Collections.singletonList("null"))
                .build();
    }
    // endregion
}
