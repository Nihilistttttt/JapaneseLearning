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

c.execute("SELECT wordId, kanjiComponents FROM BasicWord")
all_words = [(wid, get_kanji_str(kc)) for wid, kc in c.fetchall()]
print(f"Loaded {len(all_words)} words")

def find_word_id(kanji_str):
    for wid, ks in all_words:
        if ks == kanji_str:
            return wid
    return "0"

words_to_find = {
    'SynonymWord': [
        ('sy1_1', '描く'), ('sy1_2', '記す'), ('sy1_3', '書き記す'),
        ('sy2_1', '高価'), ('sy2_2', '嵩高い'), ('sy2_3', '貴い'),
        ('sy3_1', '穏やか'), ('sy3_2', '静謐'), ('sy3_3', '閑静'),
        ('sy4_1', '仲間'), ('sy4_2', '友人'), ('sy4_3', '知人'),
    ],
    'AntonymWord': [
        ('a1_1', '読む'), ('a1_2', '消す'), ('a1_3', '破る'),
        ('a2_1', '低い'), ('a2_2', '安い'), ('a2_3', '小さい'),
        ('a3_1', 'うるさい'), ('a3_2', '賑やか'), ('a3_3', '騒がしい'),
        ('a4_1', '敵'), ('a4_2', '仇'), ('a4_3', 'ライバル'),
    ],
    'DerivedWord': [
        ('d1_1', '書き方'), ('d1_2', '書き物'), ('d1_3', '書道'),
        ('d2_1', '高さ'), ('d2_2', '高み'), ('d2_3', '高値'),
        ('d3_1', '静けさ'), ('d3_2', '静かさ'), ('d3_3', '静寂'),
        ('d4_1', '友情'), ('d4_2', '友好'), ('d4_3', '友達付き合い'),
    ],
    'RelatedWord': [
        ('r1_1', '読む'), ('r1_2', '覚える'), ('r1_3', '記録'),
        ('r2_1', '山'), ('r2_2', '塔'), ('r2_3', '屋上'),
        ('r3_1', '静止'), ('r3_2', '沈黙'), ('r3_3', '平穏'),
        ('r4_1', '家族'), ('r4_2', '同僚'), ('r4_3', '知人'),
    ],
}

id_col = {
    'SynonymWord': 'synonymWordId', 'AntonymWord': 'antonymWordId',
    'DerivedWord': 'derivedWordId', 'RelatedWord': 'relatedWordId',
}

found_count = 0
for table, items in words_to_find.items():
    for row_id, kanji_str in items:
        actual_id = find_word_id(kanji_str)
        if actual_id != "0":
            found_count += 1
            c.execute(f"UPDATE {table} SET correspondingWordId = ? WHERE {id_col[table]} = ?",
                      (actual_id, row_id))
            print(f"  {table} {row_id} ({kanji_str}) -> {actual_id}")
        else:
            print(f"  {table} {row_id} ({kanji_str}) -> NOT FOUND")

conn.commit()
print(f"\nFound {found_count} words, updated correspondingWordId")
conn.close()
