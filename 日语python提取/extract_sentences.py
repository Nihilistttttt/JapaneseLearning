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


def _is_kanji(ch):
    return '\u4e00' <= ch <= '\u9fff' or ch == '々'

def _is_kana(ch):
    return ('\u3040' <= ch <= '\u309F') or ('\u30A0' <= ch <= '\u30FF')

def derive_conjugated_surface(dict_surface, dict_reading, conj_reading):
    if not dict_surface or not dict_reading or not conj_reading:
        return conj_reading if conj_reading else dict_surface

    if conj_reading.startswith(dict_surface):
        return conj_reading

    kanji_count = 0
    for ch in dict_surface:
        if _is_kanji(ch):
            kanji_count += 1
        else:
            break

    if kanji_count == 0:
        return conj_reading

    kanji_part = dict_surface[:kanji_count]
    okurigana_part = dict_surface[kanji_count:]

    if okurigana_part and dict_reading.endswith(okurigana_part):
        kanji_reading = dict_reading[:-len(okurigana_part)]
    elif okurigana_part:
        kanji_reading = dict_reading
        for i in range(len(okurigana_part), 0, -1):
            if dict_reading.endswith(okurigana_part[:i]):
                kanji_reading = dict_reading[:-i]
                break
    else:
        kanji_reading = dict_reading

    if conj_reading.startswith(kanji_reading):
        new_okurigana = conj_reading[len(kanji_reading):]
        return kanji_part + new_okurigana

    if okurigana_part:
        common = 0
        for i in range(min(len(dict_reading), len(conj_reading))):
            if dict_reading[i] == conj_reading[i]:
                common += 1
            else:
                break

        if common > 0 and common >= len(kanji_reading) - 1:
            new_okurigana = conj_reading[common:]
            return kanji_part + new_okurigana

        if len(kanji_reading) <= 2 and not conj_reading.startswith(kanji_reading[:1]):
            return conj_reading

    if okurigana_part and len(conj_reading) > len(okurigana_part):
        return kanji_part + conj_reading[len(kanji_reading):]

    return conj_reading

def _merge_shared_kanji_kana(kc, kc_kana):
    """Merge consecutive kanji chars that share a single kana annotation.
    
    e.g. ['貴','方'] / ['あなた',''] → ['貴方'] / ['あなた']
         ['日','本','語'] / ['に','ほん','ご'] → unchanged (each kanji has its own kana)
    """
    if not kc or len(kc) <= 1:
        return kc, kc_kana
    
    merged_kc = []
    merged_kk = []
    i = 0
    while i < len(kc):
        if _is_kanji(kc[i]) and kc_kana[i]:
            # Start of a kanji group with kana - find consecutive kanji with empty kana
            kanji_group = kc[i]
            kana_val = kc_kana[i]
            j = i + 1
            while j < len(kc) and _is_kanji(kc[j]) and not kc_kana[j]:
                kanji_group += kc[j]
                j += 1
            merged_kc.append(kanji_group)
            merged_kk.append(kana_val)
            i = j
        else:
            merged_kc.append(kc[i])
            merged_kk.append(kc_kana[i])
            i += 1
    
    return merged_kc, merged_kk

