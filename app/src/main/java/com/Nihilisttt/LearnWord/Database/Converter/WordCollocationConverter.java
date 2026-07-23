package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;

import com.Nihilisttt.LearnWord.Database.Entities.WordCollocationEntity;
import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.google.gson.Gson;

import java.util.Collections;

public class WordCollocationConverter {
    private static final Gson gson = new Gson();
    private static final String TAG = "WordCollocationConverter"; // 统一日志标签

    // 缓存默认对象减少GC压力
    private static final WordCollocation DEFAULT_WORD_COLLOCATION = createDefaultWordCollocation();

    private WordCollocationConverter() {
        // 防止实例化
    }

    public static WordCollocation WordCollocationEntityToWordCollocation(WordCollocationEntity entity) {
        if (entity == null) {
            Log.d(TAG, "输入WordCollocation为null，返回默认WordCollocation");
            return DEFAULT_WORD_COLLOCATION;
        }

        try {
            WordCollocation model = new WordCollocation.Builder()
                    .wordCollocationId(entity.getWordCollocationId())
                    .wordId(entity.getWordId())
                    .kanjiComponents(Convert.jsonToNestedList(entity.getKanjiComponents()))
                    .kanaComponents(Convert.jsonToNestedList(entity.getKanaComponents()))
                    .wordIdList(Convert.jsonToList(entity.getWordIdList()))
                    .translation(entity.getTranslation())
                    .source(entity.getSource())
                    .collocationAudioUrl(entity.getAudioUrl())
                    .build();

            Log.d(TAG, "成功转换WordCollocationEntity ID: " + entity.getWordCollocationId());
            return model;
        } catch (Exception e) {
            Log.d(TAG, "转换WordCollocationEntity失败, ID: " + entity.getWordCollocationId(), e); // 使用Log.d记录异常
            return DEFAULT_WORD_COLLOCATION;
        }
    }

    public static WordCollocationEntity WordCollocationToWordCollocationEntity(WordCollocation model) {
        if (model == null) {
            Log.d(TAG, "输入WordCollocation为null，抛出异常");
            throw new IllegalArgumentException("WordCollocation不能为null");
        }

        try {
            WordCollocationEntity entity = new WordCollocationEntity(
                    model.getWordCollocationId(),
                    model.getWordId(),
                    Convert.nestedListToJson(model.getKanjiComponents()),
                    Convert.nestedListToJson(model.getKanaComponents()),
                    Convert.listToJson(model.getWordIdList()),
                    model.getTranslation(),
                    model.getSource(),
                    model.getAudioUrl()
            );
            Log.d(TAG, "成功转换WordCollocation为WordCollocationEntity ID: " + model.getWordCollocationId());
            return entity;
        } catch (Exception e) {
            Log.d(TAG, "WordCollocation转WordCollocationEntity失败, ID: " + model.getWordCollocationId(), e);
            throw new RuntimeException("转换失败", e);
        }
    }


    private static WordCollocation createDefaultWordCollocation() {
        Log.d(TAG, "创建默认WordCollocation实例");
        return new WordCollocation.Builder()
                .wordCollocationId("null")
                .wordId("null")
                .kanjiComponents(Collections.singletonList(Collections.singletonList("null")))
                .kanaComponents(Collections.singletonList(Collections.singletonList("")))
                .wordIdList(Collections.singletonList("null"))
                .collocationAudioUrl("")
                .translation("null")
                .source("null")
                .build();
    }
    // endregion
}
