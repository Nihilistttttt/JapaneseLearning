"""
从room_import.json + word_sentences_data.json 生成Room兼容的SQLite数据库

Room的createFromAsset会自动复制DB文件，无需运行时JSON解析，彻底避免OOM。

步骤：
1. 创建与Room Entity完全一致的表结构
2. 插入room_master_table元数据（identity_hash）
3. 从JSON批量导入数据
4. 从word_sentences_data.json导入例句并更新Word.sentenceIdList
5. VACUUM压缩
"""

import json
import os
import sqlite3
from collections import defaultdict

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn'
DATA_DIR = os.path.join(BASE, '日语数据处理')
INPUT_PATH = os.path.join(BASE, 'app', 'src', 'main', 'assets', 'room_import.json')
SENTENCES_PATH = os.path.join(DATA_DIR, 'word_sentences_data.json')
OUTPUT_PATH = os.path.join(BASE, 'app', 'src', 'main', 'assets', 'databases', 'word_database.db')

# Room identity_hash - 从编译生成的WordDatabase_Impl.java中获取
# 需要先构建一次项目，然后从build/generated/source/kapt/debug/中找到
# 暂时用占位符，构建后替换
IDENTITY_HASH = "76252537c461c611882c982c1f2d0225"

BATCH_SIZE = 2000


