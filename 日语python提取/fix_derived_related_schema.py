import sqlite3
DB_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB_PATH)
c = conn.cursor()
c.execute("PRAGMA foreign_keys=OFF")
for old, new, id_col in [('DerivedWord','DerivedWord_new','derivedWordId'),('RelatedWord','RelatedWord_new','relatedWordId')]:
    c.execute(f"CREATE TABLE {new} ({id_col} TEXT NOT NULL PRIMARY KEY, wordId TEXT, correspondingWordId TEXT, kanjiComponents TEXT, kanaComponents TEXT)")
    c.execute(f"INSERT INTO {new} SELECT * FROM {old}")
    c.execute(f"DROP TABLE {old}")
    c.execute(f"ALTER TABLE {new} RENAME TO {old}")
conn.commit()
for t, id_col in [('DerivedWord','derivedWordId'),('RelatedWord','relatedWordId')]:
    c.execute(f"PRAGMA table_info({t})")
    print(t, [(r[1],r[2],r[3]) for r in c.fetchall() if r[1]==id_col])
conn.close()
print("Done!")