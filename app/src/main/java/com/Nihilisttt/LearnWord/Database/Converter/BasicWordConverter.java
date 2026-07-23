package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;

import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.google.gson.Gson;

import java.util.Collections;

public class BasicWordConverter {
    private static final Gson gson = new Gson();
    private static final String TAG = "BasicWordConverter"; // 统一日志标签

    // 缓存默认对象减少GC压力
    private static final BasicWord DEFAULT_BASIC_WORD = createDefaultBasicWord();

    private BasicWordConverter() {
        // 防止实例化
    }

    public static BasicWord BasicWordEntityToBasicWord(BasicWordEntity entity) {
        if (entity == null) {
            Log.d(TAG, "输入BasicWordEntity为null，返回默认BasicWord");
            return DEFAULT_BASIC_WORD;
        }

        try {
            BasicWord model = new BasicWord.Builder()
                    .wordId(entity.getWordId())
                    .kanjiComponents(Convert.jsonToList(entity.getKanjiComponents()))
                    .kanaComponents(Convert.jsonToList(entity.getKanaComponents()))
                    .audioUrl(entity.getAudioUrl())
                    .accentMark(entity.getAccentMark())
                    .mnemonic(entity.getMnemonic())
                    .build();

            Log.d(TAG, "成功转换BasicWordEntity ID: " + entity.getWordId());
            return model;
        } catch (Exception e) {
            Log.d(TAG, "转换BasicWordEntity失败, ID: " + entity.getWordId(), e); // 使用Log.d记录异常
            return DEFAULT_BASIC_WORD;
        }
    }

    public static BasicWordEntity BasicWordToBasicWordEntity(BasicWord model) {
        if (model == null) {
            Log.d(TAG, "输入BasicWord为null，抛出异常");
            throw new IllegalArgumentException("BasicWord不能为null");
        }

        try {
            BasicWordEntity entity = new BasicWordEntity(
                    model.getWordId(),
                    Convert.listToJson(model.getKanjiComponents()),
                    Convert.listToJson(model.getKanaComponents()),
                    model.getAudioUrl(),
                    model.getAccentMark(),
                    model.getMnemonic()
                    );
            Log.d(TAG, "成功转换BasicWord为BasicWordEntity ID: " + model.getWordId());
            return entity;
        } catch (Exception e) {
            Log.d(TAG, "BasicWord转WordEntity失败, ID: " + model.getWordId(), e);
            throw new RuntimeException("转换失败", e);
        }
    }


    private static BasicWord createDefaultBasicWord() {
        Log.d(TAG, "创建默认BasicWord实例");
        return new BasicWord.Builder()
                .wordId("null")
                .kanjiComponents(Collections.singletonList("null"))
                .kanaComponents(Collections.singletonList(""))
                .audioUrl("")
                .accentMark("")
                .mnemonic("")
                .build();
    }
    // endregion
}
