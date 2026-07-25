"""
从EDRG + Tatoeba提取WordSentence数据 (v3)

数据合并策略：
  1. EDRG 147835条日英例句（B行分词+词素匹配JMDict词条）
  2. Tatoeba 14895条日中例句（独立添加，用Tatoeba ID关联JMDict词条）
  3. 优先使用中文翻译，英文翻译作为备选
"""

import gzip
import json
import os
import re
from collections import defaultdict

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理'
OUT = os.path.join(BASE, 'word_sentences_data.json')

TATOEBA_JPN_CMN = os.path.join(BASE, 'tatoeba_jpn_cmn_pairs.json')
EDRG_PATH = os.path.join(BASE, 'examples.utf.gz')
JMDICT_PATH = os.path.join(BASE, 'jmdict-all-3.6.2.json')


def load_jmdict_word_ids():
    print("Loading JMDict word IDs...")
    kanji_to_ids = defaultdict(list)
    kana_to_ids = defaultdict(list)
    with open(JMDICT_PATH, encoding='utf-8') as f:
        data = json.load(f)
    for word in data['words']:
        wid = str(word['id'])
        for kanji in word.get('kanji', []):
            kanji_to_ids[kanji['text']].append(wid)
        for kana in word.get('kana', []):
            kana_to_ids[kana['text']].append(wid)
    print(f"  kanji: {len(kanji_to_ids)}, kana: {len(kana_to_ids)}")
    return dict(kanji_to_ids), dict(kana_to_ids)


def parse_b_line(b_line):
    tokens = b_line.strip().split(' ')
    kanji_comps = []
    kana_comps = []
    surfaces = []

    for token in tokens:
        if not token:
            continue
        m = re.match(r'(.+?)\(([^)]+)\)', token)
        if m:
            surface_raw = m.group(1)
            reading = m.group(2)
        else:
            surface_raw = token
            reading = None

        surface_clean = re.sub(r'\[[\d]*\]', '', surface_raw)
        surface_clean = re.sub(r'\{[^}]*\}', '', surface_clean)
        surface_clean = surface_clean.replace('~', '')
        if not surface_clean:
            continue

        k_chars = []
        r_chars = []
        has_kanji = any('\u4e00' <= c <= '\u9fff' or c == '々' for c in surface_clean)

        for ch in surface_clean:
            k_chars.append(ch)
            if has_kanji and reading and ('\u4e00' <= ch <= '\u9fff' or ch == '々'):
                r_chars.append(reading)
            else:
                r_chars.append(ch)

        kanji_comps.append(k_chars)
        kana_comps.append(r_chars)

        base_form = re.sub(r'\([^)]*\)', '', token)
        base_form = re.sub(r'\[[\d]*\]', '', base_form)
        base_form = re.sub(r'\{[^}]*\}', '', base_form)
        base_form = base_form.replace('~', '')
        surfaces.append(base_form)

    return kanji_comps, kana_comps, surfaces


def find_word_id_for_surfaces(surfaces, kanji_to_ids, kana_to_ids):
    for surface in surfaces:
        if surface in kanji_to_ids:
            return kanji_to_ids[surface][0]
        if surface in kana_to_ids:
            return kana_to_ids[surface][0]
        clean = re.sub(r'\([^)]*\)', '', surface)
        if clean and clean in kanji_to_ids:
            return kanji_to_ids[clean][0]
        if clean and clean in kana_to_ids:
            return kana_to_ids[clean][0]
    return "0"


