"""
构建权威拆分数据和最终单字库

数据源优先级：
  1. JmdictFurigana — 234744条已拆分数据，99.0%覆盖JMDict common
  2. 拆分引擎+合并字典 — 补充JmdictFurigana未覆盖的
  3. xlsx — 补充上述两者都未覆盖的

输出：
  - furigana_per_char_splits.json: 逐字格式拆分结果
  - final_kanji_dict.json: 最终单字库
  - jmdict_common_coverage.json: JMDict common覆盖率报告
"""

import json
import sys
from collections import defaultdict

DATA_DIR = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理'


def kata_to_hira(text):
    return ''.join(chr(ord(c) - 0x60) if 0x30A0 <= ord(c) <= 0x30FF else c for c in text)


def is_kana_char(c):
    if len(c) != 1:
        return False
    code = ord(c)
    return (0x3040 <= code <= 0x309F) or (0x30A0 <= code <= 0x30FF) or code == 0x30FC


def convert_furigana_to_per_char(entry):
    furis = entry['furigana']
    kanji_comps = []
    kana_comps = []

    for part in furis:
        ruby = part.get('ruby', '')
        rt = part.get('rt', '')
        rt = kata_to_hira(rt)

        if not rt:
            for c in ruby:
                kanji_comps.append(c)
                kana_comps.append('')
        else:
            cjk_count = sum(1 for c in ruby if 0x4E00 <= ord(c) <= 0x9FFF or ord(c) == 0x3005)
            kana_in_ruby = [c for c in ruby if is_kana_char(c)]

            if cjk_count == 0:
                for c in ruby:
                    kanji_comps.append(c)
                    kana_comps.append('')
            elif cjk_count == 1 and not kana_in_ruby:
                kanji_comps.append(ruby)
                kana_comps.append(rt)
            elif cjk_count == 1 and kana_in_ruby:
                kanji_char = [c for c in ruby if 0x4E00 <= ord(c) <= 0x9FFF or ord(c) == 0x3005][0]
                kana_suffix = ''.join(kana_in_ruby)

                if rt.endswith(kana_suffix) and len(rt) > len(kana_suffix):
                    kanji_reading = rt[:-len(kana_suffix)]
                else:
                    kanji_reading = rt

                kanji_comps.append(kanji_char)
                kana_comps.append(kanji_reading)

                for c in kana_suffix:
                    kanji_comps.append(c)
                    kana_comps.append('')
            elif cjk_count > 1:
                kanji_comps.append(ruby)
                kana_comps.append(rt)
            else:
                kanji_comps.append(ruby)
                kana_comps.append(rt)

    return kanji_comps, kana_comps


def build_furigana_splits():
    print('=== 构建JmdictFurigana逐字拆分 ===')
    with open(f'{DATA_DIR}/JmdictFurigana.json', encoding='utf-8-sig') as f:
        furi_data = json.load(f)

    result = {}
    stats = {'total': len(furi_data), 'converted': 0, 'skip_no_cjk': 0, 'skip_mismatch': 0}

    for entry in furi_data:
        text = entry['text']
        reading = kata_to_hira(entry['reading'])
        key = f'{text}-{reading}'

        has_cjk = any(0x4E00 <= ord(c) <= 0x9FFF or ord(c) == 0x3005 for c in text)
        if not has_cjk:
            stats['skip_no_cjk'] += 1
            continue

        kc, sc = convert_furigana_to_per_char(entry)

        if ''.join(kc) != text:
            continue

        kana_rec = ''.join(sc[i] if sc[i] else kata_to_hira(kc[i]) for i in range(len(kc)))
        if kana_rec != reading:
            stats['skip_mismatch'] += 1
            continue

        result[key] = {
            'kanji_str': text,
            'kana_str': reading,
            'kanji_comps': kc,
            'kana_comps': sc,
            'source': 'furigana'
        }
        stats['converted'] += 1

    print(f'  总条目: {stats["total"]}')
    print(f'  成功转换: {stats["converted"]}')
    print(f'  跳过(无汉字): {stats["skip_no_cjk"]}')
    print(f'  跳过(验证失败): {stats["skip_mismatch"]}')

    with open(f'{DATA_DIR}/furigana_per_char_splits.json', 'w', encoding='utf-8') as f:
        json.dump(result, f, ensure_ascii=False, indent=2)
    print(f'  已保存 furigana_per_char_splits.json')

    return result


