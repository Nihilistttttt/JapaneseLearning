"""
将JMDict JSON转换为Room可导入的JSON格式 (v10)

核心改进：
  - v10: 使用JMDict完整版（217974词），替代common-only版（22620词）
  - v10: 含汉字词排在前面，纯假名词排在后面
  - v9: 使用JmdictFurigana权威拆分数据（96.5%覆盖率）
  - v9: 单字库仅使用KANJIDIC2+JmdictFurigana提取（排除words.json/xlsx等可能有错的来源）
  - v9: 拆分优先级：JmdictFurigana逐字拆分 > 回溯算法拆分 > 整体标注
  - 每个字符一个slot：汉字slot用字典回溯匹配，假名slot直接匹配1个字符
  - 送假名自然处理：べ等假名slot匹配后标注空字符串（不重复标注）
  - 纯假名词配空字符串（不标注）
  - 无法拆分的词整体配假名
  - 罕用汉字降级：所有kanji形式不common时，用kana形式作为primary
"""

import json
import os
import sys

sys.path.insert(0, os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '分词拆字工具'))
from kanji_kana_splitter import split_word, is_kana_char, normalize_kana_annotations, katakana_to_hiragana

KA_REPEAT_CHAR = '々'

POS_MAP = {
    'n': 'NOUN', 'n-suf': 'NOUN', 'n-pref': 'NOUN', 'n-adv': 'NOUN',
    'n-t': 'NOUN', 'pn': 'NOUN', 'num': 'NOUN', 'adj-no': 'NOUN',
    'adj-n': 'NOUN', 'adj-t': 'NOUN', 'adv-n': 'NOUN',
    'v1': 'VERB', 'v5b': 'VERB', 'v5g': 'VERB', 'v5k': 'VERB',
    'v5m': 'VERB', 'v5n': 'VERB', 'v5r': 'VERB', 'v5r-i': 'VERB',
    'v5s': 'VERB', 'v5t': 'VERB', 'v5u': 'VERB', 'v5u-s': 'VERB',
    'v5aru': 'VERB', 'v5k-s': 'VERB', 'v5m-s': 'VERB', 'v5r-s': 'VERB',
    'v5b-s': 'VERB', 'v5g-s': 'VERB', 'v5n-s': 'VERB', 'v5t-s': 'VERB',
    'vk': 'VERB', 'vs': 'VERB', 'vs-i': 'VERB', 'vs-s': 'VERB',
    'vz': 'VERB', 'vi': 'VERB', 'vt': 'VERB', 'v2a-s': 'VERB',
    'v4h': 'VERB', 'v4r': 'VERB', 'v4s': 'VERB', 'aux-v': 'VERB',
    'adj-i': 'ADJECTIVE', 'adj-na': 'ADJECTIVE', 'adj-ix': 'ADJECTIVE',
    'adj-f': 'ADJECTIVE', 'adj-ku': 'ADJECTIVE', 'adj-shiku': 'ADJECTIVE',
    'adv': 'ADVERB', 'adv-to': 'ADVERB',
}

TRANSLATION_PLACEHOLDER = '（待翻译）'



def map_pos(jmdict_pos_list):
    for tag in jmdict_pos_list:
        if tag in POS_MAP:
            return POS_MAP[tag]
    return 'UNKNOWN'


def load_final_kanji_dict(dict_path):
    """加载权威单字库（KANJIDIC2 + JmdictFurigana提取）"""
    print(f'Loading final kanji dict from {dict_path}...')
    with open(dict_path, encoding='utf-8') as f:
        raw = json.load(f)
    kanji_dict = {}
    for kanji, readings in raw.items():
        kanji_dict[kanji] = readings
    print(f'  Loaded {len(kanji_dict)} kanji entries')
    return kanji_dict


def load_furigana_splits(splits_path):
    """加载JmdictFurigana权威逐字拆分数据"""
    print(f'Loading JmdictFurigana per-char splits from {splits_path}...')
    with open(splits_path, encoding='utf-8') as f:
        raw = json.load(f)
    splits = {}
    for key, data in raw.items():
        kc = data['kanji_comps']
        sc = data['kana_comps']
        kc_norm, sc_norm = normalize_kana_annotations(kc, sc)
        splits[key] = (kc_norm, sc_norm)
    print(f'  Loaded {len(splits)} splits')
    return splits