def process_edrg_examples(kanji_to_ids, kana_to_ids):
    print("Processing EDRG examples...")
    sentences = []
    sentence_id_counter = 0
    current_a = None
    jmdict_hit = 0

    with gzip.open(EDRG_PATH, 'rt', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue

            if line.startswith('A:'):
                parts = line[2:].split('\t')
                jpn_text = parts[0] if parts else ''
                eng_text = parts[1].split('#ID=')[0] if len(parts) > 1 else ''
                current_a = {'jpn': jpn_text, 'eng': eng_text}

            elif line.startswith('B:') and current_a:
                b_text = line[2:]
                kanji_comps, kana_comps, surfaces = parse_b_line(b_text)
                if not kanji_comps:
                    current_a = None
                    continue

                word_id = find_word_id_for_surfaces(surfaces, kanji_to_ids, kana_to_ids)
                if word_id != "0":
                    jmdict_hit += 1

                sentence_id_counter += 1
                word_id_list = ["0"] * len(kanji_comps)

                sentences.append({
                    'wordSentenceId': str(sentence_id_counter),
                    'wordId': word_id,
                    'wordMeaningId': '',
                    'kanjiComponents': json.dumps(kanji_comps, ensure_ascii=False),
                    'kanaComponents': json.dumps(kana_comps, ensure_ascii=False),
                    'wordIdList': json.dumps(word_id_list),
                    'translation': current_a['eng'],
                    'source': 'EDRG',
                    'audioUrl': ''
                })
                current_a = None

    print(f"  EDRG: {len(sentences)} entries, JMDict linked: {jmdict_hit}")
    return sentences, sentence_id_counter


def add_tatoeba_sentences(sentences, start_id, kanji_to_ids, kana_to_ids):
    """添加Tatoeba日中例句（无B行分词，需要简单分词）"""
    print("Adding Tatoeba Japanese-Chinese sentences...")
    tatoeba_path = os.path.join(BASE, 'tatoeba_jpn_cmn_pairs.json')

    with open(tatoeba_path, encoding='utf-8') as f:
        data = json.load(f)

    sentence_id = start_id
    added = 0
    jmdict_hit = 0

    for jpn_id, entry in data.items():
        jpn_text = entry['jpn_text']
        cmn_translations = entry['cmn_translations']
        jmdict_id = entry.get('jmdict_id', '')

        # 简单分词：按字符拆分，汉字和假名分组
        kanji_comps = []
        kana_comps = []
        current_kanji = []
        current_kana = []

        for ch in jpn_text:
            if ch in '。、！？・…「」『』（）':
                if current_kanji:
                    kanji_comps.append(current_kanji)
                    kana_comps.append([''] * len(current_kanji))
                    current_kanji = []
                if current_kana:
                    kanji_comps.append(current_kana)
                    kana_comps.append(current_kana[:])
                    current_kana = []
                continue
            elif '\u4e00' <= ch <= '\u9fff' or ch == '々':
                if current_kana:
                    kanji_comps.append(current_kana)
                    kana_comps.append(current_kana[:])
                    current_kana = []
                current_kanji.append(ch)
            else:
                if current_kanji:
                    kanji_comps.append(current_kanji)
                    kana_comps.append([''] * len(current_kanji))
                    current_kanji = []
                current_kana.append(ch)

        if current_kanji:
            kanji_comps.append(current_kanji)
            kana_comps.append([''] * len(current_kanji))
        if current_kana:
            kanji_comps.append(current_kana)
            kana_comps.append(current_kana[:])

        if not kanji_comps:
            continue

        # 确定wordId
        word_id = jmdict_id if jmdict_id else "0"
        if word_id == "0":
            # 尝试通过文本匹配
            if jpn_text in kanji_to_ids:
                word_id = kanji_to_ids[jpn_text][0]
            elif jpn_text in kana_to_ids:
                word_id = kana_to_ids[jpn_text][0]
        if word_id != "0":
            jmdict_hit += 1

        sentence_id += 1
        word_id_list = ["0"] * len(kanji_comps)

        sentences.append({
            'wordSentenceId': str(sentence_id),
            'wordId': word_id,
            'wordMeaningId': '',
            'kanjiComponents': json.dumps(kanji_comps, ensure_ascii=False),
            'kanaComponents': json.dumps(kana_comps, ensure_ascii=False),
            'wordIdList': json.dumps(word_id_list),
            'translation': cmn_translations[0] if cmn_translations else '',
            'source': 'Tatoeba',
            'audioUrl': ''
        })
        added += 1

    print(f"  Tatoeba: {added} entries, JMDict linked: {jmdict_hit}")
    return sentences, sentence_id


def main():
    kanji_to_ids, kana_to_ids = load_jmdict_word_ids()
    sentences, last_id = process_edrg_examples(kanji_to_ids, kana_to_ids)
    sentences, last_id = add_tatoeba_sentences(sentences, last_id, kanji_to_ids, kana_to_ids)

    print(f"\nTotal: {len(sentences)} WordSentence entries")
    print(f"Writing to {OUT}...")
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump({'wordSentences': sentences}, f, ensure_ascii=False, separators=(',', ':'))
    print(f"Done! File size: {os.path.getsize(OUT) / 1024 / 1024:.1f} MB")


if __name__ == '__main__':
    main()
