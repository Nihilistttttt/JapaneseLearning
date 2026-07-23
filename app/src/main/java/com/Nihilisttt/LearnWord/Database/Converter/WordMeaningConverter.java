package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;

import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.UtilityClass.Constants;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.google.gson.Gson;

public class WordMeaningConverter {
    private static final Gson gson = new Gson();
    private static final String TAG = "WordMeaningConverter"; // 统一日志标签

    // 缓存默认对象减少GC压力
    private static final WordMeaning DEFAULT_WORD_MEANING = createDefaultWordMeaning();

    private WordMeaningConverter() {
        // 防止实例化
    }

    public static WordMeaning WordMeaningEntityToWordMeaning(WordMeaningEntity entity) {
        if (entity == null) {
            Log.d(TAG, "输入WordMeaningEntity为null，返回默认WordMeaning");
            return DEFAULT_WORD_MEANING;
        }

        try {
            WordMeaning model =new WordMeaning.Builder()
                    .wordMeaningId(entity.getWordMeaningId())
                    .partOfSpeech(Convert.jsonToPartOfSpeech(entity.getPartOfSpeech()))
                    .originalDefinition(entity.getOriginalDefinition())
                    .translationDefinition(entity.getTranslationDefinition())
                    .build();

            Log.d(TAG, "成功转换WordMeaningEntity ID: " + entity.getWordMeaningId());
            return model;
        } catch (Exception e) {
            Log.d(TAG, "转换WordMeaningEntity失败, ID: " + entity.getWordMeaningId(), e); // 使用Log.d记录异常
            return DEFAULT_WORD_MEANING;
        }
    }

    public static WordMeaningEntity WordMeaningToWordMeaningEntity(WordMeaning model) {
        if (model == null) {
            Log.d(TAG, "输入WordMeaning为null，抛出异常");
            throw new IllegalArgumentException("WordMeaning不能为null");
        }

        try {
            WordMeaningEntity entity =new WordMeaningEntity(model.getWordMeaningId(),
                    model.getWordId(),
                    model.getOriginalDefinition(),
                    model.getTranslationDefinition(),
                    Convert.partOfSpeechToJson(model.getPartOfSpeech()));
            Log.d(TAG, "成功转换WordMeaning为WordMeaningEntity ID: " + model.getWordMeaningId());
            return entity;
        } catch (Exception e) {
            Log.d(TAG, "WordMeaning转WordMeaningEntity失败, ID: " + model.getWordMeaningId(), e);
            throw new RuntimeException("转换失败", e);
        }
    }


    private static WordMeaning createDefaultWordMeaning() {
        Log.d(TAG, "创建默认WordMeaning实例");
        return new WordMeaning.Builder()
                .wordMeaningId("null")
                .wordId("null")
                .originalDefinition("null")
                .translationDefinition("null")
                .partOfSpeech(Constants.PartOfSpeech.UNKNOWN)
                .build();
    }
    // endregion
}
