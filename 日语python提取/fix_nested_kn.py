"""
修复嵌套数组(WordCollocation/Idiom/Etymology/WordSentence)的内层kc/kn配对
"""
import sqlite3, json

DB_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB_PATH)
c = conn.cursor()

# 定义内层修复映射: tuple(kc) → correct_kn
inner_fixes = {
    ('友', '達'): ['とも', 'だち'],
    ('遊', 'ぶ'): ['あそ', 'ぶ'],
    ('作', 'る'): ['つく', 'る'],
    ('家',): ['いえ'],
    ('話', 'す'): ['はな', 'す'],
    ('静', 'か'): ['しず', 'か'],
    ('友',): ['とも'],
    ('達',): ['だち'],
    ('作', 'っ', 'た'): ['つく', 'っ', 'た'],
    ('映', '画'): ['えい', 'が'],
}

fixed_total = 0

nested_tables = {
    'WordCollocation': 'wordCollocationId',
    'Idiom': 'idiomId',
    'Etymology': 'etymologyId',
    'WordSentence': 'wordSentenceId',
}

for table, id_col in nested_tables.items():
    c.execute(f"SELECT {id_col}, kanjiComponents, kanaComponents FROM {table}")
    rows = c.fetchall()
    table_fixed = 0
    
    for rid, kc_json, kn_json in rows:
        kc = json.loads(kc_json) if kc_json else []
        kn = json.loads(kn_json) if kn_json else []
        if not kc or not kn:
            continue
        
        changed = False
        # 修复内层
        if len(kc) == len(kn):
            for i in range(len(kc)):
                if isinstance(kc[i], list) and isinstance(kn[i], list):
                    if len(kc[i]) != len(kn[i]):
                        key = tuple(kc[i])
                        if key in inner_fixes:
                            old_kn_i = kn[i]
                            kn[i] = inner_fixes[key]
                            changed = True
                            print(f'  {table} {rid} inner[{i}]: {kc[i]} / {old_kn_i} -> {inner_fixes[key]}')
        
        if changed:
            new_kn_json = json.dumps(kn, ensure_ascii=False)
            c.execute(f"UPDATE {table} SET kanaComponents=? WHERE {id_col}=?", (new_kn_json, rid))
            table_fixed += 1
    
    if table_fixed:
        print(f'{table}: fixed {table_fixed} records')
        fixed_total += table_fixed

conn.commit()
print(f'\nTotal fixed: {fixed_total}')

# 验证
print('\n=== Verification ===')
for table, id_col in nested_tables.items():
    c.execute(f"SELECT {id_col}, kanjiComponents, kanaComponents FROM {table}")
    mismatches = 0
    for rid, kc_json, kn_json in c.fetchall():
        kc = json.loads(kc_json) if kc_json else []
        kn = json.loads(kn_json) if kn_json else []
        if not kc or not kn:
            continue
        if len(kc) != len(kn):
            mismatches += 1
            continue
        for i in range(len(kc)):
            if isinstance(kc[i], list) and isinstance(kn[i], list):
                if len(kc[i]) != len(kn[i]):
                    mismatches += 1
                    break
    if mismatches:
        print(f'{table}: STILL {mismatches} mismatches!')
    else:
        print(f'{table}: All OK')

conn.close()