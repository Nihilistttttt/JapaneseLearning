"""
将例句数据合并到room_import.json

1. 从room_import.json加载现有数据
2. 加载word_sentences_data.json
3. 按wordId分组例句，更新WordEntity的sentenceIdList
4. 将wordSentences写入
5. 重新写入room_import.json
"""

import json
import os
from collections import defaultdict

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn'
DATA_DIR = os.path.join(BASE, '日语数据处理')
ROOM_IMPORT_PATH = os.path.join(BASE, 'app', 'src', 'main', 'assets', 'room_import.json')
SENTENCES_PATH = os.path.join(DATA_DIR, 'word_sentences_data.json')


def main():
    print("Loading room_import.json...")
    with open(ROOM_IMPORT_PATH, encoding='utf-8') as f:
        room_data = json.load(f)
    print(f"  Words: {len(room_data['words'])}")
    print(f"  BasicWords: {len(room_data['basicWords'])}")
    print(f"  WordMeanings: {len(room_data['wordMeanings'])}")
    print(f"  WordSentences (before): {len(room_data.get('wordSentences', []))}")

    print("Loading word_sentences_data.json...")
    with open(SENTENCES_PATH, encoding='utf-8') as f:
        sent_data = json.load(f)
    sentences = sent_data['wordSentences']
    print(f"  Sentences: {len(sentences)}")

    # 按wordId分组例句
    print("Grouping sentences by wordId...")
    word_to_sentences = defaultdict(list)
    for sent in sentences:
        wid = sent['wordId']
        if wid != "0":
            word_to_sentences[wid].append(sent['wordSentenceId'])

    print(f"  {len(word_to_sentences)} words have sentences")

    # 更新WordEntity的sentenceIdList
    print("Updating WordEntity sentenceIdLists...")
    updated = 0
    for word in room_data['words']:
        wid = word['wordId']
        if wid in word_to_sentences:
            word['sentenceIdList'] = json.dumps(word_to_sentences[wid])
            updated += 1

    print(f"  Updated {updated} words")

    # 设置wordSentences
    room_data['wordSentences'] = sentences

    # 写入room_import.json
    print(f"Writing to {ROOM_IMPORT_PATH}...")
    with open(ROOM_IMPORT_PATH, 'w', encoding='utf-8') as f:
        json.dump(room_data, f, ensure_ascii=False, separators=(',', ':'))

    json_size = os.path.getsize(ROOM_IMPORT_PATH)
    print(f"  JSON size: {json_size / 1024 / 1024:.1f} MB")
    print("Done!")


if __name__ == '__main__':
    main()
