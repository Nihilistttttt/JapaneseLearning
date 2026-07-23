package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;

import com.Nihilisttt.LearnWord.Database.Entities.AntonymWordEntity;
import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.UtilityClass.Convert;
import com.google.gson.Gson;
import java.util.Collections;

public class AntonymWordConverter {
    private static final Gson gson = new Gson();
    private static final String TAG = "AntonymWordConverter"; // 统一日志标签

    // 缓存默认对象减少GC压力
//    private static final AntonymWord DEFAULT_ANTONYM_WORD = createDefaultAntonymWord();

    private AntonymWordConverter() {
        // 防止实例化
    }

    public static AntonymWord AntonymWordEntityToAntonymWord(AntonymWordEntity entity) {
        if (entity == null) {
            Log.d(TAG, "输入AntonymWordEntity为null，返回默认AntonymWord");
            return null;
//            return DEFAULT_ANTONYM_WORD;
        }

        try {
            AntonymWord model = new AntonymWord.Builder()
                    .antonymWordId(entity.getAntonymWordId())
                    .wordId(entity.getWordId())
                    .correspondingWordId(entity.getCorrespondingWordId())
                    .kanjiComponents(Convert.jsonToList(entity.getKanjiComponents()))
                    .kanaComponents(Convert.jsonToList(entity.getKanaComponents()))
                    .build();

            Log.d(TAG, "成功转换AntonymWordEntity ID: " + entity.getAntonymWordId());
            return model;
        } catch (Exception e) {
            Log.d(TAG, "转换AntonymWordEntity失败, ID: " + entity.getAntonymWordId(), e); // 使用Log.d记录异常
//            return DEFAULT_ANTONYM_WORD;
            return null;
        }
    }

    public static AntonymWordEntity AntonymWordToAntonymWordEntity(AntonymWord model) {
        if (model == null) {
            Log.d(TAG, "输入AntonymWord为null，抛出异常");
            throw new IllegalArgumentException("AntonymWord不能为null");
        }

        try {
            AntonymWordEntity entity = new AntonymWordEntity(
                    model.getAntonymWordId(),
                    model.getWordId(),
                    model.getCorrespondingWordId(),
                    Convert.listToJson(model.getKanjiComponents()),
                    Convert.listToJson(model.getKanaComponents())
                    );
            Log.d(TAG, "成功转换AntonymWord为AntonymWordEntity ID: " + model.getAntonymWordId());
            return entity;
        } catch (Exception e) {
            Log.d(TAG, "AntonymWord转AntonymWordEntity失败, ID: " + model.getAntonymWordId(), e);
            throw new RuntimeException("转换失败", e);
        }
    }


//    private static AntonymWord createDefaultAntonymWord() {
//        Log.d(TAG, "创建默认AntonymWord实例");
//        return new AntonymWord.Builder()
//                .antonymWordId("null")
//                .wordId("null")
//                .correspondingWordId("null")
//                .kanjiComponents(Collections.singletonList("null"))
//                .kanaComponents(Collections.singletonList(""))
//                .build();
//    }
    // endregion
}