def split_ka_repeat_word(kanji_str, kana_str, kanji_dict):
    """
    处理含々的叠词拆分。
    
    々=前一个汉字的重复，但读音可能发生浊音化等变化。
    策略：将々替换为前一个汉字，然后尝试用回溯算法拆分。
    如果替换后能拆分，则将々对应的部分标注出来。
    
    例如：屡々-しばしば → 屡→し, 々→ば, し→し, ば→ば
          色々-いろいろ → 色→い, ろ→ろ, 色→い, ろ→ろ
          人々-ひとびと → 人→ひと, 々→びと(浊音化)
    """
    if KA_REPEAT_CHAR not in kanji_str:
        return None

    expanded = kanji_str.replace(KA_REPEAT_CHAR + KA_REPEAT_CHAR, '')
    if expanded == kanji_str:
        positions = []
        chars = list(kanji_str)
        for i, c in enumerate(chars):
            if c == KA_REPEAT_CHAR:
                prev_kanji = None
                for j in range(i - 1, -1, -1):
                    if not is_kana_char(chars[j]) and chars[j] != KA_REPEAT_CHAR:
                        prev_kanji = chars[j]
                        break
                if prev_kanji:
                    chars[i] = prev_kanji
                    positions.append((i, prev_kanji))
        expanded = ''.join(chars)

    result = split_word(expanded, kana_str, kanji_dict)
    if not result:
        return None

    expanded_kanji, expanded_kana = result

    final_kanji = list(kanji_str)
    final_kana = [''] * len(kanji_str)

    exp_idx = 0
    for i, c in enumerate(kanji_str):
        if c == KA_REPEAT_CHAR:
            prev_kanji = None
            for j in range(i - 1, -1, -1):
                if not is_kana_char(kanji_str[j]) and kanji_str[j] != KA_REPEAT_CHAR:
                    prev_kanji = kanji_str[j]
                    break
            if prev_kanji:
                for k in range(exp_idx, len(expanded_kanji)):
                    if expanded_kanji[k] == prev_kanji and expanded_kana[k]:
                        final_kana[i] = expanded_kana[k]
                        exp_idx = k + 1
                        break
        elif is_kana_char(c):
            while exp_idx < len(expanded_kanji) and expanded_kanji[exp_idx] != c:
                exp_idx += 1
            if exp_idx < len(expanded_kanji):
                exp_idx += 1
        else:
            for k in range(exp_idx, len(expanded_kanji)):
                if expanded_kanji[k] == c and expanded_kana[k]:
                    final_kana[i] = expanded_kana[k]
                    exp_idx = k + 1
                    break

    return normalize_kana_annotations(final_kanji, final_kana)


def _dakuon_to_seion(text):
    """浊音→清音转换（用于叠词拆分时尝试清音匹配）"""
    dakuon_map = {
        'が': 'か', 'ぎ': 'き', 'ぐ': 'く', 'げ': 'け', 'ご': 'こ',
        'ざ': 'さ', 'じ': 'し', 'ず': 'す', 'ぜ': 'せ', 'ぞ': 'そ',
        'だ': 'た', 'ぢ': 'ち', 'づ': 'つ', 'で': 'て', 'ど': 'と',
        'ば': 'は', 'び': 'ひ', 'ぶ': 'ふ', 'べ': 'へ', 'ぼ': 'ほ',
        'ガ': 'カ', 'ギ': 'キ', 'グ': 'ク', 'ゲ': 'ケ', 'ゴ': 'コ',
        'ザ': 'サ', 'ジ': 'シ', 'ズ': 'ス', 'ゼ': 'セ', 'ゾ': 'ソ',
        'ダ': 'タ', 'ヂ': 'チ', 'ヅ': 'ツ', 'デ': 'テ', 'ド': 'ト',
        'バ': 'ハ', 'ビ': 'ヒ', 'ブ': 'フ', 'ベ': 'ヘ', 'ボ': 'ホ',
    }
    return ''.join(dakuon_map.get(c, c) for c in text)