def _split_reading_by_surface(surface, reading):
    """Split a reading into per-character annotations based on surface structure.
    
    For tokens like 帽子を取る with reading ぼうしをとる:
    - 帽子→ぼうし, を→(kana match), 取→と, る→(okurigana)
    - Result: ['ぼうし', '', 'と', ''] (kana chars get empty annotation)
    """
    if not surface or not reading:
        return None
    
    # Segment surface into kanji and kana groups
    segments = []
    i = 0
    while i < len(surface):
        if _is_kanji(surface[i]):
            start = i
            while i < len(surface) and _is_kanji(surface[i]):
                i += 1
            segments.append(('kanji', surface[start:i]))
        elif _is_kana(surface[i]):
            start = i
            while i < len(surface) and _is_kana(surface[i]):
                i += 1
            segments.append(('kana', surface[start:i]))
        else:
            segments.append(('other', surface[i]))
            i += 1
    
    # Match reading to segments left-to-right
    # For kana segments: they must match kana in the reading
    # For kanji segments: they consume the remaining reading between kana matches
    remaining = reading
    kanji_readings = {}  # segment_index -> reading for that kanji segment
    
    for seg_idx, (seg_type, seg_text) in enumerate(segments):
        if seg_type == 'kana':
            # Find this kana segment in the remaining reading
            idx = remaining.find(seg_text)
            if idx < 0:
                return None
            # Everything before this kana match belongs to preceding kanji segments
            if idx > 0:
                # Assign this reading to the most recent unmatched kanji segment
                for j in range(seg_idx - 1, -1, -1):
                    if segments[j][0] == 'kanji' and j not in kanji_readings:
                        kanji_readings[j] = remaining[:idx]
                        break
            remaining = remaining[idx + len(seg_text):]
    
    # Any remaining reading belongs to the last kanji segment
    if remaining:
        for j in range(len(segments) - 1, -1, -1):
            if segments[j][0] == 'kanji' and j not in kanji_readings:
                kanji_readings[j] = remaining
                break
    
    # Build per-char result
    result_kana = [''] * len(surface)
    for seg_idx, kanji_reading in kanji_readings.items():
        seg_type, seg_text = segments[seg_idx]
        pos = sum(len(segments[j][1]) for j in range(seg_idx))
        result_kana[pos] = kanji_reading
    
    return result_kana

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
    word_id_to_meaning_ids = {}
    with open(JMDICT_PATH, encoding='utf-8') as f:
        data = json.load(f)
    meaning_counter = 0
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
        meaning_ids = []
        for sense in word.get('sense', []):
            meaning_counter += 1
            glosses = sense.get('gloss', [])
            eng_glosses = [g for g in glosses if g.get('lang', 'eng') == 'eng']
            if eng_glosses:
                meaning_ids.append(str(meaning_counter))
        word_id_to_meaning_ids[wid] = meaning_ids
    print(f"  kanji: {len(kanji_to_ids)}, kana: {len(kana_to_ids)}")
    return dict(kanji_to_ids), dict(kana_to_ids), id_to_primary, word_id_to_meaning_ids


