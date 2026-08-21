package com.Nihilisttt.LearnWord.Database.Repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.Nihilisttt.LearnWord.Database.Dao.BasicWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordBookDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordBookItemDao;
import com.Nihilisttt.LearnWord.Database.Database.WordDatabase;
import com.Nihilisttt.LearnWord.Database.Entities.WordBookEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordBookItemEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresetBookInitializer {
    private static final String PREFS_NAME = "preset_books_prefs";
    private static final String KEY_INITIALIZED = "preset_books_initialized";
    private static final String KEY_VERSION = "preset_books_version";
    private static final int CURRENT_VERSION = 2;

    private static final Map<Integer, int[]> JLPT_BOOK_INFO = new HashMap<>();

    static {
        JLPT_BOOK_INFO.put(5, new int[]{0xFF2A5485, 0});
        JLPT_BOOK_INFO.put(4, new int[]{0xFF471DA9, 1});
        JLPT_BOOK_INFO.put(3, new int[]{0xFF137A52, 2});
        JLPT_BOOK_INFO.put(2, new int[]{0xFF4E7C16, 3});
        JLPT_BOOK_INFO.put(1, new int[]{0xFF8F4D1F, 4});
    }

    public static void initIfNeeded(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getInt(KEY_VERSION, 0) >= CURRENT_VERSION) {
            return;
        }

        WordDatabase.databaseExecutor.execute(() -> {
            WordDatabase db = WordDatabase.getDatabase(context.getApplicationContext());
            WordBookDao bookDao = db.getWordBookDao();
            WordBookItemDao itemDao = db.getWordBookItemDao();
            BasicWordDao basicWordDao = db.getBasicWordDao();

            if (bookDao.getWordBookCountSync() > 0) {
                prefs.edit().putInt(KEY_VERSION, CURRENT_VERSION).apply();
                return;
            }

            List<Integer> availableLevels = basicWordDao.getAvailableJlptLevelsSync();
            if (availableLevels == null || availableLevels.isEmpty()) {
                prefs.edit().putInt(KEY_VERSION, CURRENT_VERSION).apply();
                return;
            }

            long now = System.currentTimeMillis();
            List<WordBookEntity> books = new ArrayList<>();
            List<WordBookItemEntity> allItems = new ArrayList<>();

            for (int level : availableLevels) {
                List<String> wordIds = basicWordDao.getWordIdsByJlptLevelSync(level);
                if (wordIds == null || wordIds.isEmpty()) continue;

                int[] info = JLPT_BOOK_INFO.getOrDefault(level, new int[]{0xFF2A5485, level});
                String bookId = "jlpt_n" + level;
                String name = "JLPT N" + level;
                int sortOrder = info[1];
                int color = info[0];

                WordBookEntity book = new WordBookEntity(
                        bookId, name, "JLPT N" + level + " 词汇", level, true,
                        wordIds.size(), 0, 0, 20, 200, sortOrder, color, now, now
                );
                books.add(book);

                for (int i = 0; i < wordIds.size(); i++) {
                    allItems.add(new WordBookItemEntity(0, bookId, wordIds.get(i), i));
                }
            }

            if (!books.isEmpty()) {
                bookDao.insertWordBooks(books);
            }
            if (!allItems.isEmpty()) {
                int batchSize = 5000;
                for (int i = 0; i < allItems.size(); i += batchSize) {
                    int end = Math.min(i + batchSize, allItems.size());
                    itemDao.insertItems(allItems.subList(i, end));
                }
            }

            prefs.edit()
                    .putBoolean(KEY_INITIALIZED, true)
                    .putInt(KEY_VERSION, CURRENT_VERSION)
                    .apply();
        });
    }
}