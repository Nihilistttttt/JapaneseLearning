"""
日语单词 汉字-假名 拆分引擎

核心算法：
  将kanji_str的每个字符视为一个slot，按顺序消耗kana_str：
  - 假名slot：直接匹配kana_str中对应位置的1个字符
  - 汉字slot：从字典中查找可能的假名读音，回溯匹配kana_str中的连续字符
  
  这自然处理了送假名问题：食べ物 → [食(たべ), べ(べ), 物(もの)]
  但后处理时，べ配了べ，normalize_kana_annotations会把べ→べ改为べ→''

输入格式：kanji_str(汉字形式), kana_str(假名读音), kanji_dict(单汉字→假名列表)
输出格式：(kanji_components, kana_components) 或 None
"""

from collections import defaultdict


HIRAGANA_RANGE = (0x3040, 0x309F)
KATAKANA_RANGE = (0x30A0, 0x30FF)
PROLONGED_SOUND_MARK = 0x30FC


def is_kana_char(c):
    code = ord(c)
    return (HIRAGANA_RANGE[0] <= code <= HIRAGANA_RANGE[1]) or \
           (KATAKANA_RANGE[0] <= code <= KATAKANA_RANGE[1]) or \
           code == PROLONGED_SOUND_MARK


def is_fullwidth_latin(c):
    code = ord(c)
    return 0xFF01 <= code <= 0xFF5E


def is_cjk_punctuation(c):
    code = ord(c)
    return (0x3000 <= code <= 0x303F) or \
           (0xFF01 <= code <= 0xFF0F) or \
           (0xFF1A <= code <= 0xFF20) or \
           (0xFF3B <= code <= 0xFF40) or \
           (0xFF5B <= code <= 0xFF60)


def katakana_to_hiragana(text):
    return ''.join(chr(ord(c) - 0x60) if 0x30A0 <= ord(c) <= 0x30FF else c for c in text)


def backtrack_split(kanji_str, kana_str, kanji_dict, max_backtrack_depth=5000):
    """
    回溯算法拆分。
    
    将kanji_str中每个字符视为一个slot：
    - 假名slot：匹配kana_str当前位置的1个字符（片假名/平假名兼容）
    - 汉字slot：从字典中查找可能的假名读音，回溯匹配
    
    返回 (kanji_components, kana_components) 或 None
    """
    if not kanji_str or not kana_str:
        return None

    if all(is_kana_char(c) for c in kanji_str):
        return [kanji_str], ['']

    has_non_kana = any(not is_kana_char(c) for c in kanji_str)
    if not has_non_kana:
        return [kanji_str], ['']

    for c in kanji_str:
        if not is_kana_char(c) and not is_fullwidth_latin(c) and not is_cjk_punctuation(c):
            if c not in kanji_dict:
                return None

    result_kanji = []
    result_kana = []
    call_count = [0]

    def backtrack(kanji_idx, kana_idx):
        call_count[0] += 1
        if call_count[0] > max_backtrack_depth:
            return False

        if kanji_idx == len(kanji_str):
            return kana_idx == len(kana_str)

        c = kanji_str[kanji_idx]

        if is_kana_char(c):
            if kana_idx >= len(kana_str):
                return False
            if katakana_to_hiragana(kana_str[kana_idx]) == katakana_to_hiragana(c):
                result_kanji.append(c)
                result_kana.append(c)
                if backtrack(kanji_idx + 1, kana_idx + 1):
                    return True
                result_kanji.pop()
                result_kana.pop()
            return False

        if is_fullwidth_latin(c) or is_cjk_punctuation(c):
            result_kanji.append(c)
            result_kana.append('')
            if backtrack(kanji_idx + 1, kana_idx):
                return True
            result_kanji.pop()
            result_kana.pop()
            return False

        if c not in kanji_dict:
            return None

        possible_kanas = sorted(kanji_dict[c], key=len, reverse=True)

        extended_kanas = list(possible_kanas)
        for kana in possible_kanas:
            next_kanji_idx = kanji_idx + 1
            while next_kanji_idx < len(kanji_str) and is_kana_char(kanji_str[next_kanji_idx]):
                suffix = kanji_str[next_kanji_idx]
                if kana.endswith(suffix) and len(kana) > len(suffix):
                    truncated = kana[:-len(suffix)]
                    if truncated and truncated not in extended_kanas:
                        extended_kanas.append(truncated)
                next_kanji_idx += 1

        for kana in sorted(extended_kanas, key=len, reverse=True):
            end = kana_idx + len(kana)
            if end > len(kana_str):
                continue
            if kana_str[kana_idx:end] != kana:
                continue

            result_kanji.append(c)
            result_kana.append(kana)

            if backtrack(kanji_idx + 1, end):
                return True

            result_kanji.pop()
            result_kana.pop()

        return False

    success = backtrack(0, 0)

    if not success:
        return None

    return result_kanji, result_kana


def normalize_kana_annotations(kanji_comps, kana_comps):
    """
    后处理：
    - 假名部分不标注（kanji[i]全是假名且kana[i]==kanji[i]时置空）
    - 送假名标注自己→改为空字符串
    """
    result_kana = list(kana_comps)
    for i in range(len(kanji_comps)):
        if i >= len(result_kana):
            break
        kc = kanji_comps[i]
        if not kc:
            continue
        if all(is_kana_char(c) for c in kc):
            if result_kana[i] == kc:
                result_kana[i] = ''
    return kanji_comps, result_kana


def split_word(kanji_str, kana_str, kanji_dict, max_backtrack_depth=5000):
    """
    统一拆分入口。
    
    返回 (kanji_components, kana_components) 或 None
    """
    if not kanji_str or not kana_str:
        return None

    result = backtrack_split(kanji_str, kana_str, kanji_dict, max_backtrack_depth)
    if result:
        return normalize_kana_annotations(result[0], result[1])

    return None


def batch_split_words(word_list, kanji_dict, progress_callback=None):
    """
    批量拆分单词。
    
    word_list: list of (kanji_str, kana_str)
    返回: list of (kanji_str, kana_str, kanji_comps, kana_comps, source)
      source: 'split' | 'fallback'
    """
    results = []
    for idx, (kanji_str, kana_str) in enumerate(word_list):
        result = split_word(kanji_str, kana_str, kanji_dict)
        if result:
            kanji_comps, kana_comps = result
            results.append((kanji_str, kana_str, kanji_comps, kana_comps, 'split'))
        else:
            results.append((kanji_str, kana_str, [kanji_str], [kana_str], 'fallback'))
        if progress_callback and idx % 1000 == 0:
            progress_callback(idx, len(word_list))
    return results
