"""
为4个测试单词补充近义词/反义词至3个，并添加派生词和关联词数据。
同时创建DerivedWord和RelatedWord表，给Word表添加derivedWordIdList和relatedWordIdList列。

测试单词:
1. 書く (かく) - wordId "1"
2. 高い (たかい) - wordId "2"
3. 静か (しずか) - wordId "3"
4. 友達 (ともだち) - wordId "4"
"""

import json
import sqlite3
import os

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn'
DB_PATH = os.path.join(BASE, 'app', 'src', 'main', 'assets', 'databases', 'word_database.db')


def j(obj):
    return json.dumps(obj, ensure_ascii=False)


def add_columns_and_tables(conn):
    c = conn.cursor()
    c.execute("CREATE TABLE IF NOT EXISTS DerivedWord (derivedWordId TEXT PRIMARY KEY, wordId TEXT, correspondingWordId TEXT, kanjiComponents TEXT, kanaComponents TEXT)")
    c.execute("CREATE TABLE IF NOT EXISTS RelatedWord (relatedWordId TEXT PRIMARY KEY, wordId TEXT, correspondingWordId TEXT, kanjiComponents TEXT, kanaComponents TEXT)")
    try:
        c.execute("ALTER TABLE Word ADD COLUMN derivedWordIdList TEXT DEFAULT '[]'")
    except sqlite3.OperationalError:
        pass
    try:
        c.execute("ALTER TABLE Word ADD COLUMN relatedWordIdList TEXT DEFAULT '[]'")
    except sqlite3.OperationalError:
        pass
    conn.commit()
    print("Created DerivedWord/RelatedWord tables and added columns")


def insert_synonym(c, sid, wid, kanji, kana):
    c.execute("INSERT INTO SynonymWord VALUES (?,?,?,?,?)", (sid, wid, "0", j(kanji), j(kana)))


def insert_antonym(c, aid, wid, kanji, kana):
    c.execute("INSERT INTO AntonymWord VALUES (?,?,?,?,?)", (aid, wid, "0", j(kanji), j(kana)))


def insert_derived(c, did, wid, kanji, kana):
    c.execute("INSERT INTO DerivedWord VALUES (?,?,?,?,?)", (did, wid, "0", j(kanji), j(kana)))


def insert_related(c, rid, wid, kanji, kana):
    c.execute("INSERT INTO RelatedWord VALUES (?,?,?,?,?)", (rid, wid, "0", j(kanji), j(kana)))


def update_word1(c):
    """書く (かく) - 五段动词"""
    wid = "1"
    insert_synonym(c, "sy1_2", wid, ["記","す"], ["き","す"])
    insert_synonym(c, "sy1_3", wid, ["書","き","記","す"], ["か","き","し","る","す"])
    insert_antonym(c, "a1_2", wid, ["消","す"], ["け","す"])
    insert_antonym(c, "a1_3", wid, ["破","る"], ["や","ぶ","る"])
    insert_derived(c, "d1_1", wid, ["書","き","方"], ["か","き","か","た"])
    insert_derived(c, "d1_2", wid, ["書","き","物"], ["か","き","も","の"])
    insert_derived(c, "d1_3", wid, ["書","道"], ["しょ","どう"])
    insert_related(c, "r1_1", wid, ["読","む"], ["よ","む"])
    insert_related(c, "r1_2", wid, ["覚","え","る"], ["お","ぼ","え","る"])
    insert_related(c, "r1_3", wid, ["記","録"], ["き","ろ","く"])
    c.execute("UPDATE Word SET synonymWordIdList = ? WHERE wordId = ?", (j(["sy1_1","sy1_2","sy1_3"]), wid))
    c.execute("UPDATE Word SET antonymWordIdList = ? WHERE wordId = ?", (j(["a1_1","a1_2","a1_3"]), wid))
    c.execute("UPDATE Word SET derivedWordIdList = ? WHERE wordId = ?", (j(["d1_1","d1_2","d1_3"]), wid))
    c.execute("UPDATE Word SET relatedWordIdList = ? WHERE wordId = ?", (j(["r1_1","r1_2","r1_3"]), wid))


def update_word2(c):
    """高い (たかい) - イ形容詞"""
    wid = "2"
    insert_synonym(c, "sy2_2", wid, ["嵩","高","い"], ["か","さ","だ","か","い"])
    insert_synonym(c, "sy2_3", wid, ["貴","い"], ["と","い"])
    insert_antonym(c, "a2_2", wid, ["安","い"], ["や","す","い"])
    insert_antonym(c, "a2_3", wid, ["小","さ","い"], ["ち","い","さ","い"])
    insert_derived(c, "d2_1", wid, ["高","さ"], ["た","か","さ"])
    insert_derived(c, "d2_2", wid, ["高","み"], ["た","か","み"])
    insert_derived(c, "d2_3", wid, ["高","値"], ["た","か","ね"])
    insert_related(c, "r2_1", wid, ["山"], ["や","ま"])
    insert_related(c, "r2_2", wid, ["塔"], ["とう"])
    insert_related(c, "r2_3", wid, ["屋","上"], ["お","く","じょう"])
    c.execute("UPDATE Word SET synonymWordIdList = ? WHERE wordId = ?", (j(["sy2_1","sy2_2","sy2_3"]), wid))
    c.execute("UPDATE Word SET antonymWordIdList = ? WHERE wordId = ?", (j(["a2_1","a2_2","a2_3"]), wid))
    c.execute("UPDATE Word SET derivedWordIdList = ? WHERE wordId = ?", (j(["d2_1","d2_2","d2_3"]), wid))
    c.execute("UPDATE Word SET relatedWordIdList = ? WHERE wordId = ?", (j(["r2_1","r2_2","r2_3"]), wid))


