package com.Nihilisttt.LearnWord.Database.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.Nihilisttt.LearnWord.Algorithm.EbbinghausAlgorithm;
import com.Nihilisttt.LearnWord.Algorithm.SRSConfig;
import com.Nihilisttt.LearnWord.Database.Dao.BasicWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordBookDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordBookItemDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordDistractorDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordReviewDao;
import com.Nihilisttt.LearnWord.Database.Database.WordDatabase;
import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordBookEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordBookItemEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordReviewEntity;

import java.util.ArrayList;
import java.util.List;

public class WordBookRepository {
    private final WordBookDao bookDao;
    private final WordBookItemDao itemDao;
    private final WordReviewDao reviewDao;
    private final WordDistractorDao distractorDao;
    private final BasicWordDao basicWordDao;
    private static volatile WordBookRepository instance;

    private WordBookRepository(Context context) {
        WordDatabase database = WordDatabase.getDatabase(context.getApplicationContext());
        this.bookDao = database.getWordBookDao();
        this.itemDao = database.getWordBookItemDao();
        this.reviewDao = database.getWordReviewDao();
        this.distractorDao = database.getWordDistractorDao();
        this.basicWordDao = database.getBasicWordDao();
    }

    public static WordBookRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (WordBookRepository.class) {
                if (instance == null) {
                    instance = new WordBookRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public LiveData<List<WordBookEntity>> getAllWordBooks() {
        return bookDao.getAllWordBooks();
    }

    public WordBookEntity getWordBookByIdSync(String bookId) {
        return bookDao.getWordBookByIdSync(bookId);
    }

    public void createCustomWordBook(String bookId, String name, String description, List<String> wordIds) {
        WordDatabase.databaseExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            WordBookEntity book = new WordBookEntity(
                    bookId, name, description, 0, false,
                    wordIds.size(), 0, 0, 20, 200, 99, 0, now, now
            );
            bookDao.insertWordBook(book);

            List<WordBookItemEntity> items = new ArrayList<>();
            for (int i = 0; i < wordIds.size(); i++) {
                items.add(new WordBookItemEntity(0, bookId, wordIds.get(i), i));
            }
            itemDao.insertItems(items);
        });
    }

    public void deleteWordBook(String bookId) {
        WordDatabase.databaseExecutor.execute(() -> {
            itemDao.deleteItemsByBookId(bookId);
            bookDao.deleteWordBook(bookId);
        });
    }

    public void updateBookDailyTargets(String bookId, int newTarget, int reviewTarget) {
        WordDatabase.databaseExecutor.execute(() -> {
            bookDao.updateBookDailyTargets(bookId, newTarget, reviewTarget, System.currentTimeMillis());
        });
    }

    public List<String> getTodayNewWords(String bookId, int limit) {
        return itemDao.getNewWordIdsSync(bookId, limit);
    }

    public List<WordReviewEntity> getStudyingWords(String bookId, int limit) {
        return reviewDao.getStudyingWordsSync(bookId, limit);
    }

    public List<WordReviewEntity> getDueReviews(String bookId, int limit) {
        long now = System.currentTimeMillis();
        long dayStart = SRSConfig.getDayStartTime(now);
        return reviewDao.getDueReviewsSync(bookId, now, dayStart, limit);
    }

    public List<WordReviewEntity> getDueReviewsAllBooks(int limit) {
        long now = System.currentTimeMillis();
        long dayStart = SRSConfig.getDayStartTime(now);
        return reviewDao.getDueReviewsSyncAllBooks(now, dayStart, limit);
    }

    public WordReviewEntity getReviewSync(String wordId, String bookId) {
        return reviewDao.getReviewSync(wordId, bookId);
    }

