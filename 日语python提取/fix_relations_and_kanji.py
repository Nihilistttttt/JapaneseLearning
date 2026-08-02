"""
综合修复测试词数据：
1. SynonymWord/AntonymWord的correspondingWordId从"0"修复为真实wordId
2. 插入DerivedWord/RelatedWord数据（fix_test_words.py遗漏）
3. KanjiInfo sameKanjiWords从简单字符串修复为JSON对象格式(kj/kn/wid)
"""

import sqlite3, json

DB = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB)
c = conn.cursor()

def j(obj):
    return json.dumps(obj, ensure_ascii=False)

# Build word_map: kanji_str -> (kj_list, kn_list, wordId)
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

c.execute("SELECT wordId, kanjiComponents, kanaComponents FROM BasicWord")
word_map = {}
for wid, kc_json, kn_json in c.fetchall():
    ks = get_kanji_str(kc_json)
    if ks and ks not in word_map:
        try:
            kc = json.loads(kc_json)
            kn = json.loads(kn_json)
            if isinstance(kc[0], list):
                kj_flat = kc[0]
                kn_flat = kn[0] if kn and isinstance(kn[0], list) else kn
            else:
                kj_flat = kc
                kn_flat = kn
            word_map[ks] = (kj_flat, kn_flat, wid)
        except:
            pass

print(f"Built word_map with {len(word_map)} entries")

# ============================================================
# 1. Fix SynonymWord/AntonymWord correspondingWordId
# ============================================================
print("\n=== Fixing correspondingWordId ===")

# Map of test word IDs to their synonym/antonym entries
# Format: (table, row_id, kanji_str)
corresponding_fixes = [
    ('SynonymWord', 'sy1_1', '描く'),
    ('SynonymWord', 'sy2_1', '高価'),
    ('SynonymWord', 'sy4_1', '仲間'),
    ('AntonymWord', 'a1_1', '読む'),
    ('AntonymWord', 'a2_1', '低い'),
]

id_col = {
    'SynonymWord': 'synonymWordId',
    'AntonymWord': 'antonymWordId',
}

fixed = 0
for table, row_id, kanji_str in corresponding_fixes:
    if kanji_str in word_map:
        _, _, real_wid = word_map[kanji_str]
        c.execute(f"UPDATE {table} SET correspondingWordId = ? WHERE {id_col[table]} = ?",
                  (real_wid, row_id))
        print(f"  {table} {row_id} ({kanji_str}) -> {real_wid}")
        fixed += 1
    else:
        print(f"  {table} {row_id} ({kanji_str}) -> NOT FOUND in word_map")

print(f"Fixed {fixed} correspondingWordIds")

# ============================================================
# 2. Insert DerivedWord/RelatedWord for 0000001-4
# ============================================================
print("\n=== Inserting DerivedWord/RelatedWord ===")

# First, delete any existing test DerivedWord/RelatedWord (should be none)
for wid in ['0000001','0000002','0000003','0000004']:
    c.execute("DELETE FROM DerivedWord WHERE wordId = ?", (wid,))
    c.execute("DELETE FROM RelatedWord WHERE wordId = ?", (wid,))

def find_wid(kanji_str):
    if kanji_str in word_map:
        return word_map[kanji_str][2]
    return "0"

# 書く (0000001)
wid = "0000001"
derived_1 = [
    ("d1_1", wid, find_wid("書き方"), ["書","き","方"], ["か","き","か","た"]),
    ("d1_2", wid, find_wid("書き物"), ["書","き","物"], ["か","き","も","の"]),
    ("d1_3", wid, find_wid("書道"), ["書","道"], ["しょ","どう"]),
]
related_1 = [
    ("r1_1", wid, find_wid("読む"), ["読","む"], ["よ","む"]),
    ("r1_2", wid, find_wid("覚える"), ["覚","え","る"], ["お","ぼ","え","る"]),
    ("r1_3", wid, find_wid("記録"), ["記","録"], ["き","ろ","く"]),
]

# 高い (0000002)
wid = "0000002"
derived_2 = [
    ("d2_1", wid, find_wid("高さ"), ["高","さ"], ["たか","さ"]),
    ("d2_2", wid, find_wid("高み"), ["高","み"], ["たか","み"]),
    ("d2_3", wid, find_wid("高値"), ["高","値"], ["たか","ね"]),
]
related_2 = [
    ("r2_1", wid, find_wid("山"), ["山"], ["やま"]),
    ("r2_2", wid, find_wid("塔"), ["塔"], ["とう"]),
    ("r2_3", wid, find_wid("屋上"), ["屋","上"], ["おく","じょう"]),
]

# 静か (0000003)
wid = "0000003"
derived_3 = [
    ("d3_1", wid, find_wid("静けさ"), ["静","け","さ"], ["しず","け","さ"]),
    ("d3_2", wid, find_wid("静かさ"), ["静","か","さ"], ["しず","か","さ"]),
    ("d3_3", wid, find_wid("静寂"), ["静","寂"], ["せい","じゃく"]),
]
related_3 = [
    ("r3_1", wid, find_wid("静止"), ["静","止"], ["せい","し"]),
    ("r3_2", wid, find_wid("沈黙"), ["沈","黙"], ["ちん","もく"]),
    ("r3_3", wid, find_wid("平穏"), ["平","穏"], ["へい","おん"]),
]