def update_word3(c):
    """静か (しずか) - ナ形容詞"""
    wid = "3"
    insert_synonym(c, "sy3_2", wid, ["静","謐"], ["せ","い","ひ","ゃ","く"])
    insert_synonym(c, "sy3_3", wid, ["閑","静"], ["か","ん","せ","い"])
    insert_antonym(c, "a3_2", wid, ["賑","や","か"], ["に","ぎ","や","か"])
    insert_antonym(c, "a3_3", wid, ["騒","が","し","い"], ["さ","わ","が","し","い"])
    insert_derived(c, "d3_1", wid, ["静","け","さ"], ["し","ず","け","さ"])
    insert_derived(c, "d3_2", wid, ["静","か","さ"], ["し","ず","か","さ"])
    insert_derived(c, "d3_3", wid, ["静","寂"], ["せ","い","じゃ","く"])
    insert_related(c, "r3_1", wid, ["静","止"], ["せ","い","し"])
    insert_related(c, "r3_2", wid, ["沈","黙"], ["ち","ん","も","く"])
    insert_related(c, "r3_3", wid, ["平","穏"], ["へ","い","お","ん"])
    c.execute("UPDATE Word SET synonymWordIdList = ? WHERE wordId = ?", (j(["sy3_1","sy3_2","sy3_3"]), wid))
    c.execute("UPDATE Word SET antonymWordIdList = ? WHERE wordId = ?", (j(["a3_1","a3_2","a3_3"]), wid))
    c.execute("UPDATE Word SET derivedWordIdList = ? WHERE wordId = ?", (j(["d3_1","d3_2","d3_3"]), wid))
    c.execute("UPDATE Word SET relatedWordIdList = ? WHERE wordId = ?", (j(["r3_1","r3_2","r3_3"]), wid))


def update_word4(c):
    """友達 (ともだち) - 名詞"""
    wid = "4"
    insert_synonym(c, "sy4_2", wid, ["友","人"], ["ゆ","う","じ","ん"])
    insert_synonym(c, "sy4_3", wid, ["知","人"], ["ち","じ","ん"])
    insert_antonym(c, "a4_2", wid, ["仇"], ["か","た","き"])
    insert_antonym(c, "a4_3", wid, ["ラ","イ","バ","ル"], ["ら","い","ば","る"])
    insert_derived(c, "d4_1", wid, ["友","情"], ["ゆ","う","じょう"])
    insert_derived(c, "d4_2", wid, ["友","好"], ["ゆ","う","こう"])
    insert_derived(c, "d4_3", wid, ["友","達","付","き","合","い"], ["と","も","だ","ち","づ","き","あ","い"])
    insert_related(c, "r4_1", wid, ["家","族"], ["か","ぞ","く"])
    insert_related(c, "r4_2", wid, ["同","僚"], ["ど","う","りょう"])
    insert_related(c, "r4_3", wid, ["知","人"], ["ち","じ","ん"])
    c.execute("UPDATE Word SET synonymWordIdList = ? WHERE wordId = ?", (j(["sy4_1","sy4_2","sy4_3"]), wid))
    c.execute("UPDATE Word SET antonymWordIdList = ? WHERE wordId = ?", (j(["a4_1","a4_2","a4_3"]), wid))
    c.execute("UPDATE Word SET derivedWordIdList = ? WHERE wordId = ?", (j(["d4_1","d4_2","d4_3"]), wid))
    c.execute("UPDATE Word SET relatedWordIdList = ? WHERE wordId = ?", (j(["r4_1","r4_2","r4_3"]), wid))


def verify(conn):
    c = conn.cursor()
    for wid in ['1','2','3','4']:
        c.execute("SELECT COUNT(*) FROM SynonymWord WHERE wordId = ?", (wid,))
        syn = c.fetchone()[0]
        c.execute("SELECT COUNT(*) FROM AntonymWord WHERE wordId = ?", (wid,))
        ant = c.fetchone()[0]
        c.execute("SELECT COUNT(*) FROM DerivedWord WHERE wordId = ?", (wid,))
        der = c.fetchone()[0]
        c.execute("SELECT COUNT(*) FROM RelatedWord WHERE wordId = ?", (wid,))
        rel = c.fetchone()[0]
        print(f"  wordId={wid}: synonyms={syn} antonyms={ant} derived={der} related={rel}")


def main():
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    add_columns_and_tables(conn)
    update_word1(c)
    update_word2(c)
    update_word3(c)
    update_word4(c)
    conn.commit()
    print("Inserted derived/related words and supplemented synonyms/antonyms")
    print("Verification:")
    verify(conn)
    conn.close()
    print("Done!")


if __name__ == '__main__':
    main()