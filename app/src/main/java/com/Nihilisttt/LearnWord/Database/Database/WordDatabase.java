package com.Nihilisttt.LearnWord.Database.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.Nihilisttt.LearnWord.Database.Dao.AntonymWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.BasicWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.ConjugationFormDao;
import com.Nihilisttt.LearnWord.Database.Dao.EtymologyDao;
import com.Nihilisttt.LearnWord.Database.Dao.GrammarPointDao;
import com.Nihilisttt.LearnWord.Database.Dao.IdiomDao;
import com.Nihilisttt.LearnWord.Database.Dao.KanjiInfoDao;
import com.Nihilisttt.LearnWord.Database.Dao.SynonymWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.UsageDistinctionDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordCollocationDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordMeaningDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordSentenceDao;
import com.Nihilisttt.LearnWord.Database.Entities.AntonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.ConjugationFormEntity;
import com.Nihilisttt.LearnWord.Database.Entities.EtymologyEntity;
import com.Nihilisttt.LearnWord.Database.Entities.GrammarPointEntity;
import com.Nihilisttt.LearnWord.Database.Entities.IdiomEntity;
import com.Nihilisttt.LearnWord.Database.Entities.KanjiInfoEntity;
import com.Nihilisttt.LearnWord.Database.Entities.SynonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.UsageDistinctionEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordCollocationEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;
import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordSentenceEntity;

import android.content.SharedPreferences;


import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// singleton
@Database(entities = {
        WordEntity.class,
        BasicWordEntity.class,
        WordMeaningEntity.class,
        WordSentenceEntity.class,
        AntonymWordEntity.class,
        SynonymWordEntity.class,
        WordCollocationEntity.class,
        ConjugationFormEntity.class,
        EtymologyEntity.class,
        KanjiInfoEntity.class,
        UsageDistinctionEntity.class,
        GrammarPointEntity.class,
        IdiomEntity.class
},
        version = 2,
        exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    private static final String PREFS_NAME = "room_db_prefs";
    private static final String KEY_DB_VERSION = "prebuilt_db_version";
    private static final int PREBUILT_DB_VERSION = 21;
    // 线程安全的单例模式
    private static volatile WordDatabase INSTANCE;
    // 数据库操作线程池（4线程）
    private static final int NUMBER_OF_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    public static final ExecutorService databaseExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    public static synchronized WordDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordDatabase.class) {
                if (INSTANCE == null) {
                    deleteOldDatabaseIfNeeded(context);
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            WordDatabase.class,
                            "word_database"
                    )
                            .createFromAsset("databases/word_database.db")
                            .setQueryExecutor(databaseExecutor)
                            .setJournalMode(JournalMode.TRUNCATE)
                            .fallbackToDestructiveMigration()
                            .enableMultiInstanceInvalidation()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void deleteOldDatabaseIfNeeded(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getInt(KEY_DB_VERSION, 0) >= PREBUILT_DB_VERSION) {
            return;
        }
        String dbName = "word_database";
        File dbFile = context.getDatabasePath(dbName);
        if (dbFile.exists()) {
            context.deleteDatabase(dbName);
        }
        prefs.edit().putInt(KEY_DB_VERSION, PREBUILT_DB_VERSION).apply();
    }
    public abstract WordDao getWordDao();
    public abstract BasicWordDao getBasicWordDao();
    public abstract WordMeaningDao getWordMeaningDao();
    public abstract WordSentenceDao getWordSentenceDao();
    public abstract AntonymWordDao getAntonymWordDao();
    public abstract SynonymWordDao getSynonymWordDao();
    public abstract WordCollocationDao getWordCollocationDao();
    public abstract ConjugationFormDao getConjugationFormDao();
    public abstract EtymologyDao getEtymologyDao();
    public abstract KanjiInfoDao getKanjiInfoDao();
    public abstract UsageDistinctionDao getUsageDistinctionDao();
    public abstract GrammarPointDao getGrammarPointDao();
    public abstract IdiomDao getIdiomDao();
    // 新增事务控制接口
    public interface TransactionListener {
        void onCommit();
        void onRollback(Exception e);
    }
    // 关闭数据库连接（在Application中调用）
    public static void closeDatabase() {
        if (INSTANCE != null && INSTANCE.isOpen()) {
            INSTANCE.close();
            INSTANCE = null;
            databaseExecutor.shutdown();
        }
    }

}
