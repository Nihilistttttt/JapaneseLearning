package com.Nihilisttt.LearnWord.Database.Converter;

import com.Nihilisttt.LearnWord.UtilityClass.AppLog;

import java.util.function.Function;
import java.util.function.Supplier;

public final class ConverterHelper {
    private ConverterHelper() {}

    public static <M, E> M entityToModel(E entity, String tag, String modelName,
                                           Function<E, M> converter, Supplier<M> defaultSupplier) {
        if (entity == null) {
            AppLog.d(tag, modelName + "Entity为null, 返回default");
            return defaultSupplier.get();
        }
        try {
            return converter.apply(entity);
        } catch (Exception e) {
            AppLog.e(tag, modelName + "Entity转换失败", e);
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
            AppLog.e(tag, modelName + "转Entity失败", e);
            throw new RuntimeException("转换失败", e);
        }
    }
}
