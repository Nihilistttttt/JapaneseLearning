package com.Nihilisttt.LearnWord.Database.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.Nihilisttt.LearnWord.Database.Dao.AntonymWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.BasicWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.SynonymWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordCollocationDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordMeaningDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordSentenceDao;
import com.Nihilisttt.LearnWord.Database.Entities.AntonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.SynonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordCollocationEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;
import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordSentenceEntity;

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
        WordCollocationEntity.class
},
        version = 1,
        exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
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
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            WordDatabase.class,
                            "word_database"
                    )
                            .setQueryExecutor(databaseExecutor)
                            .setJournalMode(JournalMode.TRUNCATE)
                            .fallbackToDestructiveMigration()  // 允许破坏性迁移（清空旧数据）
                            .enableMultiInstanceInvalidation()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    public abstract WordDao getWordDao();
    public abstract BasicWordDao getBasicWordDao();
    public abstract WordMeaningDao getWordMeaningDao();
    public abstract WordSentenceDao getWordSentenceDao();
    public abstract AntonymWordDao getAntonymWordDao();
    public abstract SynonymWordDao getSynonymWordDao();
    public abstract WordCollocationDao getWordCollocationDao();
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
