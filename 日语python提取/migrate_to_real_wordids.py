"""
把wordId="1"-"4"的派生词/关联词/补充近义词/补充反义词数据迁移到wordId="0000001"-"0000004"
"""
import sqlite3, json

DB_PATH = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(DB_PATH)
c = conn.cursor()

mapping = {'1': '0000001', '2': '0000002', '3': '0000003', '4': '0000004'}

for old_wid, new_wid in mapping.items():
    # SynonymWord: 把syN_2, syN_3的wordId改成new_wid
    c.execute("UPDATE SynonymWord SET wordId = ? WHERE wordId = ? AND synonymWordId LIKE 'sy%_%%'", (new_wid, old_wid))
    # AntonymWord: 把aN_2, aN_3的wordId改成new_wid
    c.execute("UPDATE AntonymWord SET wordId = ? WHERE wordId = ? AND antonymWordId LIKE 'a%_%%'", (new_wid, old_wid))
    # DerivedWord: 全部改成new_wid
    c.execute("UPDATE DerivedWord SET wordId = ? WHERE wordId = ?", (new_wid, old_wid))
    # RelatedWord: 全部改成new_wid
    c.execute("UPDATE RelatedWord SET wordId = ? WHERE wordId = ?", (new_wid, old_wid))

    # 获取old_wid的idList
    c.execute("SELECT synonymWordIdList, antonymWordIdList, derivedWordIdList, relatedWordIdList FROM Word WHERE wordId = ?", (old_wid,))
    row = c.fetchone()
    if row:
        syn_list = json.loads(row[0])
        ant_list = json.loads(row[1])
        der_list = json.loads(row[2])
        rel_list = json.loads(row[3])
        # 更新new_wid的idList
        c.execute("UPDATE Word SET synonymWordIdList = ?, antonymWordIdList = ?, derivedWordIdList = ?, relatedWordIdList = ? WHERE wordId = ?",
                  (json.dumps(syn_list), json.dumps(ant_list), json.dumps(der_list), json.dumps(rel_list), new_wid))
        print(f"{old_wid} -> {new_wid}: syn={syn_list} ant={ant_list} der={der_list} rel={rel_list}")

conn.commit()

# 验证
for new_wid in ['0000001','0000002','0000003','0000004']:
    c.execute("SELECT synonymWordIdList, antonymWordIdList, derivedWordIdList, relatedWordIdList FROM Word WHERE wordId = ?", (new_wid,))
    row = c.fetchone()
    print(f"  {new_wid}: syn={json.loads(row[0])} ant={json.loads(row[1])} der={json.loads(row[2])} rel={json.loads(row[3])}")
    c.execute("SELECT COUNT(*) FROM SynonymWord WHERE wordId = ?", (new_wid,))
    print(f"    SynonymWord={c.fetchone()[0]}", end="")
    c.execute("SELECT COUNT(*) FROM DerivedWord WHERE wordId = ?", (new_wid,))
    print(f" DerivedWord={c.fetchone()[0]}", end="")
    c.execute("SELECT COUNT(*) FROM RelatedWord WHERE wordId = ?", (new_wid,))
    print(f" RelatedWord={c.fetchone()[0]}")

conn.close()
print("Done!")