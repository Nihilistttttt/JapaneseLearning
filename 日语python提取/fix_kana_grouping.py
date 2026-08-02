"""
修复四个测试词(0000001-0000004)的kanaComponents分组：
当len(kc) != len(kn)时，将kn重新分组使每组对应一个kc元素。
"""
import sqlite3, json

DB_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB_PATH)
c = conn.cursor()

ids = ['0000001', '0000002', '0000003', '0000004']

# 正确的kana分组: (table, wordId, tuple(kc), correct_kn)
fixes = {
    # SynonymWord
    ('SynonymWord', '0000001', ('書', 'き', '記', 'す')): ['か', 'き', 'しる', 'す'],
    ('SynonymWord', '0000002', ('嵩', '高', 'い')): ['かさ', 'だか', 'い'],
    ('SynonymWord', '0000003', ('静', '謐')): ['せい', 'ひゃく'],
    ('SynonymWord', '0000003', ('閑', '静')): ['かん', 'せい'],
    ('SynonymWord', '0000004', ('友', '人')): ['ゆう', 'じん'],
    ('SynonymWord', '0000004', ('知', '人')): ['ち', 'じん'],

    # AntonymWord
    ('AntonymWord', '0000001', ('破', 'る')): ['やぶ', 'る'],
    ('AntonymWord', '0000002', ('安', 'い')): ['やす', 'い'],
    ('AntonymWord', '0000002', ('小', 'さ', 'い')): ['ちい', 'さ', 'い'],
    ('AntonymWord', '0000003', ('賑', 'や', 'か')): ['にぎ', 'や', 'か'],
    ('AntonymWord', '0000003', ('騒', 'が', 'し', 'い')): ['さわ', 'が', 'し', 'い'],
    ('AntonymWord', '0000004', ('仇')): ['てき'],
    ('AntonymWord', '0000004', ('敵')): ['てき'],

    # DerivedWord
    ('DerivedWord', '0000001', ('書', 'き', '方')): ['か', 'き', 'かた'],
    ('DerivedWord', '0000001', ('書', 'き', '物')): ['か', 'き', 'もの'],
    ('DerivedWord', '0000002', ('高', 'さ')): ['たか', 'さ'],
    ('DerivedWord', '0000002', ('高', 'み')): ['たか', 'み'],
    ('DerivedWord', '0000002', ('高', '値')): ['たか', 'ね'],
    ('DerivedWord', '0000003', ('静', 'け', 'さ')): ['しず', 'け', 'さ'],
    ('DerivedWord', '0000003', ('静', 'か', 'さ')): ['しず', 'か', 'さ'],
    ('DerivedWord', '0000003', ('静', '寂')): ['せい', 'じゃく'],
    ('DerivedWord', '0000004', ('友', '情')): ['ゆう', 'じょう'],
    ('DerivedWord', '0000004', ('友', '好')): ['ゆう', 'こう'],
    ('DerivedWord', '0000004', ('友', '達', '付', 'き', '合', 'い')): ['とも', 'だち', 'づ', 'き', 'あ', 'い'],

    # RelatedWord
    ('RelatedWord', '0000001', ('覚', 'え', 'る')): ['おぼ', 'え', 'る'],
    ('RelatedWord', '0000001', ('記', '録')): ['き', 'ろく'],
    ('RelatedWord', '0000002', ('山')): ['やま'],
    ('RelatedWord', '0000002', ('屋', '上')): ['おく', 'じょう'],
    ('RelatedWord', '0000003', ('静', '止')): ['せい', 'し'],
    ('RelatedWord', '0000003', ('沈', '黙')): ['ちん', 'もく'],
    ('RelatedWord', '0000003', ('平', '穏')): ['へい', 'おん'],
    ('RelatedWord', '0000004', ('家', '族')): ['か', 'ぞく'],
    ('RelatedWord', '0000004', ('同', '僚')): ['どう', 'りょう'],
    ('RelatedWord', '0000004', ('知', '人')): ['ち', 'じん'],

    # BasicWord
    ('BasicWord', '0000004', ('友', '達')): ['とも', 'だち'],
}

fixed = 0
for (table, wid, kc_tuple), correct_kn in fixes.items():
    kc_json = json.dumps(list(kc_tuple), ensure_ascii=False)
    correct_kn_json = json.dumps(correct_kn, ensure_ascii=False)
    c.execute(f'SELECT rowid, kanaComponents FROM {table} WHERE wordId=? AND kanjiComponents=?', (wid, kc_json))
    row = c.fetchone()
    if row:
        rowid, old_kn = row
        if old_kn != correct_kn_json:
            c.execute(f'UPDATE {table} SET kanaComponents=? WHERE rowid=?', (correct_kn_json, rowid))
            fixed += 1
            print(f'Fixed {table} {wid} {list(kc_tuple)}: {json.loads(old_kn)} -> {correct_kn}')
    else:
        print(f'NOT FOUND: {table} {wid} {list(kc_tuple)}')

conn.commit()
print(f'\nTotal fixed: {fixed}')

# 验证
print('\n=== Verification ===')
for table in ['BasicWord', 'SynonymWord', 'AntonymWord', 'DerivedWord', 'RelatedWord']:
    placeholders = ','.join('?' * len(ids))
    c.execute(f'SELECT wordId, kanjiComponents, kanaComponents FROM {table} WHERE wordId IN ({placeholders})', ids)
    for wid, kc, kn in c.fetchall():
        kc_list = json.loads(kc)
        kn_list = json.loads(kn)
        if len(kc_list) != len(kn_list):
            print(f'  STILL MISMATCHED: {table} {wid}: kc={kc_list}({len(kc_list)}) kn={kn_list}({len(kn_list)})')

conn.close()