def build_final_kanji_dict():
    print('\n=== 构建最终单字库 ===')

    merged = defaultdict(set)

    # 1. words.json (list of {kanji, kana})
    with open(f'{DATA_DIR}/words.json', encoding='utf-8') as f:
        words_data = json.load(f)
    count = 0
    for item in words_data:
        merged[item['kanji']].add(kata_to_hira(item['kana']))
        count += 1
    print(f'  words.json: {len(set(item["kanji"] for item in words_data))} 个汉字, {count} 个读音')

    # 2. kanjidic2_readings.json (dict of kanji: [readings])
    with open(f'{DATA_DIR}/kanjidic2_readings.json', encoding='utf-8') as f:
        kanjidic2 = json.load(f)
    count = 0
    for kanji, readings in kanjidic2.items():
        for r in readings:
            merged[kanji].add(kata_to_hira(r))
            count += 1
    print(f'  kanjidic2: {len(kanjidic2)} 个汉字, {count} 个读音')

    # 3. xlsx_kanji_readings.json
    with open(f'{DATA_DIR}/xlsx_kanji_readings.json', encoding='utf-8') as f:
        xlsx_data = json.load(f)
    count = 0
    for kanji, readings in xlsx_data.items():
        for r in readings:
            merged[kanji].add(kata_to_hira(r))
            count += 1
    print(f'  xlsx: {len(xlsx_data)} 个汉字, {count} 个读音')

    # 4. furigana_kanji_readings.json
    with open(f'{DATA_DIR}/furigana_kanji_readings.json', encoding='utf-8') as f:
        furi_data = json.load(f)
    count = 0
    for kanji, readings in furi_data.items():
        for r in readings:
            merged[kanji].add(kata_to_hira(r))
            count += 1
    print(f'  furigana: {len(furi_data)} 个汉字, {count} 个读音')

    final_dict = {}
    for kanji, readings in merged.items():
        sorted_r = sorted(readings, key=lambda x: (-len(x), x))
        final_dict[kanji] = sorted_r

    print(f'  合并后: {len(final_dict)} 个汉字, {sum(len(v) for v in final_dict.values())} 个读音')

    with open(f'{DATA_DIR}/final_kanji_dict.json', 'w', encoding='utf-8') as f:
        json.dump(final_dict, f, ensure_ascii=False, indent=2)
    print(f'  已保存 final_kanji_dict.json')

    return final_dict


def supplement_with_engine(furi_splits, final_dict, jmdict_path):
    print('\n=== 用拆分引擎补充未覆盖的词 ===')
    sys.path.insert(0, r'D:\Libraries\Projects\AndroidStudioProjects\Learn\分词拆字工具')
    from kanji_kana_splitter import split_word

    with open(jmdict_path, encoding='utf-8') as f:
        jmdict = json.load(f)

    not_covered = []
    for entry in jmdict['words']:
        kanji_forms = entry.get('kanji', [])
        kana_forms = entry.get('kana', [])
        if not kanji_forms or not kana_forms:
            continue
        kanji_str = kanji_forms[0].get('text', '')
        kana_str = kata_to_hira(kana_forms[0].get('text', ''))
        has_cjk = any(0x4E00 <= ord(c) <= 0x9FFF or ord(c) == 0x3005 for c in kanji_str)
        if not has_cjk or kanji_str == kana_str:
            continue

        key = f'{kanji_str}-{kana_str}'
        if key not in furi_splits:
            not_covered.append((kanji_str, kana_str))

    engine_ok = 0
    for kanji_str, kana_str in not_covered:
        result = split_word(kanji_str, kana_str, final_dict)
        if result:
            kc, sc = result
            key = f'{kanji_str}-{kana_str}'
            furi_splits[key] = {
                'kanji_str': kanji_str,
                'kana_str': kana_str,
                'kanji_comps': kc,
                'kana_comps': sc,
                'source': 'engine'
            }
            engine_ok += 1

    print(f'  未覆盖: {len(not_covered)}')
    print(f'  引擎补充: {engine_ok}')
    print(f'  仍无法拆分: {len(not_covered) - engine_ok}')

    return furi_splits


def generate_coverage_report(splits, jmdict_path):
    print('\n=== 生成覆盖率报告 ===')
    with open(jmdict_path, encoding='utf-8') as f:
        jmdict = json.load(f)

    total = 0
    covered = 0
    uncovered = []
    by_source = defaultdict(int)

    for entry in jmdict['words']:
        kanji_forms = entry.get('kanji', [])
        kana_forms = entry.get('kana', [])
        if not kanji_forms or not kana_forms:
            continue
        kanji_str = kanji_forms[0].get('text', '')
        kana_str = kata_to_hira(kana_forms[0].get('text', ''))
        has_cjk = any(0x4E00 <= ord(c) <= 0x9FFF or ord(c) == 0x3005 for c in kanji_str)
        if not has_cjk or kanji_str == kana_str:
            continue

        total += 1
        key = f'{kanji_str}-{kana_str}'
        if key in splits:
            covered += 1
            by_source[splits[key]['source']] += 1
        else:
            uncovered.append({'kanji': kanji_str, 'kana': kana_str})

    report = {
        'total_words': total,
        'covered': covered,
        'uncovered': len(uncovered),
        'coverage_rate': round(covered / total * 100, 1),
        'by_source': dict(by_source),
        'uncovered_words': uncovered[:50]
    }

    print(f'  总词数: {total}')
    print(f'  已覆盖: {covered}')
    print(f'  未覆盖: {len(uncovered)}')
    print(f'  覆盖率: {report["coverage_rate"]}%')
    print(f'  按来源: {dict(by_source)}')

    with open(f'{DATA_DIR}/jmdict_common_coverage.json', 'w', encoding='utf-8') as f:
        json.dump(report, f, ensure_ascii=False, indent=2)
    print(f'  已保存 jmdict_common_coverage.json')

    return report


if __name__ == '__main__':
    furi_splits = build_furigana_splits()
    final_dict = build_final_kanji_dict()
    jmdict_path = f'{DATA_DIR}/jmdict-eng-common-3.6.2.json'

    furi_splits = supplement_with_engine(furi_splits, final_dict, jmdict_path)

    with open(f'{DATA_DIR}/furigana_per_char_splits.json', 'w', encoding='utf-8') as f:
        json.dump(furi_splits, f, ensure_ascii=False, indent=2)
    print(f'\n已更新 furigana_per_char_splits.json ({len(furi_splits)} 条)')

    report = generate_coverage_report(furi_splits, jmdict_path)
