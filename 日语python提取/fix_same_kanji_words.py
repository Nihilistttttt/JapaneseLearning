"""
修复KanjiInfo的sameKanjiWords格式：
从简单字符串["書籍","図書"]改成JSON对象[{"kj":["書","籍"],"kn":["しょ","せき"],"wid":"1234560"},...]
"""
import sqlite3, json

DB_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB_PATH)
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

c.execute("SELECT kanjiInfoId, sameKanjiWords FROM KanjiInfo WHERE sameKanjiWords IS NOT NULL AND sameKanjiWords != '[]'")
rows = c.fetchall()
print(f"Processing {len(rows)} KanjiInfo records")

fixed = 0
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
                new_list.append(json.dumps(obj, ensure_ascii=False))
            else:
                kj = list(kanji_str)
                obj = {"kj": kj, "kn": kj, "wid": "0"}
                new_list.append(json.dumps(obj, ensure_ascii=False))
    c.execute("UPDATE KanjiInfo SET sameKanjiWords = ? WHERE kanjiInfoId = ?",
              (json.dumps(new_list, ensure_ascii=False), kid))
    fixed += 1

conn.commit()
print(f"Fixed {fixed} KanjiInfo records")

c.execute("SELECT sameKanjiWords FROM KanjiInfo WHERE kanjiInfoId = 'ki1_1'")
row = c.fetchone()
print("ki1_1 sample:", row[0][:200] if row and row[0] else "NULL")
conn.close()