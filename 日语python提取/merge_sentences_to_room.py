"""
将例句数据合并到room_import.json.gz (v11)

1. 从room_import.json.gz加载现有数据
2. 加载word_sentences_data.json
3. 按wordId分组例句，更新WordEntity的sentenceIdList
4. 将wordSentences写入
5. 重新gzip压缩
"""

import json
import os
import gzip
import shutil
from collections import defaultdict

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn'
DATA_DIR = os.path.join(BASE, '日语数据处理')
ROOM_IMPORT_PATH = os.path.join(BASE, 'app', 'src', 'main', 'assets', 'room_import.json')
ROOM_IMPORT_GZ_PATH = os.path.join(BASE, 'app', 'src', 'main', 'assets', 'room_import.json.gz')
SENTENCES_PATH = os.path.join(DATA_DIR, 'word_sentences_data.json')


def main():
    print("Loading room_import.json...")
    with open(ROOM_IMPORT_PATH, encoding='utf-8') as f:
        room_data = json.load(f)
    print(f"  Words: {len(room_data['words'])}")
    print(f"  BasicWords: {len(room_data['basicWords'])}")
    print(f"  WordMeanings: {len(room_data['wordMeanings'])}")

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

    # 先写临时JSON文件
    tmp_json = os.path.join(DATA_DIR, 'room_import_v11.json')
    print(f"Writing temporary JSON...")
    with open(tmp_json, 'w', encoding='utf-8') as f:
        json.dump(room_data, f, ensure_ascii=False, separators=(',', ':'))
    json_size = os.path.getsize(tmp_json)
    print(f"  JSON size: {json_size / 1024 / 1024:.1f} MB")

    # Gzip压缩
    print("Compressing to room_import.json.gz...")
    with open(tmp_json, 'rb') as f_in:
        with gzip.open(ROOM_IMPORT_GZ_PATH, 'wb') as f_out:
            shutil.copyfileobj(f_in, f_out)

    gz_size = os.path.getsize(ROOM_IMPORT_GZ_PATH)
    print(f"  Gzipped size: {gz_size / 1024 / 1024:.1f} MB")

    # 删除临时文件
    os.remove(tmp_json)
    print("Done!")


if __name__ == '__main__':
    main()