    public void startNewWordStudy(String wordId, String bookId) {
        WordDatabase.databaseExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            WordReviewEntity review = EbbinghausAlgorithm.createNewReview(wordId, bookId, now);
            reviewDao.insertReview(review);
        });
    }

    public void pass(String wordId, String bookId) {
        WordDatabase.databaseExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            WordReviewEntity review = reviewDao.getReviewSync(wordId, bookId);
            if (review != null) {
                EbbinghausAlgorithm.pass(review, now);
                reviewDao.updateReview(review);
                updateBookProgress(bookId);
            }
        });
    }

    public void fuzzyPass(String wordId, String bookId) {
        WordDatabase.databaseExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            WordReviewEntity review = reviewDao.getReviewSync(wordId, bookId);
            if (review != null) {
                EbbinghausAlgorithm.fuzzyPass(review, now);
                reviewDao.updateReview(review);
            }
        });
    }

    public void fail(String wordId, String bookId) {
        WordDatabase.databaseExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            WordReviewEntity review = reviewDao.getReviewSync(wordId, bookId);
            if (review != null) {
                EbbinghausAlgorithm.fail(review, now);
                reviewDao.updateReview(review);
            }
        });
    }

    public void deleteWord(String wordId, String bookId) {
        WordDatabase.databaseExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            WordReviewEntity review = reviewDao.getReviewSync(wordId, bookId);
            if (review != null) {
                EbbinghausAlgorithm.delete(review, now);
                reviewDao.updateReview(review);
                updateBookProgress(bookId);
            } else {
                WordReviewEntity newReview = EbbinghausAlgorithm.createNewReview(wordId, bookId, now);
                EbbinghausAlgorithm.delete(newReview, now);
                reviewDao.insertReview(newReview);
                updateBookProgress(bookId);
            }
        });
    }

    private void updateBookProgress(String bookId) {
        int learned = reviewDao.getLearnedCountSync(bookId);
        int mastered = reviewDao.getMasteredCountSync(bookId, SRSConfig.MASTERED_THRESHOLD);
        bookDao.updateBookProgress(bookId, learned, mastered, System.currentTimeMillis());
    }

    public int getDailyStudyCount(String bookId) {
        long dayStart = SRSConfig.getDayStartTime(System.currentTimeMillis());
        return reviewDao.getDailyStudyCountSync(bookId, dayStart);
    }

    public int getDailyPassCount(String bookId) {
        long dayStart = SRSConfig.getDayStartTime(System.currentTimeMillis());
        return reviewDao.getDailyPassCountSync(bookId, dayStart);
    }

    public int getDailyReviewCount(String bookId) {
        long dayStart = SRSConfig.getDayStartTime(System.currentTimeMillis());
        return reviewDao.getDailyReviewCountSync(bookId, dayStart);
    }

    public int getDueReviewCount(String bookId) {
        long now = System.currentTimeMillis();
        return reviewDao.getDueReviewCountSync(bookId, now);
    }

    public int getStudyingCount(String bookId) {
        return reviewDao.getStudyingCountSync(bookId);
    }

    public int getLearnedCount(String bookId) {
        return reviewDao.getLearnedCountSync(bookId);
    }

    public int getMasteredCount(String bookId) {
        return reviewDao.getMasteredCountSync(bookId, SRSConfig.MASTERED_THRESHOLD);
    }

    public int getDeletedCount(String bookId) {
        return reviewDao.getDeletedCountSync(bookId);
    }

    public int getTotalWordCount(String bookId) {
        return itemDao.getWordCountByBookIdSync(bookId);
    }

    public List<String> getDistractorWordIds(String wordId, int limit) {
        return distractorDao.getDistractorWordIdsSync(wordId, limit);
    }

    public int getDistractorCount() {
        return distractorDao.getCountSync();
    }

    public void createTestReviews(String bookId, int count) {
        WordDatabase.databaseExecutor.execute(() -> {
            long now = System.currentTimeMillis();
            long twoDaysAgo = now - 2L * 24 * 3600 * 1000;
            long oneHourAgo = now - 3600 * 1000;

            List<String> wordIds = itemDao.getNewWordIdsSync(bookId, count + 50);
            if (wordIds == null || wordIds.isEmpty()) return;

            int inserted = 0;
            for (String wordId : wordIds) {
                if (inserted >= count) break;
                WordReviewEntity existing = reviewDao.getReviewByWordIdSync(wordId);
                if (existing != null) continue;

                WordReviewEntity review = new WordReviewEntity(
                        0, wordId, bookId,
                        SRSConfig.STATUS_COMPLETED, 1, oneHourAgo, 0,
                        twoDaysAgo, twoDaysAgo
                );
                reviewDao.insertReview(review);
                inserted++;
            }
            updateBookProgress(bookId);
        });
    }
}