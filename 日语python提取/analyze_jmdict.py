import json
from collections import Counter

with open(r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理\jmdict-eng-common-3.6.2.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

words = data['words']
print(f'Total words: {len(words)}')

# 找有antonym和related的词
found = False
for w in words:
    for s in w['sense']:
        if s['antonym'] or s['related']:
            kanji_texts = [k['text'] for k in w['kanji']]
            kana_texts = [k['text'] for k in w['kana']]
            print(f'\nID={w["id"]}, kanji={kanji_texts}, kana={kana_texts}')
            print(f'  antonym={s["antonym"]}')
            print(f'  related={s["related"]}')
            print(f'  pos={s["partOfSpeech"]}')
            gloss_texts = [g['text'] for g in s['gloss']]
            print(f'  gloss={gloss_texts}')
            found = True
            break
    if found:
        break

# 统计有antonym/related的词数
ant_count = sum(1 for w in words for s in w['sense'] if s['antonym'])
rel_count = sum(1 for w in words for s in w['sense'] if s['related'])
print(f'\nSenses with antonym: {ant_count}')
print(f'Senses with related: {rel_count}')

# 统计词性分布
pos_counter = Counter()
for w in words:
    for s in w['sense']:
        for p in s['partOfSpeech']:
            pos_counter[p] += 1
print(f'\nTop 20 POS tags:')
for pos, cnt in pos_counter.most_common(20):
    print(f'  {pos}: {cnt}')

# 看一个有多个sense的词
for w in words[:200]:
    if len(w['sense']) >= 3:
        kanji_texts = [k['text'] for k in w['kanji']]
        kana_texts = [k['text'] for k in w['kana']]
        print(f'\nMulti-sense word: ID={w["id"]}, kanji={kanji_texts}, kana={kana_texts}')
        for i, s in enumerate(w['sense']):
            gloss_texts = [g['text'] for g in s['gloss']]
            print(f'  sense[{i}]: pos={s["partOfSpeech"]}, gloss={gloss_texts[:3]}')
        break