# 友達 (0000004)
wid = "0000004"
derived_4 = [
    ("d4_1", wid, find_wid("友情"), ["友","情"], ["ゆう","じょう"]),
    ("d4_2", wid, find_wid("友好"), ["友","好"], ["ゆう","こう"]),
    ("d4_3", wid, find_wid("友達付き合い"), ["友","達","付","き","合","い"], ["とも","だち","づ","き","あ","い"]),
]
related_4 = [
    ("r4_1", wid, find_wid("家族"), ["家","族"], ["か","ぞく"]),
    ("r4_2", wid, find_wid("同僚"), ["同","僚"], ["どう","りょう"]),
    ("r4_3", wid, find_wid("知人"), ["知","人"], ["ち","じん"]),
]

all_derived = derived_1 + derived_2 + derived_3 + derived_4
all_related = related_1 + related_2 + related_3 + related_4

for did, wid, cwid, kc, kn in all_derived:
    c.execute("INSERT INTO DerivedWord VALUES (?,?,?,?,?)", (did, wid, cwid, j(kc), j(kn)))
for rid, wid, cwid, kc, kn in all_related:
    c.execute("INSERT INTO RelatedWord VALUES (?,?,?,?,?)", (rid, wid, cwid, j(kc), j(kn)))

# Update Word table derivedWordIdList/relatedWordIdList
c.execute("UPDATE Word SET derivedWordIdList=?, relatedWordIdList=? WHERE wordId=?",
          (j(["d1_1","d1_2","d1_3"]), j(["r1_1","r1_2","r1_3"]), "0000001"))
c.execute("UPDATE Word SET derivedWordIdList=?, relatedWordIdList=? WHERE wordId=?",
          (j(["d2_1","d2_2","d2_3"]), j(["r2_1","r2_2","r2_3"]), "0000002"))
c.execute("UPDATE Word SET derivedWordIdList=?, relatedWordIdList=? WHERE wordId=?",
          (j(["d3_1","d3_2","d3_3"]), j(["r3_1","r3_2","r3_3"]), "0000003"))
c.execute("UPDATE Word SET derivedWordIdList=?, relatedWordIdList=? WHERE wordId=?",
          (j(["d4_1","d4_2","d4_3"]), j(["r4_1","r4_2","r4_3"]), "0000004"))

print(f"Inserted {len(all_derived)} DerivedWord + {len(all_related)} RelatedWord")

# ============================================================
# 3. Fix KanjiInfo sameKanjiWords format
# ============================================================
print("\n=== Fixing KanjiInfo sameKanjiWords ===")

c.execute("SELECT kanjiInfoId, sameKanjiWords FROM KanjiInfo WHERE wordId IN ('0000001','0000002','0000003','0000004')")
rows = c.fetchall()

fixed_ki = 0
for kid, skw_json in rows:
    try:
        skw = json.loads(skw_json)
    except:
        continue
    if not skw:
        continue
    needs_fix = False
    for item in skw:
        if isinstance(item, str):
            needs_fix = True
            break
    if not needs_fix:
        continue
    new_list = []
    for item in skw:
        if isinstance(item, dict):
            new_list.append(json.dumps(item, ensure_ascii=False))
        else:
            kanji_str = item
            if kanji_str in word_map:
                kj, kn, wid = word_map[kanji_str]
                obj = {"kj": kj, "kn": kn, "wid": wid}
            else:
                kj = list(kanji_str)
                obj = {"kj": kj, "kn": kj, "wid": "0"}
            new_list.append(json.dumps(obj, ensure_ascii=False))
    c.execute("UPDATE KanjiInfo SET sameKanjiWords = ? WHERE kanjiInfoId = ?",
              (json.dumps(new_list, ensure_ascii=False), kid))
    fixed_ki += 1
    print(f"  Fixed {kid}: {new_list[:2]}...")

print(f"Fixed {fixed_ki} KanjiInfo records")

# ============================================================
# Commit & Verify
# ============================================================
conn.commit()

print("\n=== Verification ===")
for wid in ['0000001','0000002','0000003','0000004']:
    c.execute("SELECT count(*) FROM SynonymWord WHERE wordId=?", (wid,))
    syn = c.fetchone()[0]
    c.execute("SELECT count(*) FROM AntonymWord WHERE wordId=?", (wid,))
    ant = c.fetchone()[0]
    c.execute("SELECT count(*) FROM DerivedWord WHERE wordId=?", (wid,))
    der = c.fetchone()[0]
    c.execute("SELECT count(*) FROM RelatedWord WHERE wordId=?", (wid,))
    rel = c.fetchone()[0]
    print(f"  {wid}: syn={syn} ant={ant} derived={der} related={rel}")

# Verify correspondingWordId
print("\n=== SynonymWord/AntonymWord correspondingWordId ===")
c.execute("SELECT synonymWordId,wordId,correspondingWordId FROM SynonymWord WHERE wordId IN ('0000001','0000002','0000003','0000004')")
for r in c.fetchall():
    print(f"  Syn {r[0]}: wid={r[1]} cwid={r[2]}")
c.execute("SELECT antonymWordId,wordId,correspondingWordId FROM AntonymWord WHERE wordId IN ('0000001','0000002','0000003','0000004')")
for r in c.fetchall():
    print(f"  Ant {r[0]}: wid={r[1]} cwid={r[2]}")

# Verify sameKanjiWords format
print("\n=== KanjiInfo sameKanjiWords format ===")
c.execute("SELECT kanjiInfoId,kanji,sameKanjiWords FROM KanjiInfo WHERE wordId IN ('0000001','0000002','0000003','0000004')")
for r in c.fetchall():
    skw = json.loads(r[2])
    first = skw[0] if skw else "empty"
    is_obj = isinstance(first, str) and first.startswith('{') if skw else False
    print(f"  {r[0]} ({r[1]}): {len(skw)} words, first_is_json_obj={is_obj}")

conn.close()
print("\nDone!")