def parse_b_line(b_line, kanji_dict, furigana_splits, kanji_to_ids, kana_to_ids, id_to_primary):
    tokens = b_line.strip().split(' ')
    kanji_comps = []
    kana_comps = []
    surfaces = []
    token_word_ids = []

    for token in tokens:
        if not token:
            continue
        # Extract conjugation form from {xxx} if present (check full token first)
        conj_match = re.search(r'\{([^}]+)\}', token)
        conj_reading = None
        if conj_match:
            conj_reading = conj_match.group(1)

        m = re.match(r'(.+?)\(([^)]+)\)', token)
        if m:
            surface_raw = m.group(1)
            reading = m.group(2)
        else:
            surface_raw = re.sub(r'\{[^}]*\}', '', token)
            reading = None

        surface_clean = re.sub(r'\[[\d]*\]', '', surface_raw)
        surface_clean = re.sub(r'\{[^}]*\}', '', surface_clean)
        surface_clean = surface_clean.replace('~', '')
        if not surface_clean:
            continue

        has_kanji = any('\u4e00' <= c <= '\u9fff' or c == '々' for c in surface_clean)

        if has_kanji and not reading:
            ids = kanji_to_ids.get(surface_clean)
            if not ids:
                ids = kana_to_ids.get(surface_clean)
            if ids:
                reading = id_to_primary.get(ids[0], ('', ''))[1]

        # Derive conjugated surface form if {xxx} is present
        conj_has_kanji = conj_reading and any(_is_kanji(ch) for ch in conj_reading)
        if conj_reading and not conj_has_kanji and reading:
            # Pure kana conjugation: derive surface from dict form
            surface_display = derive_conjugated_surface(surface_clean, reading, conj_reading)
        elif conj_reading:
            # Mixed kanji+kana in {...}: use directly as surface_display
            surface_display = conj_reading
        else:
            surface_display = surface_clean

        has_kanji_display = any('\u4e00' <= c <= '\u9fff' or c == '々' for c in surface_display)

        if has_kanji_display and reading:
            if conj_reading:
                if conj_has_kanji:
                    # Mixed kanji+kana conjugation (e.g. 帰った, 面白かった, 帽子を取り)
                    # The kanji part should be same as dict form, only okurigana changed
                    # Use furigana splits for dict form to annotate kanji, okurigana gets empty
                    kc = list(surface_display)
                    kc_kana = [''] * len(kc)
                    # Try furigana splits for the dictionary form
                    word_key = f"{surface_clean}-{reading}"
                    if word_key in furigana_splits:
                        dict_kc, dict_kc_kana = furigana_splits[word_key]
                        # Map dict form annotations to conjugated form
                        # Kanji chars that appear in both get their readings from dict form
                        dict_idx = 0
                        for ci in range(len(kc)):
                            if _is_kanji(kc[ci]):
                                # Find corresponding kanji in dict form
                                while dict_idx < len(dict_kc) and not _is_kanji(dict_kc[dict_idx]):
                                    dict_idx += 1
                                if dict_idx < len(dict_kc) and dict_kc_kana[dict_idx]:
                                    kc_kana[ci] = dict_kc_kana[dict_idx]
                                    dict_idx += 1
                    else:
                        # No furigana splits: try _split_reading_by_surface
                        split_result = _split_reading_by_surface(surface_display, reading)
                        if split_result:
                            kc_kana = split_result
                        else:
                            # Last resort: put reading on first kanji
                            for ci in range(len(kc)):
                                if _is_kanji(kc[ci]):
                                    kc_kana[ci] = reading
                                    break
                else:
                    # Pure kana conjugation (e.g. かぶって from 被る)
                    # Derive per-char kana annotations
                    kanji_count = 0
                    for ch in surface_display:
                        if _is_kanji(ch):
                            kanji_count += 1
                        else:
                            break
                    kc = list(surface_display)
                    kc_kana = [''] * len(kc)
                    if kanji_count > 0:
                        # Determine kanji reading from dict_reading and okurigana
                        dict_kanji_count = 0
                        for ch in surface_clean:
                            if _is_kanji(ch):
                                dict_kanji_count += 1
                            else:
                                break
                        dict_okurigana = surface_clean[dict_kanji_count:]
                        if dict_okurigana and reading.endswith(dict_okurigana):
                            kanji_reading = reading[:-len(dict_okurigana)]
                        elif dict_okurigana:
                            kanji_reading = reading
                            for i in range(len(dict_okurigana), 0, -1):
                                if reading.endswith(dict_okurigana[:i]):
                                    kanji_reading = reading[:-i]
                                    break
                        else:
                            kanji_reading = reading
                        if kanji_count == 1:
                            kc_kana[0] = kanji_reading
                        else:
                            word_key = f"{surface_clean}-{reading}"
                            if word_key in furigana_splits:
                                dict_kc, dict_kc_kana = furigana_splits[word_key]
                                dict_kanji_readings = []
                                for idx, ch in enumerate(dict_kc):
                                    if _is_kanji(ch) and dict_kc_kana[idx]:
                                        dict_kanji_readings.append(dict_kc_kana[idx])
                                if len(dict_kanji_readings) == kanji_count:
                                    for idx in range(kanji_count):
                                        kc_kana[idx] = dict_kanji_readings[idx]
                                else:
                                    kc_kana[0] = kanji_reading
                            else:
                                kc_kana[0] = kanji_reading
            else:
                # Dictionary form: use furigana splits
                word_key = f"{surface_clean}-{reading}"
                if word_key in furigana_splits:
                    kc, kc_kana = furigana_splits[word_key]
                elif kanji_dict:
                    result = split_word(surface_clean, reading, kanji_dict)
                    if result:
                        kc, kc_kana = result
                    else:
                        kc = list(surface_display)
                        split_result = _split_reading_by_surface(surface_display, reading)
                        if split_result:
                            kc_kana = split_result
                        else:
                            kc_kana = [reading if _is_kanji(ch) else '' for ch in surface_display]
                else:
                    kc = list(surface_display)
                    split_result = _split_reading_by_surface(surface_display, reading)
                    if split_result:
                        kc_kana = split_result
                    else:
                        kc_kana = [reading if _is_kanji(ch) else '' for ch in surface_display]
            kanji_comps.append(kc)
            kana_comps.append(kc_kana)
        else:
            kc = list(surface_display)
            kc_kana = [''] * len(surface_display)
            kanji_comps.append(kc)
            kana_comps.append(kc_kana)

        base_form = re.sub(r'\([^)]*\)', '', token)
        base_form = re.sub(r'\[[\d]*\]', '', base_form)
        base_form = re.sub(r'\{[^}]*\}', '', base_form)
        base_form = base_form.replace('~', '')
        surfaces.append(base_form)

        # 查找当前token的wordId
        token_wid = "0"
        for lookup in [surface_clean, base_form]:
            if lookup in kanji_to_ids:
                token_wid = kanji_to_ids[lookup][0]
                break
            if lookup in kana_to_ids:
                token_wid = kana_to_ids[lookup][0]
                break
        token_word_ids.append(token_wid)

    # Post-process: merge consecutive kanji with shared kana annotation
    # e.g. ['貴','方'] / ['あなた',''] → ['貴方'] / ['あなた']
    merged_kanji_comps = []
    merged_kana_comps = []
    for kc, kc_kana in zip(kanji_comps, kana_comps):
        mk, mv = _merge_shared_kanji_kana(kc, kc_kana)
        merged_kanji_comps.append(mk)
        merged_kana_comps.append(mv)

    return merged_kanji_comps, merged_kana_comps, surfaces, token_word_ids


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


