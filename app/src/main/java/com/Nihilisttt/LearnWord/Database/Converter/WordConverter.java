package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;

import com.Nihilisttt.LearnWord.Database.Entities.WordEntity;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.google.gson.Gson;

import java.util.Collections;

public class WordConverter {
    private static final Gson gson = new Gson();
    private static final String TAG = "WordConverter"; // 统一日志标签

    // 缓存默认对象减少GC压力
    private static final Word DEFAULT_WORD = createDefaultWord();

    private WordConverter() {
        // 防止实例化
    }

    public static Word WordEntityToWord(WordEntity entity) {
        if (entity == null) {
            Log.d(TAG, "输入WordEntity为null，返回默认Word");
            return DEFAULT_WORD;
        }

        try {
            Word model = new Word.Builder()
                    .addWordId(entity.getWordId())
                    .withAntonymIdList(Convert.jsonToList(entity.getAntonymWordIdList()))
                    .withSynonymIdList(Convert.jsonToList(entity.getSynonymWordIdList()))
                    .withCollocationIdList(Convert.jsonToList(entity.getCollocationIdList()))
                    .withMeaningIdList(Convert.jsonToList(entity.getMeaningIdList()))
                    .withSentenceIdList(Convert.jsonToList(entity.getSentenceIdList()))
                    .build();
            Log.d(TAG, "成功转换WordEntity ID: " + entity.getWordId());
            return model;
        } catch (Exception e) {
            Log.d(TAG, "转换WordEntity失败, ID: " + entity.getWordId(), e); // 使用Log.d记录异常
            return DEFAULT_WORD;
        }
    }

    public static WordEntity WordToWordEntity(Word model) {
        if (model == null) {
            Log.d(TAG, "输入Word为null，抛出异常");
            throw new IllegalArgumentException("Word不能为null");
        }

        try {
            WordEntity entity = new WordEntity(
                    model.getWordId(),
                    Convert.listToJson(model.getAntonymIdList()),
                    Convert.listToJson(model.getSynonymIdList()),
                    Convert.listToJson(model.getCollocationIdList()),
                    Convert.listToJson(model.getMeaningIdList()),
                    Convert.listToJson(model.getSentenceIdList())
            );
            Log.d(TAG, "成功转换Word为WordEntity ID: " + model.getWordId());
            return entity;
        } catch (Exception e) {
            Log.d(TAG, "Word转WordEntity失败, ID: " + model.getWordId(), e);
            throw new RuntimeException("转换失败", e);
        }
    }


    private static Word createDefaultWord() {
        Log.d(TAG, "创建默认Word实例");
        return new Word.Builder()
                .addWordId("null")
                .withAntonymIdList(Collections.singletonList("null"))
                .withSynonymIdList(Collections.singletonList("null"))
                .withMeaningIdList(Collections.singletonList("null"))
                .withCollocationIdList(Collections.singletonList("null"))
                .withSentenceIdList(Collections.singletonList("null"))
                .build();
    }
    // endregion
}
