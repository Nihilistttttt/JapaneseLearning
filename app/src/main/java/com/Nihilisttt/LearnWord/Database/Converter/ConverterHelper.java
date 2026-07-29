package com.Nihilisttt.LearnWord.Database.Converter;

import android.util.Log;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ConverterHelper {
    private ConverterHelper() {}

    public static <M, E> M entityToModel(E entity, String tag, String modelName,
                                           Function<E, M> converter, Supplier<M> defaultSupplier) {
        if (entity == null) {
            Log.d(tag, "输入" + modelName + "Entity为null");
            return defaultSupplier.get();
        }
        try {
            return converter.apply(entity);
        } catch (Exception e) {
            Log.d(tag, "转换" + modelName + "Entity失败", e);
            return defaultSupplier.get();
        }
    }

    public static <M, E> E modelToEntity(M model, String tag, String modelName,
                                           Function<M, E> converter) {
        if (model == null) {
            throw new IllegalArgumentException(modelName + "不能为null");
        }
        try {
            return converter.apply(model);
        } catch (Exception e) {
            Log.d(tag, modelName + "转Entity失败", e);
            throw new RuntimeException("转换失败", e);
        }
    }
}