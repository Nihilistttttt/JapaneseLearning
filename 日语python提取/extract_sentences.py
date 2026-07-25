"""
从EDRG + Tatoeba提取WordSentence数据 (v4)

v4改进：
  - EDRG B行分词使用JmdictFurigana权威拆分+回溯匹配，正确分配假名注音到每个汉字
  - 之前：汉字位置存整词读音（如"忙"→"いそがしい"），假名位置重复存假名
  - 现在：汉字位置存逐字注音（如"忙"→"いそが"），假名/送假名位置存空字符串

数据合并策略：
  1. EDRG 147835条日英例句（B行分词+词素匹配JMDict词条）
  2. Tatoeba 14895条日中例句（独立添加，用Tatoeba ID关联JMDict词条）
"""

import gzip
import json
import os
import re
import sys
from collections import defaultdict

sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '分词拆字工具'))
from kanji_kana_splitter import split_word, is_kana_char, normalize_kana_annotations

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理'
OUT = os.path.join(BASE, 'word_sentences_data.json')

TATOEBA_JPN_CMN = os.path.join(BASE, 'tatoeba_jpn_cmn_pairs.json')
EDRG_PATH = os.path.join(BASE, 'examples.utf.gz')
JMDICT_PATH = os.path.join(BASE, 'jmdict-all-3.6.2.json')


def load_jmdict_word_ids():
    print("Loading JMDict word IDs...")
    kanji_to_ids = defaultdict(list)
    kana_to_ids = defaultdict(list)
    id_to_primary = {}
    with open(JMDICT_PATH, encoding='utf-8') as f:
        data = json.load(f)
    for word in data['words']:
        wid = str(word['id'])
        for kanji in word.get('kanji', []):
            kanji_to_ids[kanji['text']].append(wid)
        for kana in word.get('kana', []):
            kana_to_ids[kana['text']].append(wid)
        kanji_list = word.get('kanji', [])
        kana_list = word.get('kana', [])
        pk = ''
        pkana = ''
        if kanji_list:
            common_k = [k for k in kanji_list if k.get('common', False)]
            pk = common_k[0]['text'] if common_k else kanji_list[0]['text']
        if kana_list:
            common_k = [k for k in kana_list if k.get('common', False)]
            pkana = common_k[0]['text'] if common_k else kana_list[0]['text']
        if not pk:
            pk = pkana
        id_to_primary[wid] = (pk, pkana)
    print(f"  kanji: {len(kanji_to_ids)}, kana: {len(kana_to_ids)}")
    return dict(kanji_to_ids), dict(kana_to_ids), id_to_primary


def parse_b_line(b_line, kanji_dict, furigana_splits, kanji_to_ids, kana_to_ids, id_to_primary):
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

        has_kanji = any('\u4e00' <= c <= '\u9fff' or c == '々' for c in surface_clean)

        if has_kanji and not reading:
            # 有汉字但EDRG没给读音：尝试从JMDict查找
            ids = kanji_to_ids.get(surface_clean)
            if not ids:
                ids = kana_to_ids.get(surface_clean)
            if ids:
                reading = id_to_primary.get(ids[0], ('', ''))[1]

        if has_kanji and reading:
            word_key = f"{surface_clean}-{reading}"
            if word_key in furigana_splits:
                kc, kc_kana = furigana_splits[word_key]
            elif kanji_dict:
                result = split_word(surface_clean, reading, kanji_dict)
                if result:
                    kc, kc_kana = result
                else:
                    kc, kc_kana = normalize_kana_annotations(
                        list(surface_clean),
                        [reading if ('\u4e00' <= ch <= '\u9fff' or ch == '々') else '' for ch in surface_clean]
                    )
            else:
                kc = list(surface_clean)
                kc_kana = [reading if ('\u4e00' <= ch <= '\u9fff' or ch == '々') else '' for ch in surface_clean]
            kanji_comps.append(kc)
            kana_comps.append(kc_kana)
        else:
            # 纯假名/标点：逐字拆分，假名位置kana为空
            kc = list(surface_clean)
            kc_kana = [''] * len(surface_clean)
            kanji_comps.append(kc)
            kana_comps.append(kc_kana)

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


def process_edrg_examples(kanji_to_ids, kana_to_ids, id_to_primary, kanji_dict, furigana_splits):
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
                kanji_c, kana_c, surfaces = parse_b_line(b_text, kanji_dict, furigana_splits, kanji_to_ids, kana_to_ids, id_to_primary)
                if not kanji_c:
                    current_a = None
                    continue

                word_id = find_word_id_for_surfaces(surfaces, kanji_to_ids, kana_to_ids)
                if word_id != "0":
                    jmdict_hit += 1

                sentence_id_counter += 1
                word_id_list = ["0"] * len(kanji_c)

                sentences.append({
                    'wordSentenceId': str(sentence_id_counter),
                    'wordId': word_id,
                    'wordMeaningId': '',
                    'kanjiComponents': json.dumps(kanji_c, ensure_ascii=False),
                    'kanaComponents': json.dumps(kana_c, ensure_ascii=False),
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


def load_furigana_splits():
    splits_path = os.path.join(BASE, 'furigana_per_char_splits.json')
    print(f"Loading furigana splits from {splits_path}...")
    with open(splits_path, encoding='utf-8') as f:
        raw = json.load(f)
    splits = {}
    for key, data in raw.items():
        kc = data['kanji_comps']
        sc = data['kana_comps']
        kc_norm, sc_norm = normalize_kana_annotations(kc, sc)
        splits[key] = (kc_norm, sc_norm)
    print(f"  Loaded {len(splits)} splits")
    return splits


def load_kanji_dict():
    dict_path = os.path.join(BASE, 'final_kanji_dict.json')
    print(f"Loading kanji dict from {dict_path}...")
    with open(dict_path, encoding='utf-8') as f:
        raw = json.load(f)
    kanji_dict = {}
    for kanji, readings in raw.items():
        kanji_dict[kanji] = readings
    print(f"  Loaded {len(kanji_dict)} kanji entries")
    return kanji_dict


def main():
    kanji_to_ids, kana_to_ids, id_to_primary = load_jmdict_word_ids()
    furigana_splits = load_furigana_splits()
    kanji_dict = load_kanji_dict()
    sentences, last_id = process_edrg_examples(kanji_to_ids, kana_to_ids, id_to_primary, kanji_dict, furigana_splits)
    sentences, last_id = add_tatoeba_sentences(sentences, last_id, kanji_to_ids, kana_to_ids)

    print(f"\nTotal: {len(sentences)} WordSentence entries")
    print(f"Writing to {OUT}...")
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump({'wordSentences': sentences}, f, ensure_ascii=False, separators=(',', ':'))
    print(f"Done! File size: {os.path.getsize(OUT) / 1024 / 1024:.1f} MB")


if __name__ == '__main__':
    main()
