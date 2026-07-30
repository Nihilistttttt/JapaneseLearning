package com.Nihilisttt.LearnWord.UtilityClass;

import android.content.Context;
import android.content.SharedPreferences;


import com.Nihilisttt.LearnWord.Database.Dao.AntonymWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.BasicWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.SynonymWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordMeaningDao;
import com.Nihilisttt.LearnWord.Database.Database.WordDatabase;
import com.Nihilisttt.LearnWord.Database.Entities.AntonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.SynonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;
import com.Nihilisttt.LearnWord.UtilityClass.AppLog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.zip.GZIPInputStream;

public class DataImporter {
    private static final String TAG = "DataImporter";
    private static final String PREFS_NAME = "data_import_prefs";
    private static final String KEY_IMPORTED = "is_data_imported";
    private static final String KEY_VERSION = "import_data_version";
    private static final int CURRENT_VERSION = 10;
    private static final int BATCH_SIZE = 500;

    private DataImporter() {
    }

    public static boolean isImported(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_VERSION, 0) >= CURRENT_VERSION;
    }

    public static void importIfNeeded(Context context) {
        if (isImported(context)) {
            AppLog.d(TAG, "Already imported v" + CURRENT_VERSION + ", skipping");
            return;
        }
        importDataSync(context);
    }

    public static void importDataSync(Context context) {
        final CountDownLatch latch = new CountDownLatch(1);
        WordDatabase database = WordDatabase.getDatabase(context);
        WordDao wordDao = database.getWordDao();
        BasicWordDao basicWordDao = database.getBasicWordDao();
        WordMeaningDao wordMeaningDao = database.getWordMeaningDao();
        AntonymWordDao antonymWordDao = database.getAntonymWordDao();
        SynonymWordDao synonymWordDao = database.getSynonymWordDao();

        WordDatabase.databaseExecutor.execute(() -> {
            long startTime = System.currentTimeMillis();
            AppLog.d(TAG, "Starting import v" + CURRENT_VERSION + "...");

            try {
                JsonObject root = loadJson(context);
                if (root == null) {
                    AppLog.e(TAG, "Failed to load JSON from assets");
                    return;
                }

                database.runInTransaction(() -> {
                    wordDao.deleteAllWords();
                    basicWordDao.deleteAllBasicWords();
                    wordMeaningDao.deleteAllWordMeanings();
                    antonymWordDao.deleteAllAntonymWords();
                    synonymWordDao.deleteAllSynonymWords();

                    batchInsertWords(wordDao, root.getAsJsonArray("words"));
                    batchInsertBasicWords(basicWordDao, root.getAsJsonArray("basicWords"));
                    batchInsertWordMeanings(wordMeaningDao, root.getAsJsonArray("wordMeanings"));
                    batchInsertAntonymWords(antonymWordDao, root.getAsJsonArray("antonymWords"));
                    batchInsertSynonymWords(synonymWordDao, root.getAsJsonArray("synonymWords"));
                });

                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                prefs.edit().putInt(KEY_VERSION, CURRENT_VERSION).apply();

                long elapsed = System.currentTimeMillis() - startTime;
                AppLog.d(TAG, "Import completed: " + elapsed + "ms");

            } catch (Exception e) {
                AppLog.e(TAG, "Data import failed", e);
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();
            AppLog.d(TAG, "Import latch released");
        } catch (InterruptedException e) {
            AppLog.e(TAG, "Import interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    private static JsonObject loadJson(Context context) {
        try (InputStream is = context.getAssets().open("room_import_gzipped");
             GZIPInputStream gzis = new GZIPInputStream(is);
             InputStreamReader reader = new InputStreamReader(gzis, StandardCharsets.UTF_8)) {
            return new Gson().fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            AppLog.e(TAG, "Error reading room_import_gzipped", e);
            return null;
        }
    }

    private static void batchInsertWords(WordDao dao, JsonArray array) {
        if (array == null) return;
        List<WordEntity> batch = new ArrayList<>(BATCH_SIZE);
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            batch.add(new WordEntity(
                    obj.get("wordId").getAsString(),
                    obj.get("antonymWordIdList").getAsString(),
                    obj.get("synonymWordIdList").getAsString(),
                    obj.get("collocationIdList").getAsString(),
                    obj.get("meaningIdList").getAsString(),
                    obj.get("sentenceIdList").getAsString()
            ));
            if (batch.size() >= BATCH_SIZE) {
                dao.insertWords(batch.toArray(new WordEntity[0]));
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            dao.insertWords(batch.toArray(new WordEntity[0]));
        }
        AppLog.d(TAG, "Inserted " + array.size() + " words");
    }

    private static void batchInsertBasicWords(BasicWordDao dao, JsonArray array) {
        if (array == null) return;
        List<BasicWordEntity> batch = new ArrayList<>(BATCH_SIZE);
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            batch.add(new BasicWordEntity(
                    obj.get("wordId").getAsString(),
                    obj.get("kanjiComponents").getAsString(),
                    obj.get("kanaComponents").getAsString(),
                    getAsStringOrDefault(obj, "audioUrl", ""),
                    getAsStringOrDefault(obj, "accentMark", ""),
                    getAsStringOrDefault(obj, "mnemonic", "")
            ));
            if (batch.size() >= BATCH_SIZE) {
                dao.insertBasicWords(batch.toArray(new BasicWordEntity[0]));
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            dao.insertBasicWords(batch.toArray(new BasicWordEntity[0]));
        }
        AppLog.d(TAG, "Inserted " + array.size() + " basicWords");
    }

    private static void batchInsertWordMeanings(WordMeaningDao dao, JsonArray array) {
        if (array == null) return;
        List<WordMeaningEntity> batch = new ArrayList<>(BATCH_SIZE);
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            batch.add(new WordMeaningEntity(
                    obj.get("wordMeaningId").getAsString(),
                    obj.get("wordId").getAsString(),
                    obj.get("originalDefinition").getAsString(),
                    obj.get("translationDefinition").getAsString(),
                    obj.get("partOfSpeech").getAsString()
            ));
            if (batch.size() >= BATCH_SIZE) {
                dao.insertWords(batch.toArray(new WordMeaningEntity[0]));
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            dao.insertWords(batch.toArray(new WordMeaningEntity[0]));
        }
        AppLog.d(TAG, "Inserted " + array.size() + " wordMeanings");
    }

    private static void batchInsertAntonymWords(AntonymWordDao dao, JsonArray array) {
        if (array == null) return;
        List<AntonymWordEntity> batch = new ArrayList<>(BATCH_SIZE);
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            batch.add(new AntonymWordEntity(
                    obj.get("antonymWordId").getAsString(),
                    obj.get("wordId").getAsString(),
                    obj.get("correspondingWordId").getAsString(),
                    obj.get("kanjiComponents").getAsString(),
                    obj.get("kanaComponents").getAsString()
            ));
            if (batch.size() >= BATCH_SIZE) {
                dao.insertAntonymWords(batch.toArray(new AntonymWordEntity[0]));
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            dao.insertAntonymWords(batch.toArray(new AntonymWordEntity[0]));
        }
        AppLog.d(TAG, "Inserted " + array.size() + " antonymWords");
    }

    private static void batchInsertSynonymWords(SynonymWordDao dao, JsonArray array) {
        if (array == null) return;
        List<SynonymWordEntity> batch = new ArrayList<>(BATCH_SIZE);
        for (JsonElement elem : array) {
            JsonObject obj = elem.getAsJsonObject();
            batch.add(new SynonymWordEntity(
                    obj.get("synonymWordId").getAsString(),
                    obj.get("wordId").getAsString(),
                    obj.get("correspondingWordId").getAsString(),
                    obj.get("kanjiComponents").getAsString(),
                    obj.get("kanaComponents").getAsString()
            ));
            if (batch.size() >= BATCH_SIZE) {
                dao.insertSynonymWords(batch.toArray(new SynonymWordEntity[0]));
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            dao.insertSynonymWords(batch.toArray(new SynonymWordEntity[0]));
        }
        AppLog.d(TAG, "Inserted " + array.size() + " synonymWords");
    }

    private static String getAsStringOrDefault(JsonObject obj, String key, String defaultValue) {
        JsonElement elem = obj.get(key);
        if (elem == null || elem.isJsonNull()) return defaultValue;
        String val = elem.getAsString();
        return val.isEmpty() ? defaultValue : val;
    }
}