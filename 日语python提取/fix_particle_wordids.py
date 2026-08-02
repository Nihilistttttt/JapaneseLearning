"""
修复测试词例句/词组中wordIdList的"0"占位符，替换为真实wordId。
根据每个词的kanjiComponents拼接后查BasicWord表获取wordId。
"""

import sqlite3, json

DB = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB)
c = conn.cursor()

def get_kanji_str(kc_json):
    try:
        kc = json.loads(kc_json)
        if not kc: return ""
        if isinstance(kc[0], list):
            return "".join("".join(comp) for comp in kc)
        else:
            return "".join(kc)
    except:
        return ""

# Build word_map: kanji_str -> wordId
c.execute("SELECT wordId, kanjiComponents FROM BasicWord")
word_map = {}
for wid, kc_json in c.fetchall():
    ks = get_kanji_str(kc_json)
    if ks and ks not in word_map:
        word_map[ks] = wid

print(f"Built word_map with {len(word_map)} entries")

# Fix WordSentence
print("\n=== Fixing WordSentence ===")
c.execute("SELECT wordSentenceId, kanjiComponents, kanaComponents, wordIdList FROM WordSentence WHERE wordId IN ('0000001','0000002','0000003','0000004')")
rows = c.fetchall()
fixed_sentences = 0
for sid, kc_json, kn_json, wil_json in rows:
    kc = json.loads(kc_json)
    kn = json.loads(kn_json)
    wil = json.loads(wil_json)
    changed = False
    for i in range(len(wil)):
        if wil[i] == "0" and i < len(kc):
            # Get kanji string for this component
            comp = kc[i]
            if isinstance(comp, list):
                ks = "".join(comp)
            else:
                ks = comp
            if ks in word_map:
                wil[i] = word_map[ks]
                changed = True
    if changed:
        c.execute("UPDATE WordSentence SET wordIdList = ? WHERE wordSentenceId = ?",
                  (json.dumps(wil), sid))
        fixed_sentences += 1
        # Verify
        remaining_zeros = sum(1 for w in wil if w == "0")
        print(f"  {sid}: fixed, remaining zeros: {remaining_zeros}")

print(f"Fixed {fixed_sentences} sentences")

# Fix WordCollocation
print("\n=== Fixing WordCollocation ===")
c.execute("SELECT wordCollocationId, kanjiComponents, kanaComponents, wordIdList FROM WordCollocation WHERE wordId IN ('0000001','0000002','0000003','0000004')")
rows = c.fetchall()
fixed_collocations = 0
for cid, kc_json, kn_json, wil_json in rows:
    kc = json.loads(kc_json)
    kn = json.loads(kn_json)
    wil = json.loads(wil_json)
    changed = False
    for i in range(len(wil)):
        if wil[i] == "0" and i < len(kc):
            comp = kc[i]
            if isinstance(comp, list):
                ks = "".join(comp)
            else:
                ks = comp
            if ks in word_map:
                wil[i] = word_map[ks]
                changed = True
    if changed:
        c.execute("UPDATE WordCollocation SET wordIdList = ? WHERE wordCollocationId = ?",
                  (json.dumps(wil), cid))
        fixed_collocations += 1
        remaining_zeros = sum(1 for w in wil if w == "0")
        print(f"  {cid}: fixed, remaining zeros: {remaining_zeros}")

print(f"Fixed {fixed_collocations} collocations")

conn.commit()

# Final verification
print("\n=== Verification ===")
c.execute("SELECT wordSentenceId, wordIdList FROM WordSentence WHERE wordId IN ('0000001','0000002','0000003','0000004')")
total_zeros = 0
for r in c.fetchall():
    wil = json.loads(r[1])
    zeros = sum(1 for w in wil if w == "0")
    total_zeros += zeros
print(f"Remaining '0' in sentences: {total_zeros}")

c.execute("SELECT wordCollocationId, wordIdList FROM WordCollocation WHERE wordId IN ('0000001','0000002','0000003','0000004')")
total_zeros = 0
for r in c.fetchall():
    wil = json.loads(r[1])
    zeros = sum(1 for w in wil if w == "0")
    total_zeros += zeros
print(f"Remaining '0' in collocations: {total_zeros}")

# Show a sample
print("\n=== Sample s1_1 after fix ===")
c.execute("SELECT kanjiComponents, kanaComponents, wordIdList FROM WordSentence WHERE wordSentenceId='s1_1'")
r = c.fetchone()
kc = json.loads(r[0]); kn = json.loads(r[1]); wil = json.loads(r[2])
for i in range(len(kc)):
    k_str = "".join(kc[i]) if isinstance(kc[i], list) else kc[i]
    n_str = "".join(kn[i]) if isinstance(kn[i], list) else kn[i]
    print(f"  [{i}] {k_str}({n_str}) -> wid={wil[i]}")

print("\n=== Sample s1_2 after fix ===")
c.execute("SELECT kanjiComponents, kanaComponents, wordIdList FROM WordSentence WHERE wordSentenceId='s1_2'")
r = c.fetchone()
kc = json.loads(r[0]); kn = json.loads(r[1]); wil = json.loads(r[2])
for i in range(len(kc)):
    k_str = "".join(kc[i]) if isinstance(kc[i], list) else kc[i]
    n_str = "".join(kn[i]) if isinstance(kn[i], list) else kn[i]
    print(f"  [{i}] {k_str}({n_str}) -> wid={wil[i]}")

conn.close()
print("\nDone!")