def split_repeated_word(kanji_str, kana_str, kanji_dict):
    """
    处理叠词拆分（如限り限り-ぎりぎり）。
    
    策略：
    1. 检查kanji_str是否是ABAB形式
    2. 用前半AB拆分，尝试原始读音和清音化版本
    3. 后半直接从kana_str中按前半结构分配假名（允许浊音化）
    """
    half_len = len(kanji_str) // 2
    if half_len == 0 or len(kanji_str) % 2 != 0:
        return None

    first_half = kanji_str[:half_len]
    second_half = kanji_str[half_len:]

    if first_half != second_half:
        return None

    kana_half_len = len(kana_str) // 2
    first_kana_part = kana_str[:kana_half_len]
    second_kana_part = kana_str[kana_half_len:]

    first_result = split_word(first_half, first_kana_part, kanji_dict)

    if not first_result:
        seion_kana = _dakuon_to_seion(first_kana_part)
        if seion_kana != first_kana_part:
            first_result = split_word(first_half, seion_kana, kanji_dict)

    if not first_result:
        return None

    first_kanji_comps, first_kana_comps = first_result

    full_kanji = first_kanji_comps + first_kanji_comps

    kana_pos = 0
    second_kana_comps = []
    for kc, fc in zip(first_kanji_comps, first_kana_comps):
        if not fc:
            if all(is_kana_char(c) for c in kc):
                kana_pos_end = kana_pos + len(kc)
                if kana_pos_end > len(second_kana_part):
                    return None
                second_kana_comps.append('')
                kana_pos = kana_pos_end
            else:
                second_kana_comps.append('')
        else:
            kana_pos_end = kana_pos + len(fc)
            if kana_pos_end > len(second_kana_part):
                return None
            second_kana_comps.append(second_kana_part[kana_pos:kana_pos_end])
            kana_pos = kana_pos_end

    if kana_pos != len(second_kana_part):
        return None

    full_kana = first_kana_comps + second_kana_comps

    return normalize_kana_annotations(full_kanji, full_kana)






def find_word_id_by_text(text, kanji_to_ids, kana_to_ids):
    ids = kanji_to_ids.get(text)
    if ids:
        return ids[0]
    ids = kana_to_ids.get(text)
    if ids:
        return ids[0]
    return None


