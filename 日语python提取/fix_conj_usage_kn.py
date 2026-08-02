"""
修复ConjugationForm和UsageDistinction的kanaComponents分组问题
"""
import sqlite3, json

DB_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB_PATH)
c = conn.cursor()

fixed = 0

# === ConjugationForm ===
# 所有7条都是wordId=3的静か活用形，模式：静=しず, 后续一一对应
# 通用修复：对于wordId=3且kc以'静'开头的记录，将kn前两个元素合并
c.execute("SELECT conjugationFormId, wordId, kanjiComponents, kanaComponents FROM ConjugationForm WHERE wordId=?", ('3',))
rows = c.fetchall()
print(f'ConjugationForm wordId=3: {len(rows)} records')
for cfid, wid, kc_json, kn_json in rows:
    kc = json.loads(kc_json)
    kn = json.loads(kn_json)
    if len(kc) != len(kn):
        # 静=しず, 后续一一对应
        new_kn = ['しず'] + kn[2:]  # 合并前两个kana元素
        if len(new_kn) == len(kc):
            new_kn_json = json.dumps(new_kn, ensure_ascii=False)
            c.execute("UPDATE ConjugationForm SET kanaComponents=? WHERE conjugationFormId=?", (new_kn_json, cfid))
            fixed += 1
            print(f'  Fixed cfId={cfid}: {kc} / {kn} -> {new_kn}')
        else:
            print(f'  CANNOT FIX cfId={cfid}: {kc}({len(kc)}) / {kn}({len(kn)}) -> new_kn={new_kn}({len(new_kn)})')

# === UsageDistinction ===
# wordId=3: 静か = しずか → kc=['静','か'] kn=['しず','か']
c.execute("SELECT usageDistinctionId, wordId, kanjiComponents, kanaComponents FROM UsageDistinction WHERE wordId=?", ('3',))
rows = c.fetchall()
for udid, wid, kc_json, kn_json in rows:
    kc = json.loads(kc_json)
    kn = json.loads(kn_json)
    if len(kc) != len(kn):
        new_kn = ['しず'] + kn[2:]
        if len(new_kn) == len(kc):
            new_kn_json = json.dumps(new_kn, ensure_ascii=False)
            c.execute("UPDATE UsageDistinction SET kanaComponents=? WHERE usageDistinctionId=?", (new_kn_json, udid))
            fixed += 1
            print(f'  Fixed UsageDistinction wordId=3: {kc} / {kn} -> {new_kn}')

# wordId=4: 友達 = ともだち → kc=['友','達'] kn=['とも','だち']
c.execute("SELECT usageDistinctionId, wordId, kanjiComponents, kanaComponents FROM UsageDistinction WHERE wordId=?", ('4',))
rows = c.fetchall()
for udid, wid, kc_json, kn_json in rows:
    kc = json.loads(kc_json)
    kn = json.loads(kn_json)
    if len(kc) != len(kn):
        new_kn = ['とも', 'だち']
        if len(new_kn) == len(kc):
            new_kn_json = json.dumps(new_kn, ensure_ascii=False)
            c.execute("UPDATE UsageDistinction SET kanaComponents=? WHERE usageDistinctionId=?", (new_kn_json, udid))
            fixed += 1
            print(f'  Fixed UsageDistinction wordId=4: {kc} / {kn} -> {new_kn}')

conn.commit()
print(f'\nTotal fixed: {fixed}')

# 验证
print('\n=== Verification ===')
for table in ['ConjugationForm', 'UsageDistinction']:
    c.execute(f"SELECT wordId, kanjiComponents, kanaComponents FROM {table}")
    mismatches = 0
    for wid, kc, kn in c.fetchall():
        kc_list = json.loads(kc) if kc else []
        kn_list = json.loads(kn) if kn else []
        if kc_list and kn_list and len(kc_list) != len(kn_list):
            mismatches += 1
            print(f'  STILL MISMATCHED: {table} {wid}: kc={kc_list}({len(kc_list)}) kn={kn_list}({len(kn_list)})')
    if mismatches == 0:
        print(f'{table}: All OK')

conn.close()