import tarfile
import json
import os
from collections import defaultdict

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理'
OUT = os.path.join(BASE, 'tatoeba_jpn_cmn_pairs.json')

print("Step 1: Loading Japanese and Chinese sentences...")
jpn_sentences = {}
cmn_sentences = {}

with tarfile.open(os.path.join(BASE, 'sentences.tar_2.bz2'), 'r:bz2') as f:
    for member in f.getmembers():
        content = f.extractfile(member)
        if not content:
            continue
        count = 0
        for line in content:
            line = line.decode('utf-8', errors='replace').strip()
            if not line:
                continue
            parts = line.split('\t')
            if len(parts) < 3:
                continue
            sent_id, lang, text = parts[0], parts[1], parts[2]
            if lang == 'jpn':
                jpn_sentences[sent_id] = text
            elif lang == 'cmn':
                cmn_sentences[sent_id] = text
            count += 1
            if count % 500000 == 0:
                print(f"  sentences: {count}, jpn={len(jpn_sentences)}, cmn={len(cmn_sentences)}")
        break

print(f"  Loaded: {len(jpn_sentences)} Japanese, {len(cmn_sentences)} Chinese")

print("Step 2: Loading links to find Japanese-Chinese pairs...")
jpn_to_cmn = defaultdict(list)

with tarfile.open(os.path.join(BASE, 'links.tar.bz2'), 'r:bz2') as f:
    for member in f.getmembers():
        content = f.extractfile(member)
        if not content:
            continue
        count = 0
        for line in content:
            line = line.decode('utf-8', errors='replace').strip()
            if not line:
                continue
            parts = line.split('\t')
            if len(parts) < 2:
                continue
            sent_id, trans_id = parts[0], parts[1]
            if sent_id in jpn_sentences and trans_id in cmn_sentences:
                jpn_to_cmn[sent_id].append(trans_id)
            elif trans_id in jpn_sentences and sent_id in cmn_sentences:
                jpn_to_cmn[trans_id].append(sent_id)
            count += 1
            if count % 1000000 == 0:
                print(f"  links: {count}, pairs found: {len(jpn_to_cmn)}")
        break

print(f"  Found {len(jpn_to_cmn)} Japanese sentences with Chinese translations")

print("Step 3: Loading jpn_indices to link sentences to JMDict entries...")
sent_to_jmdict = {}

with tarfile.open(os.path.join(BASE, 'jpn_indices.tar.bz2'), 'r:bz2') as f:
    for member in f.getmembers():
        content = f.extractfile(member)
        if not content:
            continue
        count = 0
        for line in content:
            line = line.decode('utf-8', errors='replace').strip()
            if not line:
                continue
            parts = line.split('\t')
            if len(parts) >= 2:
                sent_id, jmdict_id = parts[0], parts[1]
                if sent_id in jpn_to_cmn:
                    sent_to_jmdict[sent_id] = jmdict_id
            count += 1
            if count % 100000 == 0:
                print(f"  indices: {count}, matched: {len(sent_to_jmdict)}")
        break

print(f"  {len(sent_to_jmdict)} sentences linked to JMDict entries")

print("Step 4: Building output...")
result = {}
for jpn_id, cmn_ids in jpn_to_cmn.items():
    jmdict_id = sent_to_jmdict.get(jpn_id, "")
    entry = {
        "jmdict_id": jmdict_id,
        "jpn_id": jpn_id,
        "jpn_text": jpn_sentences[jpn_id],
        "cmn_translations": [cmn_sentences[cid] for cid in cmn_ids]
    }
    result[jpn_id] = entry

with open(OUT, 'w', encoding='utf-8') as f:
    json.dump(result, f, ensure_ascii=False, indent=2)

print(f"Done! {len(result)} Japanese-Chinese pairs saved to {OUT}")
print(f"File size: {os.path.getsize(OUT)/1024/1024:.1f} MB")

linked = sum(1 for v in result.values() if v["jmdict_id"])
print(f"With JMDict link: {linked}, Without: {len(result) - linked}")