package com.Nihilisttt.LearnWord.Database.Repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.Nihilisttt.LearnWord.Database.Converter.AntonymWordConverter;
import com.Nihilisttt.LearnWord.Database.Converter.BasicWordConverter;
import com.Nihilisttt.LearnWord.Database.Converter.ConjugationFormConverter;
import com.Nihilisttt.LearnWord.Database.Converter.DerivedWordConverter;
import com.Nihilisttt.LearnWord.Database.Converter.EtymologyConverter;
import com.Nihilisttt.LearnWord.Database.Converter.GrammarPointConverter;
import com.Nihilisttt.LearnWord.Database.Converter.IdiomConverter;
import com.Nihilisttt.LearnWord.Database.Converter.KanjiInfoConverter;
import com.Nihilisttt.LearnWord.Database.Converter.RelatedWordConverter;
import com.Nihilisttt.LearnWord.Database.Converter.SynonymWordConverter;
import com.Nihilisttt.LearnWord.Database.Converter.UsageDistinctionConverter;
import com.Nihilisttt.LearnWord.Database.Converter.WordCollocationConverter;
import com.Nihilisttt.LearnWord.Database.Converter.WordMeaningConverter;
import com.Nihilisttt.LearnWord.Database.Converter.WordSentenceConverter;
import com.Nihilisttt.LearnWord.Database.Dao.AntonymWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.BasicWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.ConjugationFormDao;
import com.Nihilisttt.LearnWord.Database.Dao.DerivedWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.EtymologyDao;
import com.Nihilisttt.LearnWord.Database.Dao.GrammarPointDao;
import com.Nihilisttt.LearnWord.Database.Dao.IdiomDao;
import com.Nihilisttt.LearnWord.Database.Dao.KanjiInfoDao;
import com.Nihilisttt.LearnWord.Database.Dao.RelatedWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.SynonymWordDao;
import com.Nihilisttt.LearnWord.Database.Dao.UsageDistinctionDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordCollocationDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordMeaningDao;
import com.Nihilisttt.LearnWord.Database.Dao.WordSentenceDao;
import com.Nihilisttt.LearnWord.Database.Database.WordDatabase;
import com.Nihilisttt.LearnWord.Database.Entities.AntonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.BasicWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.ConjugationFormEntity;
import com.Nihilisttt.LearnWord.Database.Entities.DerivedWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.EtymologyEntity;
import com.Nihilisttt.LearnWord.Database.Entities.GrammarPointEntity;
import com.Nihilisttt.LearnWord.Database.Entities.IdiomEntity;
import com.Nihilisttt.LearnWord.Database.Entities.KanjiInfoEntity;
import com.Nihilisttt.LearnWord.Database.Entities.RelatedWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.SynonymWordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.UsageDistinctionEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordCollocationEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordMeaningEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordEntity;
import com.Nihilisttt.LearnWord.Database.Entities.WordSentenceEntity;
import com.Nihilisttt.LearnWord.JavaBean.AntonymWord;
import com.Nihilisttt.LearnWord.JavaBean.BasicWord;
import com.Nihilisttt.LearnWord.JavaBean.ConjugationForm;
import com.Nihilisttt.LearnWord.JavaBean.DerivedWord;
import com.Nihilisttt.LearnWord.JavaBean.Etymology;
import com.Nihilisttt.LearnWord.JavaBean.GrammarPoint;
import com.Nihilisttt.LearnWord.JavaBean.Idiom;
import com.Nihilisttt.LearnWord.JavaBean.KanjiInfo;
import com.Nihilisttt.LearnWord.JavaBean.RelatedWord;
import com.Nihilisttt.LearnWord.JavaBean.SynonymWord;
import com.Nihilisttt.LearnWord.JavaBean.UsageDistinction;
import com.Nihilisttt.LearnWord.JavaBean.Word;
import com.Nihilisttt.LearnWord.Database.Converter.WordConverter;
import com.Nihilisttt.LearnWord.JavaBean.WordCollocation;
import com.Nihilisttt.LearnWord.JavaBean.WordMeaning;
import com.Nihilisttt.LearnWord.JavaBean.WordSentence;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WordRepository {
    private final WordDao wordDao;
    private final BasicWordDao basicWordDao;
    private final WordMeaningDao wordMeaningDao;
    private final WordSentenceDao wordSentenceDao;
    private final WordCollocationDao wordCollocationDao;
    private final AntonymWordDao antonymWordDao;
    private final SynonymWordDao synonymWordDao;
    private final ConjugationFormDao conjugationFormDao;
    private final EtymologyDao etymologyDao;
    private final KanjiInfoDao kanjiInfoDao;
    private final UsageDistinctionDao usageDistinctionDao;
    private final GrammarPointDao grammarPointDao;
    private final IdiomDao idiomDao;
    private final DerivedWordDao derivedWordDao;
    private final RelatedWordDao relatedWordDao;
    private static volatile WordRepository instance;

    // 私有构造函数
    private WordRepository(Context context) {
        WordDatabase database = WordDatabase.getDatabase(context.getApplicationContext());
        this.wordDao = database.getWordDao();
        this.basicWordDao = database.getBasicWordDao();
        this.wordMeaningDao = database.getWordMeaningDao();
        this.wordSentenceDao = database.getWordSentenceDao();
        this.wordCollocationDao = database.getWordCollocationDao();
        this.antonymWordDao = database.getAntonymWordDao();
        this.synonymWordDao = database.getSynonymWordDao();
        this.conjugationFormDao = database.getConjugationFormDao();
        this.etymologyDao = database.getEtymologyDao();
        this.kanjiInfoDao = database.getKanjiInfoDao();
        this.usageDistinctionDao = database.getUsageDistinctionDao();
        this.grammarPointDao = database.getGrammarPointDao();
        this.idiomDao = database.getIdiomDao();
        this.derivedWordDao = database.getDerivedWordDao();
        this.relatedWordDao = database.getRelatedWordDao();
    }

    // 单例获取方法
    public static WordRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (WordRepository.class) {
                if (instance == null) {
                    instance = new WordRepository(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // 批量插入优化
    public void batchInsertWords(List<Word> words) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordEntity[] entities = words.stream()
                    .map(WordConverter::WordToWordEntity)
                    .toArray(WordEntity[]::new);
            wordDao.insertWords(entities);
        });
    }

    public void batchInsertBasicWords(List<BasicWord> basicWords) {
        WordDatabase.databaseExecutor.execute(() -> {
            BasicWordEntity[] entities = basicWords.stream()
                    .map(BasicWordConverter::BasicWordToBasicWordEntity)
                    .toArray(BasicWordEntity[]::new);
            basicWordDao.insertBasicWords(entities);
        });
    }

    public void batchInsertWordMeanings(List<WordMeaning> meanings) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordMeaningEntity[] entities = meanings.stream()
                    .map(WordMeaningConverter::WordMeaningToWordMeaningEntity)
                    .toArray(WordMeaningEntity[]::new);
            wordMeaningDao.insertWords(entities);
        });
    }

    public void batchInsertWordSentences(List<WordSentence> sentences) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordSentenceEntity[] entities = sentences.stream()
                    .map(WordSentenceConverter::WordSentenceToWordSentenceEntity)
                    .toArray(WordSentenceEntity[]::new);
            wordSentenceDao.insertWords(entities);
        });
    }

    public void batchInsertWordCollocations(List<WordCollocation> collocations) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordCollocationEntity[] entities = collocations.stream()
                    .map(WordCollocationConverter::WordCollocationToWordCollocationEntity)
                    .toArray(WordCollocationEntity[]::new);
            wordCollocationDao.insertWordCollocations(entities);
        });
    }

    public void batchInsertAntonyms(List<AntonymWord> antonyms) {
        WordDatabase.databaseExecutor.execute(() -> {
            AntonymWordEntity[] entities = antonyms.stream()
                    .map(AntonymWordConverter::AntonymWordToAntonymWordEntity)
                    .toArray(AntonymWordEntity[]::new);
            antonymWordDao.insertAntonymWords(entities);
        });
    }

    public void batchInsertSynonyms(List<SynonymWord> synonyms) {
        WordDatabase.databaseExecutor.execute(() -> {
            SynonymWordEntity[] entities = synonyms.stream()
                    .map(SynonymWordConverter::SynonymWordToSynonymWordEntity)
                    .toArray(SynonymWordEntity[]::new);
            synonymWordDao.insertSynonymWords(entities);
        });
    }

    public void batchInsertDerivedWords(List<DerivedWord> derivedWords) {
        WordDatabase.databaseExecutor.execute(() -> {
            DerivedWordEntity[] entities = derivedWords.stream()
                    .map(DerivedWordConverter::DerivedWordToDerivedWordEntity)
                    .toArray(DerivedWordEntity[]::new);
            derivedWordDao.insertDerivedWords(entities);
        });
    }

    public void batchInsertRelatedWords(List<RelatedWord> relatedWords) {
        WordDatabase.databaseExecutor.execute(() -> {
            RelatedWordEntity[] entities = relatedWords.stream()
                    .map(RelatedWordConverter::RelatedWordToRelatedWordEntity)
                    .toArray(RelatedWordEntity[]::new);
            relatedWordDao.insertRelatedWords(entities);
        });
    }

    // 修改所有原有的insert方法，使用WordDatabase的线程池
    public void insertWord(Word word) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordEntity entity = WordConverter.WordToWordEntity(word);
            wordDao.insertWords(entity);
        });
    }

    public void insertBasicWord(BasicWord basicWord) {
        WordDatabase.databaseExecutor.execute(() -> {
            BasicWordEntity entity = BasicWordConverter.BasicWordToBasicWordEntity(basicWord);
            basicWordDao.insertBasicWords(entity);
        });
    }

    public void insertWordMeaning(WordMeaning meaning) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordMeaningEntity entity = WordMeaningConverter.WordMeaningToWordMeaningEntity(meaning);
            wordMeaningDao.insertWords(entity);
        });
    }

    public void insertWordSentence(WordSentence sentence) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordSentenceEntity entity = WordSentenceConverter.WordSentenceToWordSentenceEntity(sentence);
            wordSentenceDao.insertWords(entity);
        });
    }

    public void insertWordCollocation(WordCollocation collocation) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordCollocationEntity entity = WordCollocationConverter.WordCollocationToWordCollocationEntity(collocation);
            wordCollocationDao.insertWordCollocations(entity);
        });
    }

    public void insertAntonym(AntonymWord antonym) {
        WordDatabase.databaseExecutor.execute(() -> {
            AntonymWordEntity entity = AntonymWordConverter.AntonymWordToAntonymWordEntity(antonym);
            antonymWordDao.insertAntonymWords(entity);
        });
    }

    public void insertSynonym(SynonymWord synonym) {
        WordDatabase.databaseExecutor.execute(() -> {
            SynonymWordEntity entity = SynonymWordConverter.SynonymWordToSynonymWordEntity(synonym);
            synonymWordDao.insertSynonymWords(entity);
        });
    }

    // 修改所有原有的update方法，使用WordDatabase的线程池
    public void updateWord(Word word) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordEntity entity = WordConverter.WordToWordEntity(word);
            wordDao.updateWords(entity);
        });
    }

    public void updateBasicWord(BasicWord basicWord) {
        WordDatabase.databaseExecutor.execute(() -> {
            BasicWordEntity entity = BasicWordConverter.BasicWordToBasicWordEntity(basicWord);
            basicWordDao.updateBasicWords(entity);
        });
    }

    public void updateWordMeaning(WordMeaning meaning) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordMeaningEntity entity = WordMeaningConverter.WordMeaningToWordMeaningEntity(meaning);
            wordMeaningDao.updateWords(entity);
        });
    }

    public void updateWordSentence(WordSentence sentence) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordSentenceEntity entity = WordSentenceConverter.WordSentenceToWordSentenceEntity(sentence);
            wordSentenceDao.updateWords(entity);
        });
    }

    public void updateWordCollocation(WordCollocation collocation) {
        WordDatabase.databaseExecutor.execute(() -> {
            WordCollocationEntity entity = WordCollocationConverter.WordCollocationToWordCollocationEntity(collocation);
            wordCollocationDao.updateWordCollocations(entity);
        });
    }

    public void updateAntonym(AntonymWord antonym) {
        WordDatabase.databaseExecutor.execute(() -> {
            AntonymWordEntity entity = AntonymWordConverter.AntonymWordToAntonymWordEntity(antonym);
            antonymWordDao.updateAntonymWords(entity);
        });
    }

    public void updateSynonym(SynonymWord synonym) {
        WordDatabase.databaseExecutor.execute(() -> {
            SynonymWordEntity entity = SynonymWordConverter.SynonymWordToSynonymWordEntity(synonym);
            synonymWordDao.updateSynonymWords(entity);
        });
    }

    // 修改所有原有的delete方法，使用WordDatabase的线程池
    public void deleteWord(String wordId) {
        WordDatabase.databaseExecutor.execute(() -> wordDao.deleteWord(wordId));
    }

    public void deleteBasicWord(String wordId) {
        WordDatabase.databaseExecutor.execute(() -> basicWordDao.deleteBasicWord(wordId));
    }

    public void deleteWordMeaning(String wordId) {
        WordDatabase.databaseExecutor.execute(() -> wordMeaningDao.deleteWord(wordId));
    }

    public void deleteWordSentence(String wordId) {
        WordDatabase.databaseExecutor.execute(() -> wordSentenceDao.deleteWord(wordId));
    }

    public void deleteWordCollocation(String wordId) {
        WordDatabase.databaseExecutor.execute(() -> wordCollocationDao.deleteWordCollocation(wordId));
    }

    public void deleteAntonym(String wordId) {
        WordDatabase.databaseExecutor.execute(() -> antonymWordDao.deleteAntonymWord(wordId));
    }

    public void deleteSynonym(String wordId) {
        WordDatabase.databaseExecutor.execute(() -> synonymWordDao.deleteSynonymWord(wordId));
    }

    // 修改clearAll方法，使用WordDatabase的线程池
    public void deleteAll() {
        WordDatabase.databaseExecutor.execute(() -> {
            wordDao.deleteAllWords();
            basicWordDao.deleteAllBasicWords();
            wordMeaningDao.deleteAllWordMeanings();
            wordSentenceDao.deleteAllWordSentences();
            wordCollocationDao.deleteAllWordCollocations();
            antonymWordDao.deleteAllAntonymWords();
            synonymWordDao.deleteAllSynonymWords();
        });
    }

    // region 查询操作


    // region Word操作
    public LiveData<Word> getWordById(String wordId) {
        return Transformations.map(wordDao.getWordByWordId(wordId),
                WordConverter::WordEntityToWord
        );
    }

    public Word getWordByIdSync(String wordId) {
        WordEntity entity = wordDao.getWordByWordIdSync(wordId);
        return WordConverter.WordEntityToWord(entity);
    }

    public BasicWord getBasicWordByIdSync(String wordId) {
        BasicWordEntity entity = basicWordDao.getBasicWordByWordIdSync(wordId);
        return BasicWordConverter.BasicWordEntityToBasicWord(entity);
    }

    public List<WordMeaning> getWordMeaningsByWordMeaningIdListSync(List<String> meaningIdList) {
        if (meaningIdList == null || meaningIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<WordMeaningEntity> entities = wordMeaningDao.getWordMeaningsByWordMeaningIdListSync(meaningIdList);
        return entities.stream()
                .map(WordMeaningConverter::WordMeaningEntityToWordMeaning)
                .collect(Collectors.toList());
    }

    public LiveData<List<Word>> getAllWords() {
        return Transformations.map(wordDao.getAllWords(), entities ->
                entities.stream()
                        .map(WordConverter::WordEntityToWord)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<Word>> getWordsByIdList(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            MutableLiveData<List<Word>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(wordDao.getWordsByIdList(idList),
                entities -> entities.stream()
                        .map(WordConverter::WordEntityToWord)
                        .collect(Collectors.toList())
        );
    }

    // endregion

    // region BasicWord操作

    public LiveData<List<BasicWord>> getAllBasicWords() {
        return Transformations.map(basicWordDao.getAllBasicWords(), entities ->
                entities.stream()
                        .map(BasicWordConverter::BasicWordEntityToBasicWord)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<BasicWord> getBasicWordById(String wordId) {
        return Transformations.map(basicWordDao.getBasicWordByWordId(wordId),
                BasicWordConverter::BasicWordEntityToBasicWord
        );
    }

    public void clearAllBasicWords() {
        WordDatabase.databaseExecutor.execute(basicWordDao::deleteAllBasicWords);
    }

    // endregion

    // region WordMeaning操作
    public LiveData<WordMeaning> getWordMeaningByMeaningId(String meaningId) {
        return Transformations.map(wordMeaningDao.getWordMeaningByWordMeaningId(meaningId),
                WordMeaningConverter::WordMeaningEntityToWordMeaning
        );
    }

    public LiveData<List<WordMeaning>> getWordMeaningsByWordMeaningIdList(List<String> meaningIdList) {
        if (meaningIdList == null || meaningIdList.isEmpty()) {
            MutableLiveData<List<WordMeaning>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(wordMeaningDao.getWordMeaningsByWordMeaningIdList(meaningIdList),
                entities -> entities.stream()
                        .map(WordMeaningConverter::WordMeaningEntityToWordMeaning)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<WordMeaning>> getWordMeaningsByWordId(String wordId) {
        return Transformations.map(wordMeaningDao.getWordMeaningByWordId(wordId),
                entities -> entities.stream()
                        .map(WordMeaningConverter::WordMeaningEntityToWordMeaning)
                        .collect(Collectors.toList())
        );
    }

    // endregion

    // region WordSentence操作
    public LiveData<WordSentence> getWordSentenceByWordSentenceId(String wordSentenceId) {
        return Transformations.map(wordSentenceDao.getWordSentenceByWordSentenceId(wordSentenceId),
                WordSentenceConverter::WordSentenceEntityToWordSentence
        );
    }
    public LiveData<List<WordSentence>> getWordSentenceByWordId(String wordId) {
        return Transformations.map(wordSentenceDao.getWordSentenceByWordId(wordId),
                entities -> entities.stream()
                        .map(WordSentenceConverter::WordSentenceEntityToWordSentence)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<WordSentence>> getWordSentenceByWordMeaningId(String wordMeaningId) {
        return Transformations.map(wordSentenceDao.getWordSentenceByWordMeaningId(wordMeaningId),
                entities -> entities.stream()
                        .map(WordSentenceConverter::WordSentenceEntityToWordSentence)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<WordSentence>> getWordSentencesBySentencesIdList(List<String> sentenceIds) {
        if (sentenceIds == null || sentenceIds.isEmpty()) {
            MutableLiveData<List<WordSentence>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(wordSentenceDao.getWordSentencesByWordSentenceIdList(sentenceIds),
                entities -> entities.stream()
                        .map(WordSentenceConverter::WordSentenceEntityToWordSentence)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<WordSentence>> getAllWordSentences() {
        return Transformations.map(wordSentenceDao.getAllWordSentence(),
                entities -> entities.stream()
                        .map(WordSentenceConverter::WordSentenceEntityToWordSentence)
                        .collect(Collectors.toList())
        );
    }

    // endregion

    // region WordCollocation操作
    public LiveData<WordCollocation> getWordCollocationByWordId(String wordId) {
        return Transformations.map(wordCollocationDao.getWordCollocationByWordCollocationId(wordId),
                WordCollocationConverter::WordCollocationEntityToWordCollocation
        );
    }

    public LiveData<List<WordCollocation>> getWordCollocationsByWordCollocationIdList(List<String> collocationIds) {
        if (collocationIds == null || collocationIds.isEmpty()) {
            MutableLiveData<List<WordCollocation>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(wordCollocationDao.getWordCollocationsByWordCollocationIdList(collocationIds),
                entities -> entities.stream()
                        .map(WordCollocationConverter::WordCollocationEntityToWordCollocation)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<WordCollocation>> getAllWordCollocations() {
        return Transformations.map(wordCollocationDao.getAllWordCollocations(),
                entities -> entities.stream()
                        .map(WordCollocationConverter::WordCollocationEntityToWordCollocation)
                        .collect(Collectors.toList())
        );
    }

    // endregion

    // region AntonymWord操作
    public LiveData<List<AntonymWord>> getAntonymByWordId(String wordId) {
        return Transformations.map(antonymWordDao.getAntonymWordByWordId(wordId),
                entities -> entities.stream()
                        .map(AntonymWordConverter::AntonymWordEntityToAntonymWord)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<AntonymWord>> getAntonymWordsByAntonymWordsIdList(List<String> antonymIdList) {
        if (antonymIdList == null || antonymIdList.isEmpty()) {
            MutableLiveData<List<AntonymWord>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(antonymWordDao.getAntonymWordsByAntonymIdList(antonymIdList),
                entities -> entities.stream()
                        .map(AntonymWordConverter::AntonymWordEntityToAntonymWord)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<AntonymWord>> getAllAntonymWords() {
        return Transformations.map(antonymWordDao.getAllAntonymWords(),
                entities -> entities.stream()
                        .map(AntonymWordConverter::AntonymWordEntityToAntonymWord)
                        .collect(Collectors.toList())
        );
    }

    // endregion

    // region SynonymWord操作
    public LiveData<List<SynonymWord>> getSynonymByWordId(String wordId) {
        return Transformations.map(synonymWordDao.getSynonymWordByWordId(wordId),
                entities -> entities.stream()
                        .map(SynonymWordConverter::SynonymWordEntityToSynonymWord)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<SynonymWord>> getSynonymWordsBySynonymWordsIdList(List<String> synonymIdList) {
        if (synonymIdList == null || synonymIdList.isEmpty()) {
            MutableLiveData<List<SynonymWord>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(synonymWordDao.getSynonymWordsBySynonymIdList(synonymIdList),
                entities -> entities.stream()
                        .map(SynonymWordConverter::SynonymWordEntityToSynonymWord)
                        .collect(Collectors.toList())
        );
    }

    public LiveData<List<SynonymWord>> getAllSynonymWords() {
        return Transformations.map(synonymWordDao.getAllSynonymWords(),
                entities -> entities.stream()
                        .map(SynonymWordConverter::SynonymWordEntityToSynonymWord)
                        .collect(Collectors.toList())
        );
    }

    // endregion

    // region DerivedWord操作
    public LiveData<List<DerivedWord>> getDerivedWordsByDerivedWordsIdList(List<String> derivedIdList) {
        if (derivedIdList == null || derivedIdList.isEmpty()) {
            MutableLiveData<List<DerivedWord>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(derivedWordDao.getDerivedWordsByDerivedIdList(derivedIdList),
                entities -> entities.stream()
                        .map(DerivedWordConverter::DerivedWordEntityToDerivedWord)
                        .collect(Collectors.toList())
        );
    }
    // endregion

    // region RelatedWord操作
    public LiveData<List<RelatedWord>> getRelatedWordsByRelatedWordsIdList(List<String> relatedIdList) {
        if (relatedIdList == null || relatedIdList.isEmpty()) {
            MutableLiveData<List<RelatedWord>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(relatedWordDao.getRelatedWordsByRelatedIdList(relatedIdList),
                entities -> entities.stream()
                        .map(RelatedWordConverter::RelatedWordEntityToRelatedWord)
                        .collect(Collectors.toList())
        );
    }
    // endregion

    // region ConjugationForm操作
    public LiveData<List<ConjugationForm>> getConjugationFormsByConjugationFormIdList(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            MutableLiveData<List<ConjugationForm>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(conjugationFormDao.getConjugationFormsByConjugationFormIdList(idList),
                entities -> entities.stream()
                        .map(ConjugationFormConverter::entityToModel)
                        .collect(Collectors.toList())
        );
    }
    // endregion

    // region Etymology操作
    public LiveData<List<Etymology>> getEtymologiesByEtymologyIdList(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            MutableLiveData<List<Etymology>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(etymologyDao.getEtymologiesByIdList(idList),
                entities -> entities.stream()
                        .map(EtymologyConverter::EtymologyEntityToEtymology)
                        .collect(Collectors.toList())
        );
    }
    // endregion

    // region KanjiInfo操作
    public LiveData<List<KanjiInfo>> getKanjiInfosByKanjiInfoIdList(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            MutableLiveData<List<KanjiInfo>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(kanjiInfoDao.getKanjiInfosByKanjiInfoIdList(idList),
                entities -> entities.stream()
                        .map(KanjiInfoConverter::entityToModel)
                        .collect(Collectors.toList())
        );
    }
    // endregion

    // region UsageDistinction操作
    public LiveData<List<UsageDistinction>> getUsageDistinctionsByUsageDistinctionIdList(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            MutableLiveData<List<UsageDistinction>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(usageDistinctionDao.getUsageDistinctionsByUsageDistinctionIdList(idList),
                entities -> entities.stream()
                        .map(UsageDistinctionConverter::entityToModel)
                        .collect(Collectors.toList())
        );
    }
    // endregion

    // region GrammarPoint操作
    public LiveData<List<GrammarPoint>> getGrammarPointsByGrammarPointIdList(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            MutableLiveData<List<GrammarPoint>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(grammarPointDao.getGrammarPointsByGrammarPointIdList(idList),
                entities -> entities.stream()
                        .map(GrammarPointConverter::entityToModel)
                        .collect(Collectors.toList())
        );
    }
    // endregion

    // region Idiom操作
    public LiveData<List<Idiom>> getIdiomsByIdiomIdList(List<String> idList) {
        if (idList == null || idList.isEmpty()) {
            MutableLiveData<List<Idiom>> result = new MutableLiveData<>();
            result.setValue(Collections.emptyList());
            return result;
        }
        return Transformations.map(idiomDao.getIdiomsByIdiomIdList(idList),
                entities -> entities.stream()
                        .map(IdiomConverter::entityToModel)
                        .collect(Collectors.toList())
        );
    }
    // endregion

    // region 导航查询
    public String getFirstWordId() {
        return wordDao.getFirstWordId();
    }

    public String getLastWordId() {
        return wordDao.getLastWordId();
    }

    public String getNextWordId(String currentId) {
        return wordDao.getNextWordId(currentId);
    }

    public String getPreviousWordId(String currentId) {
        return wordDao.getPreviousWordId(currentId);
    }
    // endregion

    // region 搜索
    public List<BasicWordEntity> searchBasicWordsSync(String query, int limit) {
        return basicWordDao.searchBasicWordsSync(query, limit);
    }

    public List<WordMeaningEntity> getWordMeaningsByWordIdListSync(List<String> wordIdList) {
        return wordMeaningDao.getWordMeaningsByWordIdListSync(wordIdList);
    }

    public WordMeaningEntity getFirstMeaningByWordIdSync(String wordId) {
        return wordMeaningDao.getFirstMeaningByWordIdSync(wordId);
    }

    public BasicWordEntity getBasicWordEntityByIdSync(String wordId) {
        return basicWordDao.getBasicWordByWordIdSync(wordId);
    }

    public List<WordMeaningEntity> getRandomMeaningsSync(String excludeWordId, int limit) {
        return wordMeaningDao.getRandomMeaningsSync(excludeWordId, limit);
    }
    // endregion

}
