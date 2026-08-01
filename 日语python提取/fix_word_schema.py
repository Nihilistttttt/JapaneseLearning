import sqlite3, os
DB_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB_PATH)
c = conn.cursor()
c.execute("PRAGMA foreign_keys=OFF")
c.execute("CREATE TABLE Word_new (wordId TEXT NOT NULL PRIMARY KEY, meaningIdList TEXT, sentenceIdList TEXT, synonymWordIdList TEXT, antonymWordIdList TEXT, collocationIdList TEXT, conjugationFormIdList TEXT NOT NULL DEFAULT '[]', etymologyIdList TEXT NOT NULL DEFAULT '[]', kanjiInfoIdList TEXT NOT NULL DEFAULT '[]', usageDistinctionIdList TEXT NOT NULL DEFAULT '[]', grammarPointIdList TEXT NOT NULL DEFAULT '[]', idiomIdList TEXT NOT NULL DEFAULT '[]', derivedWordIdList TEXT NOT NULL DEFAULT '[]', relatedWordIdList TEXT NOT NULL DEFAULT '[]')")
c.execute("INSERT INTO Word_new SELECT wordId, meaningIdList, sentenceIdList, synonymWordIdList, antonymWordIdList, collocationIdList, conjugationFormIdList, etymologyIdList, kanjiInfoIdList, usageDistinctionIdList, grammarPointIdList, idiomIdList, COALESCE(derivedWordIdList,'[]'), COALESCE(relatedWordIdList,'[]') FROM Word")
c.execute("DROP TABLE Word")
c.execute("ALTER TABLE Word_new RENAME TO Word")
conn.commit()
c.execute("PRAGMA table_info(Word)")
for r in c.fetchall():
    print(r)
conn.close()
print("Done!")