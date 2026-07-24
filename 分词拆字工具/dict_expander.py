"""
统计扩充单字库

核心思路：
  1. 从已拆分的词中提取 单汉字→假名 的映射
  2. 对于两汉字词，如果已知一个汉字的读音，可以推断另一个汉字的读音
  3. 统计每个推断结果的频率，高频结果更可信
  4. 将高置信度的新映射添加回单字库

使用方式：
  from dict_expander import expand_kanji_dict
  expanded_dict = expand_kanji_dict(base_dict, split_results)
"""

from collections import defaultdict
from kanji_kana_splitter import is_kana_char, split_word


def extract_single_kanji_readings(split_results):
    """
    从已拆分结果中提取 单汉字→假名 映射。
    
    split_results: list of (kanji_str, kana_str, kanji_comps, kana_comps, source)
    返回: dict[str, dict[str, int]]  {kanji: {kana: count}}
    """
    readings = defaultdict(lambda: defaultdict(int))

    for kanji_str, kana_str, kanji_comps, kana_comps, source in split_results:
        if source != 'split':
            continue
        for kc, sc in zip(kanji_comps, kana_comps):
            if not kc or not sc:
                continue
            if all(is_kana_char(c) for c in kc):
                continue
            if len(kc) == 1 and not is_kana_char(kc):
                readings[kc][sc] += 1

    return dict(readings)


def infer_readings_from_two_kanji_words(split_results, base_dict):
    """
    从两汉字词推断新读音。
    
    对于 kanji_str=AB, kana_str=XY 的词：
      如果A的读音已知（A→X），则推断B→Y
      如果B的读音已知（B→Y），则推断A→X
    
    返回: dict[str, dict[str, int]]  {kanji: {kana: count}}
    """
    inferred = defaultdict(lambda: defaultdict(int))

    for kanji_str, kana_str, kanji_comps, kana_comps, source in split_results:
        if source != 'split':
            continue

        kanji_chars = [(kc, sc) for kc, sc in zip(kanji_comps, kana_comps)
                       if kc and sc and not all(is_kana_char(c) for c in kc)]

        if len(kanji_chars) != 2:
            continue

        (k1, r1), (k2, r2) = kanji_chars

        k1_known = k1 in base_dict and r1 in base_dict[k1]
        k2_known = k2 in base_dict and r2 in base_dict[k2]

        if k1_known and not k2_known and len(k2) == 1:
            inferred[k2][r2] += 1
        if k2_known and not k1_known and len(k1) == 1:
            inferred[k1][r1] += 1

    return dict(inferred)


def expand_kanji_dict(base_dict, split_results, min_count=3, max_readings=8):
    """
    扩充单字库。
    
    base_dict: dict[str, list[str]]  原始单汉字→假名列表
    split_results: list of (kanji_str, kana_str, kanji_comps, kana_comps, source)
    min_count: 推断结果至少出现几次才采纳
    max_readings: 每个汉字最多保留几个读音
    
    返回: (expanded_dict, new_entries)
      expanded_dict: 扩充后的字典
      new_entries: dict[str, list[str]]  新增的映射
    """
    direct_readings = extract_single_kanji_readings(split_results)
    inferred_readings = infer_readings_from_two_kanji_words(split_results, base_dict)

    expanded = {k: list(v) for k, v in base_dict.items()}
    new_entries = {}

    for kanji, reading_counts in direct_readings.items():
        if kanji not in expanded:
            sorted_readings = sorted(reading_counts.items(), key=lambda x: -x[1])
            accepted = [r for r, cnt in sorted_readings if cnt >= 1][:max_readings]
            if accepted:
                expanded[kanji] = accepted
                new_entries[kanji] = accepted

    for kanji, reading_counts in inferred_readings.items():
        sorted_readings = sorted(reading_counts.items(), key=lambda x: -x[1])
        for reading, count in sorted_readings:
            if count < min_count:
                continue
            if kanji not in expanded:
                expanded[kanji] = [reading]
                new_entries.setdefault(kanji, []).append(reading)
            elif reading not in expanded[kanji]:
                if len(expanded[kanji]) < max_readings:
                    expanded[kanji].append(reading)
                    new_entries.setdefault(kanji, []).append(reading)

    return expanded, new_entries


def iterative_expand(base_dict, word_list, max_iterations=5, min_count=3):
    """
    迭代扩充：每次用扩充后的字典重新拆分，直到没有新的拆分结果。
    
    base_dict: dict[str, list[str]]
    word_list: list of (kanji_str, kana_str)
    max_iterations: 最大迭代次数
    
    返回: (final_dict, all_split_results, iteration_log)
    """
    current_dict = {k: list(v) for k, v in base_dict.items()}
    iteration_log = []

    for iteration in range(max_iterations):
        from kanji_kana_splitter import batch_split_words
        split_results = batch_split_words(word_list, current_dict)

        split_count = sum(1 for r in split_results if r[4] == 'split')
        fallback_count = sum(1 for r in split_results if r[4] == 'fallback')

        expanded_dict, new_entries = expand_kanji_dict(current_dict, split_results, min_count=min_count)

        new_count = len(new_entries)
        iteration_log.append({
            'iteration': iteration + 1,
            'split': split_count,
            'fallback': fallback_count,
            'new_entries': new_count,
            'new_entries_detail': {k: v for k, v in new_entries.items()}
        })

        if new_count == 0:
            break

        current_dict = expanded_dict

    final_results = batch_split_words(word_list, current_dict)
    return current_dict, final_results, iteration_log