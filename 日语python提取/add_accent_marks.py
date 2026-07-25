"""
用合并重音数据(NHK+Kishimoto+Wadoku)补充重音标记
"""
import csv
import json

COMBINED_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理\jp-pitch-accent-db-master\assets\output\output.csv'
ROOM_IMPORT_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\room_import.json'

print("Loading combined pitch accent data...")
word_to_accent = {}

with open(COMBINED_PATH, 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader)  # skip header
    for row in reader:
        if len(row) < 5:
            continue
        kana = row[1]
        word = row[2]
        accent = row[4]
        
        if not accent:
            continue
        
        # Store by word (kanji form)
        if word not in word_to_accent:
            word_to_accent[word] = accent
        # Store by kana form
        if kana not in word_to_accent:
            word_to_accent[kana] = accent

print(f"  Total entries: {len(word_to_accent)}")

print("\nLoading room_import.json...")
with open(ROOM_IMPORT_PATH, 'r', encoding='utf-8') as f:
    data = json.load(f)

updated = 0
already = 0

for word in data['basicWords']:
    if word.get('accentMark', '') != '':
        already += 1
        continue
    
    kanji_comps = json.loads(word.get('kanjiComponents', '[]'))
    kana_comps = json.loads(word.get('kanaComponents', '[]'))
    
    surface = ''.join(''.join(comp) for comp in kanji_comps)
    kana_surface = ''.join(''.join(comp) for comp in kana_comps)
    
    if surface in word_to_accent:
        word['accentMark'] = word_to_accent[surface]
        updated += 1
    elif kana_surface in word_to_accent:
        word['accentMark'] = word_to_accent[kana_surface]
        updated += 1

total_with = already + updated
print(f"  Previously had accent: {already}")
print(f"  Newly matched: {updated}")
print(f"  Total with accent: {total_with}/{len(data['basicWords'])} ({100*total_with/len(data['basicWords']):.1f}%)")

print("Saving room_import.json...")
with open(ROOM_IMPORT_PATH, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False)

print("Done!")