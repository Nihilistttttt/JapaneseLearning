import sqlite3, json
conn = sqlite3.connect(r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db')
c = conn.cursor()

# Fix BasicWord 0000004: 友達 = ともだち, 友=とも, 達=だち
c.execute("UPDATE BasicWord SET kanaComponents=? WHERE wordId=?", (json.dumps(['とも', 'だち'], ensure_ascii=False), '0000004'))
print(f'BasicWord 0000004 updated: {c.rowcount} row')

# Fix SynonymWord 0000003: 穏やか = おだやか, 穏=おだ, や=や, か=か
kc_json = json.dumps(['穏', 'や', 'か'], ensure_ascii=False)
c.execute("UPDATE SynonymWord SET kanaComponents=? WHERE wordId=? AND kanjiComponents=?", (json.dumps(['おだ', 'や', 'か'], ensure_ascii=False), '0000003', kc_json))
print(f'SynonymWord 0000003 穏やか updated: {c.rowcount} row')

conn.commit()

# Verify
print('\n=== Verification ===')
for table in ['BasicWord', 'SynonymWord', 'AntonymWord', 'DerivedWord', 'RelatedWord']:
    ids = ['0000001', '0000002', '0000003', '0000004']
    placeholders = ','.join('?' * len(ids))
    c.execute(f"SELECT wordId, kanjiComponents, kanaComponents FROM {table} WHERE wordId IN ({placeholders})", ids)
    for wid, kc, kn in c.fetchall():
        kc_list = json.loads(kc)
        kn_list = json.loads(kn)
        if len(kc_list) != len(kn_list):
            print(f'  MISMATCH: {table} {wid}: kc={kc_list}({len(kc_list)}) kn={kn_list}({len(kn_list)})')
        else:
            print(f'  OK: {table} {wid}: {kc_list} / {kn_list}')

conn.close()