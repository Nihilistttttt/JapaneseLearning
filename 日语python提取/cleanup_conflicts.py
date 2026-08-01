import sqlite3
DB_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB_PATH)
c = conn.cursor()
prefixes = ['m1_','m2_','m3_','m4_','s1_','s2_','s3_','s4_','c1_','c2_','c3_','c4_',
            'sy1_','sy2_','sy3_','sy4_','a1_','a2_','a3_','a4_',
            'cf1_','cf2_','cf3_','cf4_','et1_','et2_','et3_','et4_',
            'ki1_','ki2_','ki3_','ki4_','ud1_','ud2_','ud3_','ud4_',
            'gp1_','gp2_','gp3_','gp4_','id1_','id2_','id3_','id4_',
            'd1_','d2_','d3_','d4_','r1_','r2_','r3_','r4_']
tables_ids = [
    ('WordMeaning','wordMeaningId'),('WordSentence','wordSentenceId'),('WordCollocation','wordCollocationId'),
    ('SynonymWord','synonymWordId'),('AntonymWord','antonymWordId'),('ConjugationForm','conjugationFormId'),
    ('Etymology','etymologyId'),('KanjiInfo','kanjiInfoId'),('UsageDistinction','usageDistinctionId'),
    ('GrammarPoint','grammarPointId'),('Idiom','idiomId'),('DerivedWord','derivedWordId'),('RelatedWord','relatedWordId')
]
total = 0
for table, id_col in tables_ids:
    for p in prefixes:
        c.execute(f"DELETE FROM {table} WHERE {id_col} LIKE ?", (p + '%',))
        total += c.rowcount
for wid in ['1','2','3','4']:
    c.execute("DELETE FROM Word WHERE wordId = ?", (wid,))
    total += c.rowcount
    c.execute("DELETE FROM BasicWord WHERE wordId = ?", (wid,))
    total += c.rowcount
conn.commit()
print(f"Deleted {total} conflicting records")
conn.close()