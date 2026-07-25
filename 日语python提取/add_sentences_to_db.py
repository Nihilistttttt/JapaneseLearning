"""
在现有预构建DB上追加例句数据，避免重新加载190MB JSON

1. 加载word_sentences_data.json（87.5MB）
2. 插入WordSentence表
3. 更新Word.sentenceIdList
4. VACUUM压缩
"""

import json
import os
import sqlite3
import ijson
from collections import defaultdict

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn'
DATA_DIR = os.path.join(BASE, '日语数据处理')
DB_PATH = os.path.join(BASE, 'app', 'src', 'main', 'assets', 'databases', 'word_database.db')
SENTENCES_PATH = os.path.join(DATA_DIR, 'word_sentences_data.json')

BATCH_SIZE = 2000


def main():
    print(f"Loading sentences from {SENTENCES_PATH}...")
    with open(SENTENCES_PATH, 'r', encoding='utf-8') as f:
        sent_data = json.load(f)
    sentences = sent_data['wordSentences']
    print(f"  Sentences: {len(sentences)}")

    print(f"Opening database at {DB_PATH}...")
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()

    cursor.execute("PRAGMA synchronous = OFF")
    cursor.execute("PRAGMA cache_size = -20000")

    # 插入WordSentence
    print("Inserting WordSentence...")
    columns = ['wordSentenceId', 'wordId', 'wordMeaningId', 'kanjiComponents', 'kanaComponents',
               'wordIdList', 'translation', 'source', 'audioUrl']
    placeholders = ','.join(['?'] * len(columns))
    col_str = ','.join(columns)
    sql = f"INSERT INTO WordSentence ({col_str}) VALUES ({placeholders})"

    word_to_sent_ids = defaultdict(list)
    batch = []
    total = 0

    for s in sentences:
        row = (s['wordSentenceId'], s.get('wordId', ''), s.get('wordMeaningId', ''),
               s.get('kanjiComponents', '[]'), s.get('kanaComponents', '[]'),
               s.get('wordIdList', '[]'), s.get('translation', ''), s.get('source', 'EDRG'),
               s.get('audioUrl', ''))
        batch.append(row)
        total += 1

        wid = s.get('wordId', '')
        if wid and wid != "0":
            word_to_sent_ids[wid].append(s['wordSentenceId'])

        if len(batch) >= BATCH_SIZE:
            cursor.executemany(sql, batch)
            conn.commit()
            batch = []
            if total % 20000 == 0:
                print(f"  Inserted {total}...")

    if batch:
        cursor.executemany(sql, batch)
        conn.commit()

    print(f"  Inserted {total} rows into WordSentence")

    # 更新Word.sentenceIdList
    print("Updating Word.sentenceIdList...")
    updated = 0
    for wid, sent_ids in word_to_sent_ids.items():
        cursor.execute("UPDATE Word SET sentenceIdList = ? WHERE wordId = ?",
                       (json.dumps(sent_ids), wid))
        updated += 1
        if updated % 5000 == 0:
            conn.commit()
    conn.commit()
    print(f"  Updated {updated} words with sentence IDs")

    cursor.execute("PRAGMA synchronous = NORMAL")

    db_size = os.path.getsize(DB_PATH)
    print(f"Before VACUUM: {db_size / 1024 / 1024:.1f} MB")

    print("VACUUMing...")
    cursor.execute("VACUUM")
    print("VACUUM done!")

    conn.close()

    db_size = os.path.getsize(DB_PATH)
    print(f"Done! Database size: {db_size / 1024 / 1024:.1f} MB")


if __name__ == '__main__':
    main()