def create_tables(cursor):
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS room_master_table (
            id INTEGER PRIMARY KEY,
            identity_hash TEXT NOT NULL
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS Word (
            wordId TEXT NOT NULL PRIMARY KEY,
            meaningIdList TEXT,
            sentenceIdList TEXT,
            synonymWordIdList TEXT,
            antonymWordIdList TEXT,
            collocationIdList TEXT
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS BasicWord (
            wordId TEXT NOT NULL PRIMARY KEY,
            kanjiComponents TEXT,
            kanaComponents TEXT,
            audioUrl TEXT,
            accentMark TEXT,
            mnemonic TEXT
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS WordMeaning (
            wordMeaningId TEXT NOT NULL PRIMARY KEY,
            wordId TEXT,
            originalDefinition TEXT,
            translationDefinition TEXT,
            partOfSpeech TEXT
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS WordSentence (
            wordSentenceId TEXT NOT NULL PRIMARY KEY,
            wordId TEXT,
            wordMeaningId TEXT,
            kanjiComponents TEXT,
            kanaComponents TEXT,
            wordIdList TEXT,
            translation TEXT,
            source TEXT,
            audioUrl TEXT
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS AntonymWord (
            antonymWordId TEXT NOT NULL PRIMARY KEY,
            wordId TEXT,
            correspondingWordId TEXT,
            kanjiComponents TEXT,
            kanaComponents TEXT
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS SynonymWord (
            synonymWordId TEXT NOT NULL PRIMARY KEY,
            wordId TEXT,
            correspondingWordId TEXT,
            kanjiComponents TEXT,
            kanaComponents TEXT
        )
    """)

    cursor.execute("""
        CREATE TABLE IF NOT EXISTS WordCollocation (
            wordCollocationId TEXT NOT NULL PRIMARY KEY,
            wordId TEXT,
            kanjiComponents TEXT,
            kanaComponents TEXT,
            wordIdList TEXT,
            translation TEXT,
            source TEXT,
            audioUrl TEXT
        )
    """)


def create_indexes(cursor):
    cursor.execute("CREATE INDEX IF NOT EXISTS index_WordMeaning_wordId ON WordMeaning(wordId)")
    cursor.execute("CREATE INDEX IF NOT EXISTS index_WordSentence_wordId ON WordSentence(wordId)")
    cursor.execute("CREATE INDEX IF NOT EXISTS index_AntonymWord_wordId ON AntonymWord(wordId)")
    cursor.execute("CREATE INDEX IF NOT EXISTS index_SynonymWord_wordId ON SynonymWord(wordId)")
    cursor.execute("CREATE INDEX IF NOT EXISTS index_WordCollocation_wordId ON WordCollocation(wordId)")


def batch_insert(cursor, conn, table, columns, rows):
    if not rows:
        return
    placeholders = ','.join(['?'] * len(columns))
    col_str = ','.join(columns)
    sql = f"INSERT INTO {table} ({col_str}) VALUES ({placeholders})"
    for i in range(0, len(rows), BATCH_SIZE):
        batch = rows[i:i + BATCH_SIZE]
        cursor.executemany(sql, batch)
        conn.commit()
    print(f"  Inserted {len(rows)} rows into {table}")


def main():
    print(f"Loading data from {INPUT_PATH}...")
    with open(INPUT_PATH, 'r', encoding='utf-8') as f:
        data = json.load(f)

    print(f"  Words: {len(data.get('words', []))}")
    print(f"  BasicWords: {len(data.get('basicWords', []))}")
    print(f"  WordMeanings: {len(data.get('wordMeanings', []))}")
    print(f"  WordSentences: {len(data.get('wordSentences', []))}")
    print(f"  AntonymWords: {len(data.get('antonymWords', []))}")
    print(f"  SynonymWords: {len(data.get('synonymWords', []))}")

    os.makedirs(os.path.dirname(OUTPUT_PATH), exist_ok=True)
    if os.path.exists(OUTPUT_PATH):
        os.remove(OUTPUT_PATH)

    print(f"Creating database at {OUTPUT_PATH}...")
    conn = sqlite3.connect(OUTPUT_PATH)
    cursor = conn.cursor()

    # PRAGMA优化
    cursor.execute("PRAGMA synchronous = OFF")
    cursor.execute("PRAGMA cache_size = -20000")
    cursor.execute("PRAGMA temp_store = MEMORY")

    create_tables(cursor)

    # 插入room_master_table
    cursor.execute("INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (42, ?)",
                   (IDENTITY_HASH,))

    # 批量导入数据
    print("Importing data...")

    word_columns = ['wordId', 'antonymWordIdList', 'synonymWordIdList', 'collocationIdList', 'meaningIdList', 'sentenceIdList']
    word_rows = [(w['wordId'], w.get('antonymWordIdList', '[]'), w.get('synonymWordIdList', '[]'),
                  w.get('collocationIdList', '[]'), w.get('meaningIdList', '[]'), w.get('sentenceIdList', '[]'))
                 for w in data.get('words', [])]
    batch_insert(cursor, conn, 'Word', word_columns, word_rows)

    basic_columns = ['wordId', 'kanjiComponents', 'kanaComponents', 'audioUrl', 'accentMark', 'mnemonic']
    basic_rows = [(b['wordId'], b.get('kanjiComponents', '[]'), b.get('kanaComponents', '[]'),
                   b.get('audioUrl', ''), b.get('accentMark', ''), b.get('mnemonic', ''))
                  for b in data.get('basicWords', [])]
    batch_insert(cursor, conn, 'BasicWord', basic_columns, basic_rows)

    meaning_columns = ['wordMeaningId', 'wordId', 'originalDefinition', 'translationDefinition', 'partOfSpeech']
    meaning_rows = [(m['wordMeaningId'], m.get('wordId', ''), m.get('originalDefinition', ''),
                     m.get('translationDefinition', ''), m.get('partOfSpeech', ''))
                    for m in data.get('wordMeanings', [])]
    batch_insert(cursor, conn, 'WordMeaning', meaning_columns, meaning_rows)

    sentence_columns = ['wordSentenceId', 'wordId', 'wordMeaningId', 'kanjiComponents', 'kanaComponents',
                        'wordIdList', 'translation', 'source', 'audioUrl']
    sentence_rows = [(s['wordSentenceId'], s.get('wordId', ''), s.get('wordMeaningId', ''),
                      s.get('kanjiComponents', '[]'), s.get('kanaComponents', '[]'),
                      s.get('wordIdList', '[]'), s.get('translation', ''), s.get('source', 'EDRG'),
                      s.get('audioUrl', ''))
                     for s in data.get('wordSentences', [])]
    batch_insert(cursor, conn, 'WordSentence', sentence_columns, sentence_rows)

    antonym_columns = ['antonymWordId', 'wordId', 'correspondingWordId', 'kanjiComponents', 'kanaComponents']
    antonym_rows = [(a['antonymWordId'], a.get('wordId', ''), a.get('correspondingWordId', ''),
                     a.get('kanjiComponents', '[]'), a.get('kanaComponents', '[]'))
                    for a in data.get('antonymWords', [])]
    batch_insert(cursor, conn, 'AntonymWord', antonym_columns, antonym_rows)

    synonym_columns = ['synonymWordId', 'wordId', 'correspondingWordId', 'kanjiComponents', 'kanaComponents']
    synonym_rows = [(s['synonymWordId'], s.get('wordId', ''), s.get('correspondingWordId', ''),
                     s.get('kanjiComponents', '[]'), s.get('kanaComponents', '[]'))
                    for s in data.get('synonymWords', [])]
    batch_insert(cursor, conn, 'SynonymWord', synonym_columns, synonym_rows)

    collocation_columns = ['wordCollocationId', 'wordId', 'kanjiComponents', 'kanaComponents',
                           'wordIdList', 'translation', 'source', 'audioUrl']
    collocation_rows = [(c['wordCollocationId'], c.get('wordId', ''), c.get('kanjiComponents', '[]'),
                          c.get('kanaComponents', '[]'), c.get('wordIdList', '[]'), c.get('translation', ''),
                          c.get('source', ''), c.get('audioUrl', ''))
                         for c in data.get('wordCollocations', [])]
    batch_insert(cursor, conn, 'WordCollocation', collocation_columns, collocation_rows)

    MAX_SENTENCES_PER_WORD = 6

    # 从word_sentences_data.json导入例句
    if os.path.exists(SENTENCES_PATH):
        print(f"Loading sentences from {SENTENCES_PATH}...")
        with open(SENTENCES_PATH, 'r', encoding='utf-8') as f:
            sent_data = json.load(f)
        sentences = sent_data['wordSentences']
        print(f"  Sentences: {len(sentences)}")

        # Limit sentences per wordId
        word_sent_count = defaultdict(int)
        limited_sentences = []
        skipped = 0
        for s in sentences:
            wid = s.get('wordId', '')
            if wid and wid != "0":
                if word_sent_count[wid] >= MAX_SENTENCES_PER_WORD:
                    skipped += 1
                    continue
                word_sent_count[wid] += 1
            limited_sentences.append(s)
        print(f"  After limiting to {MAX_SENTENCES_PER_WORD}/word: {len(limited_sentences)} (skipped {skipped})")

        sentence_rows = [(s['wordSentenceId'], s.get('wordId', ''), s.get('wordMeaningId', ''),
                          s.get('kanjiComponents', '[]'), s.get('kanaComponents', '[]'),
                          s.get('wordIdList', '[]'), s.get('translation', ''), s.get('source', 'EDRG'),
                          s.get('audioUrl', ''))
                         for s in limited_sentences]
        batch_insert(cursor, conn, 'WordSentence', sentence_columns, sentence_rows)

        # 按wordId分组例句ID，更新Word.sentenceIdList
        print("Updating Word.sentenceIdList...")
        word_to_sent_ids = defaultdict(list)
        for s in limited_sentences:
            wid = s.get('wordId', '')
            if wid and wid != "0":
                word_to_sent_ids[wid].append(s['wordSentenceId'])

        updated = 0
        for wid, sent_ids in word_to_sent_ids.items():
            cursor.execute("UPDATE Word SET sentenceIdList = ? WHERE wordId = ?",
                           (json.dumps(sent_ids), wid))
            updated += 1
            if updated % 5000 == 0:
                conn.commit()
        conn.commit()
        print(f"  Updated {updated} words with sentence IDs")
    else:
        print(f"  {SENTENCES_PATH} not found, skipping sentences")

    print("Skipping indexes (Room Entity does not declare them, schema validation would fail)")

    # 恢复PRAGMA
    cursor.execute("PRAGMA synchronous = NORMAL")

    conn.commit()
    print("Commit done!")

    db_size = os.path.getsize(OUTPUT_PATH)
    print(f"Before VACUUM: {db_size / 1024 / 1024:.1f} MB")

    # VACUUM压缩
    print("VACUUMing...")
    try:
        cursor.execute("VACUUM")
        print("VACUUM done!")
    except Exception as e:
        print(f"VACUUM failed: {e}")

    conn.close()

    db_size = os.path.getsize(OUTPUT_PATH)
    print(f"Done! Database size: {db_size / 1024 / 1024:.1f} MB")


if __name__ == '__main__':
    main()