def convert_jmdict_to_room(input_path, output_path, furigana_splits_path=None, kanji_dict_path=None):
    print(f'Loading JMDict from {input_path}...')
    with open(input_path, 'r', encoding='utf-8') as f:
        data = json.load(f)

    jmdict_words = data['words']
    print(f'Loaded {len(jmdict_words)} words')

    kanji_to_ids = {}
    kana_to_ids = {}
    id_to_primary = {}
    for w in jmdict_words:
        wid = w['id']
        for k in w.get('kanji', []):
            kanji_to_ids.setdefault(k['text'], []).append(wid)
        for k in w.get('kana', []):
            kana_to_ids.setdefault(k['text'], []).append(wid)
        kanji_list = w.get('kanji', [])
        kana_list = w.get('kana', [])
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

    furigana_splits = {}
    if furigana_splits_path and os.path.exists(furigana_splits_path):
        furigana_splits = load_furigana_splits(furigana_splits_path)
    print(f'Furigana splits: {len(furigana_splits)}', flush=True)

    kanji_dict = {}
    if kanji_dict_path and os.path.exists(kanji_dict_path):
        kanji_dict = load_final_kanji_dict(kanji_dict_path)

    words_kanji = []
    words_kana = []
    basic_words_kanji = []
    basic_words_kana = []
    word_meanings = []
    antonym_words = []
    synonym_words = []

    meaning_id_counter = 0
    antonym_id_counter = 0
    synonym_id_counter = 0

    skipped_no_kanji_no_kana = 0
    furigana_hit = 0
    backtrack_hit = 0
    ka_repeat_hit = 0
    repeated_hit = 0
    fallback_hit = 0

    word_id_to_components = {}

    # Pass 1: Build word_id_to_components for all words
    print("Pass 1: Building word_id_to_components...")
    for w in jmdict_words:
        word_id = w['id']
        kanji_list = w.get('kanji', [])
        kana_list = w.get('kana', [])

        if not kanji_list and not kana_list:
            continue

        primary_kanji, primary_kana = id_to_primary[word_id]

        word_key = f"{primary_kanji}-{primary_kana}"
        if word_key in furigana_splits:
            kanji_components, kana_components = furigana_splits[word_key]
            furigana_hit += 1
        elif all(is_kana_char(c) for c in primary_kanji):
            kanji_components = [primary_kanji]
            kana_components = ['']
        elif kanji_dict and any(not is_kana_char(c) for c in primary_kanji):
            bt_result = split_word(primary_kanji, primary_kana, kanji_dict)
            if bt_result:
                kanji_components, kana_components = bt_result
                backtrack_hit += 1
            elif KA_REPEAT_CHAR in primary_kanji:
                ka_result = split_ka_repeat_word(primary_kanji, primary_kana, kanji_dict)
                if ka_result:
                    kanji_components, kana_components = ka_result
                    ka_repeat_hit += 1
                else:
                    kanji_components = [primary_kanji]
                    kana_components = [primary_kana]
                    fallback_hit += 1
            else:
                rep_result = split_repeated_word(primary_kanji, primary_kana, kanji_dict)
                if rep_result:
                    kanji_components, kana_components = rep_result
                    repeated_hit += 1
                else:
                    kanji_components = [primary_kanji]
                    kana_components = [primary_kana]
                    fallback_hit += 1
        else:
            kanji_components = [primary_kanji]
            kana_components = [primary_kana]
            fallback_hit += 1

        word_id_to_components[word_id] = (kanji_components, kana_components)

    print(f"  word_id_to_components: {len(word_id_to_components)} entries")
    print(f"  Split sources: furigana={furigana_hit}, backtrack={backtrack_hit}, ka_repeat={ka_repeat_hit}, repeated={repeated_hit}, fallback={fallback_hit}")

    # Pass 2: Process all words (meanings, antonyms, synonyms)
    print("Pass 2: Processing words, meanings, antonyms, synonyms...")
    for w in jmdict_words:
        word_id = w['id']
        kanji_list = w.get('kanji', [])
        kana_list = w.get('kana', [])

        if not kanji_list and not kana_list:
            skipped_no_kanji_no_kana += 1
            continue

        primary_kanji, primary_kana = id_to_primary[word_id]
        kanji_components, kana_components = word_id_to_components[word_id]

        meaning_ids = []
        antonym_ids = []
        synonym_ids = []

        for sense in w.get('sense', []):
            meaning_id_counter += 1
            mid = str(meaning_id_counter)

            pos = map_pos(sense.get('partOfSpeech', []))

            glosses = sense.get('gloss', [])
            eng_glosses = [g for g in glosses if g.get('lang', 'eng') == 'eng']
            if not eng_glosses:
                # Skip non-English senses entirely
                continue
            original_def = '; '.join(g['text'] for g in eng_glosses if g.get('text'))
            if not original_def:
                continue

            meaning_ids.append(mid)

            word_meanings.append({
                'wordMeaningId': mid,
                'wordId': word_id,
                'originalDefinition': original_def,
                'translationDefinition': TRANSLATION_PLACEHOLDER,
                'partOfSpeech': pos
            })

            for ant in sense.get('antonym', []):
                if not ant or len(ant) < 1:
                    continue
                ref_text = ant[0]
                ant_word_id = find_word_id_by_text(ref_text, kanji_to_ids, kana_to_ids)
                if not ant_word_id:
                    continue

                antonym_id_counter += 1
                aid = str(antonym_id_counter)
                antonym_ids.append(aid)

                ref_comps = word_id_to_components.get(ant_word_id)
                if ref_comps:
                    ant_kanji_comps, ant_kana_comps = ref_comps
                else:
                    ant_kanji_comps = [ref_text]
                    ant_kana_comps = ['']

                antonym_words.append({
                    'antonymWordId': aid,
                    'wordId': word_id,
                    'correspondingWordId': ant_word_id,
                    'kanjiComponents': ant_kanji_comps,
                    'kanaComponents': ant_kana_comps
                })

            for rel in sense.get('related', []):
                if not rel or len(rel) < 1:
                    continue
                ref_text = rel[0]
                rel_word_id = find_word_id_by_text(ref_text, kanji_to_ids, kana_to_ids)
                if not rel_word_id:
                    continue

                synonym_id_counter += 1
                sid = str(synonym_id_counter)
                synonym_ids.append(sid)

                ref_comps = word_id_to_components.get(rel_word_id)
                if ref_comps:
                    rel_kanji_comps, rel_kana_comps = ref_comps
                else:
                    rel_kanji_comps = [ref_text]
                    rel_kana_comps = ['']

                synonym_words.append({
                    'synonymWordId': sid,
                    'wordId': word_id,
                    'correspondingWordId': rel_word_id,
                    'kanjiComponents': rel_kanji_comps,
                    'kanaComponents': rel_kana_comps
                })

        word_entry = {
            'wordId': word_id,
            'antonymWordIdList': json.dumps(antonym_ids),
            'synonymWordIdList': json.dumps(synonym_ids),
            'collocationIdList': json.dumps([]),
            'meaningIdList': json.dumps(meaning_ids),
            'sentenceIdList': json.dumps([])
        }

        basic_entry = {
            'wordId': word_id,
            'kanjiComponents': json.dumps(kanji_components, ensure_ascii=False),
            'kanaComponents': json.dumps(kana_components, ensure_ascii=False),
            'audioUrl': '',
            'accentMark': '',
            'mnemonic': ''
        }

        has_cjk = any(0x4E00 <= ord(c) <= 0x9FFF or ord(c) == 0x3005 for c in primary_kanji)
        if has_cjk:
            words_kanji.append(word_entry)
            basic_words_kanji.append(basic_entry)
        else:
            words_kana.append(word_entry)
            basic_words_kana.append(basic_entry)

    for aw in antonym_words:
        aw['kanjiComponents'] = json.dumps(aw['kanjiComponents'], ensure_ascii=False)
        aw['kanaComponents'] = json.dumps(aw['kanaComponents'], ensure_ascii=False)
    for sw in synonym_words:
        sw['kanjiComponents'] = json.dumps(sw['kanjiComponents'], ensure_ascii=False)
        sw['kanaComponents'] = json.dumps(sw['kanaComponents'], ensure_ascii=False)

    words = words_kanji + words_kana
    basic_words = basic_words_kanji + basic_words_kana

    result = {
        'words': words,
        'basicWords': basic_words,
        'wordMeanings': word_meanings,
        'wordSentences': [],
        'wordCollocations': [],
        'antonymWords': antonym_words,
        'synonymWords': synonym_words
    }

    print(f'\nConversion results:')
    print(f'  Words: {len(words)} (kanji={len(words_kanji)}, kana={len(words_kana)})')
    print(f'  BasicWords: {len(basic_words)} (kanji={len(basic_words_kanji)}, kana={len(basic_words_kana)})')
    print(f'  WordMeanings: {len(word_meanings)}')
    print(f'  AntonymWords: {len(antonym_words)}')
    print(f'  SynonymWords: {len(synonym_words)}')
    print(f'  WordSentences: 0 (pending Tatoeba)')
    print(f'  WordCollocations: 0 (pending Kanjium)')
    print(f'  Skipped (no kanji/kana): {skipped_no_kanji_no_kana}')
    print(f'  Split sources: furigana={furigana_hit}, backtrack={backtrack_hit}, ka_repeat={ka_repeat_hit}, repeated={repeated_hit}, fallback={fallback_hit}')

    print(f'\nWriting to {output_path}...')
    with open(output_path, 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, separators=(',', ':'))

    file_size = os.path.getsize(output_path)
    print(f'Done! File size: {file_size / 1024 / 1024:.1f} MB')


if __name__ == '__main__':
    input_path = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理\jmdict-all-3.6.2.json'
    output_path = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\room_import.json'
    furigana_splits_path = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理\furigana_per_char_splits.json'
    kanji_dict_path = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理\final_kanji_dict.json'

    os.makedirs(os.path.dirname(output_path), exist_ok=True)
    convert_jmdict_to_room(input_path, output_path, furigana_splits_path, kanji_dict_path)