def process_edrg_examples(kanji_to_ids, kana_to_ids, id_to_primary, word_id_to_meaning_ids, kanji_dict, furigana_splits):
    print("Processing EDRG examples...")
    sentences = []
    sentence_id_counter = 0
    current_a = None
    jmdict_hit = 0
    word_sent_counter = defaultdict(int)

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
                kanji_c, kana_c, surfaces, token_wids = parse_b_line(b_text, kanji_dict, furigana_splits, kanji_to_ids, kana_to_ids, id_to_primary)
                if not kanji_c:
                    current_a = None
                    continue

                word_id = find_word_id_for_surfaces(surfaces, kanji_to_ids, kana_to_ids)
                if word_id != "0":
                    jmdict_hit += 1

                meaning_id = ''
                if word_id != "0" and word_id in word_id_to_meaning_ids:
                    mids = word_id_to_meaning_ids[word_id]
                    if mids:
                        # Round-robin assign sentences to meanings
                        idx = word_sent_counter[word_id] % len(mids)
                        meaning_id = mids[idx]
                        word_sent_counter[word_id] += 1

                sentence_id_counter += 1

                sentences.append({
                    'wordSentenceId': str(sentence_id_counter),
                    'wordId': word_id,
                    'wordMeaningId': meaning_id,
                    'kanjiComponents': json.dumps(kanji_c, ensure_ascii=False),
                    'kanaComponents': json.dumps(kana_c, ensure_ascii=False),
                    'wordIdList': json.dumps(token_wids),
                    'translation': current_a['eng'],
                    'source': 'EDRG',
                    'audioUrl': ''
                })
                current_a = None

    print(f"  EDRG: {len(sentences)} entries, JMDict linked: {jmdict_hit}")
    return sentences, sentence_id_counter


def add_tatoeba_sentences(sentences, start_id, kanji_to_ids, kana_to_ids, word_id_to_meaning_ids):
    """添加Tatoeba日中例句（无B行分词，需要简单分词）"""
    print("Adding Tatoeba Japanese-Chinese sentences...")
    tatoeba_path = os.path.join(BASE, 'tatoeba_jpn_cmn_pairs.json')

    with open(tatoeba_path, encoding='utf-8') as f:
        data = json.load(f)

    tatoeba_word_sent_counter = defaultdict(int)
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

        meaning_id = ''
        if word_id != "0" and word_id in word_id_to_meaning_ids:
            mids = word_id_to_meaning_ids[word_id]
            if mids:
                idx = tatoeba_word_sent_counter[word_id] % len(mids)
                meaning_id = mids[idx]
                tatoeba_word_sent_counter[word_id] += 1

        sentence_id += 1
        # 为每个token查找wordId
        token_word_ids = []
        for kc in kanji_comps:
            text = ''.join(kc)
            wid = "0"
            if text in kanji_to_ids:
                wid = kanji_to_ids[text][0]
            elif text in kana_to_ids:
                wid = kana_to_ids[text][0]
            token_word_ids.append(wid)

        sentences.append({
            'wordSentenceId': str(sentence_id),
            'wordId': word_id,
            'wordMeaningId': meaning_id,
            'kanjiComponents': json.dumps(kanji_comps, ensure_ascii=False),
            'kanaComponents': json.dumps(kana_comps, ensure_ascii=False),
            'wordIdList': json.dumps(token_word_ids),
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
    kanji_to_ids, kana_to_ids, id_to_primary, word_id_to_meaning_ids = load_jmdict_word_ids()
    furigana_splits = load_furigana_splits()
    kanji_dict = load_kanji_dict()
    sentences, last_id = process_edrg_examples(kanji_to_ids, kana_to_ids, id_to_primary, word_id_to_meaning_ids, kanji_dict, furigana_splits)
    sentences, last_id = add_tatoeba_sentences(sentences, last_id, kanji_to_ids, kana_to_ids, word_id_to_meaning_ids)

    print(f"\nTotal: {len(sentences)} WordSentence entries")
    print(f"Writing to {OUT}...")
    with open(OUT, 'w', encoding='utf-8') as f:
        json.dump({'wordSentences': sentences}, f, ensure_ascii=False, separators=(',', ':'))
    print(f"Done! File size: {os.path.getsize(OUT) / 1024 / 1024:.1f} MB")


if __name__ == '__main__':
    main()
