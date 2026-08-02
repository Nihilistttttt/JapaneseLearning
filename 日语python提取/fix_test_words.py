"""
修复测试词数据：
1. 删除wordId 1-4的脏数据（嵌套数组格式）
2. 为wordId 0000001-4补全Word/WordMeaning/WordSentence等关联数据
3. 修复wordIdList中"0"占位符，替换为真实存在的wordId
4. kanjiComponents/kanaComponents使用扁平数组格式
"""

import json, sqlite3, os

BASE = r'D:\Libraries\Projects\AndroidStudioProjects\Learn'
DB_PATH = os.path.join(BASE, 'app', 'src', 'main', 'assets', 'databases', 'word_database.db')

REAL_IDS = {
    '手紙':'1327720','日記':'1463930','毎日':'1524720','小説':'1348430',
    '山':'1302680','物価':'1502430','東京':'1447690','服':'1500940',
    '空':'1245280','部屋':'1499320','公園':'1273270','音楽':'1183720',
    '田舎':'1442750','友達':'1540170','読む':'1456360','低い':'1434180',
    '描く':'1583460','日本語':'1464530','何か':'1188270','彼':'1000580',
    'レポート':'1145990','値段':'1600160','鼻':'1486720',
    '書く':'1343950','高い':'1283190','静か':'1381820',
}

def j(obj): return json.dumps(obj, ensure_ascii=False)

ALL_TABLES = ['Word','BasicWord','WordMeaning','WordSentence','WordCollocation',
              'AntonymWord','SynonymWord','ConjugationForm','Etymology',
              'KanjiInfo','UsageDistinction','GrammarPoint','Idiom',
              'DerivedWord','RelatedWord']

def main():
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()
    # 删除wordId 1-4脏数据
    for wid in ['1','2','3','4']:
        for t in ALL_TABLES:
            c.execute(f"DELETE FROM {t} WHERE wordId = ?", (wid,))
    print("已删除wordId 1-4脏数据")
    # 删除wordId 0000001-4旧数据
    for wid in ['0000001','0000002','0000003','0000004']:
        for t in ALL_TABLES:
            c.execute(f"DELETE FROM {t} WHERE wordId = ?", (wid,))
    print("已删除wordId 0000001-4旧数据")
    # 重新插入
    insert_word1_kaku(c)
    insert_word2_takai(c)
    insert_word3_shizuka(c)
    insert_word4_tomodachi(c)
    conn.commit()
    conn.close()
    print("修复完成！")

def insert_word1_kaku(c):
    wid="0000001"; r=REAL_IDS
    c.execute("INSERT INTO Word VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",(wid,j([]),j([]),j([]),j([]),j([]),j(["cf1_1","cf1_2","cf1_3","cf1_4","cf1_5","cf1_6","cf1_7","cf1_8","cf1_9","cf1_10","cf1_11","cf1_12","cf1_13","cf1_14"]),j(["et1_1"]),j(["ki1_1"]),j(["ud1_1"]),j(["gp1_1","gp1_2"]),j(["id1_1","id1_2"]),j([]),j([])))
    c.execute("INSERT INTO BasicWord VALUES (?,?,?,?,?,?,?,?)",(wid,j(["書","く"]),j(["か","く"]),"","0","",5,800))
    c.execute("INSERT INTO WordMeaning VALUES (?,?,?,?,?)",("m1_1",wid,"書く","写，书写","動詞"))
    c.execute("UPDATE Word SET meaningIdList=? WHERE wordId=?",(j(["m1_1"]),wid))
    sentences=[
        ("s1_1",[["手","紙"],["を"],["書","く"]],[["て","がみ"],["を"],["か","く"]],j([r["手紙"],"0",wid]),"写信","manual"),
        ("s1_2",[["毎","日"],["日","記"],["を"],["書","い"],["て","い"],["る"]],[["まい","にち"],["にっ","き"],["を"],["か","い"],["て","い"],["る"]],j([r["毎日"],r["日記"],"0","0","0","0"]),"每天在写日记","manual"),
        ("s1_3",[["レ","ポ","ー","ト"],["を"],["書","か"],["な","け","れ","ば"],["な","ら","な","い"]],[["れ","ぽ","ー","と"],["を"],["か","か"],["な","け","れ","ば"],["な","ら","な","い"]],j([r["レポート"],"0",wid,"0","0"]),"必须写报告","manual"),
        ("s1_4",[["彼"],["は"],["小","説"],["を"],["書","い","た"]],[["かれ"],["は"],["しょう","せつ"],["を"],["か","い","た"]],j([r["彼"],"0",r["小説"],"0",wid]),"他写了小说","manual"),
        ("s1_5",[["日","本","語"],["で"],["手","紙"],["を"],["書","い"],["て"],["く","だ","さ","い"]],[["に","ほん","ご"],["で"],["て","がみ"],["を"],["か","い"],["て"],["く","だ","さ","い"]],j([r["日本語"],"0",r["手紙"],"0",wid,"0","0"]),"请用日语写信","manual"),
        ("s1_6",[["何","か"],["書","き"],["た","い"],["こ","と"],["が"],["あ","り","ま","す"],["か"]],[["なに","か"],["か","き"],["た","い"],["こ","と"],["が"],["あ","り","ま","す"],["か"]],j([r["何か"],wid,"0","0","0","0","0","0"]),"有什么想写的吗","manual"),
    ]
    for sid,kc,kac,wil,trans,src in sentences:
        c.execute("INSERT INTO WordSentence VALUES (?,?,?,?,?,?,?,?,?)",(sid,wid,"m1_1",j(kc),j(kac),wil,trans,src,""))
    c.execute("UPDATE Word SET sentenceIdList=? WHERE wordId=?",(j([s[0] for s in sentences]),wid))
    collocations=[
        ("c1_1",[["手","紙"],["を"],["書","く"]],[["て","がみ"],["を"],["か","く"]],j([r["手紙"],"0",wid]),"写信"),
        ("c1_2",[["日","記"],["を"],["書","く"]],[["にっ","き"],["を"],["か","く"]],j([r["日記"],"0",wid]),"写日记"),
        ("c1_3",[["小","説"],["を"],["書","く"]],[["しょう","せつ"],["を"],["か","く"]],j([r["小説"],"0",wid]),"写小说"),
        ("c1_4",[["レ","ポ","ー","ト"],["を"],["書","く"]],[["れ","ぽ","ー","と"],["を"],["か","く"]],j([r["レポート"],"0",wid]),"写报告"),
    ]
    for cid,kc,kac,wil,trans in collocations:
        c.execute("INSERT INTO WordCollocation VALUES (?,?,?,?,?,?,?,?)",(cid,wid,j(kc),j(kac),wil,trans,"manual",""))
    c.execute("UPDATE Word SET collocationIdList=? WHERE wordId=?",(j([c[0] for c in collocations]),wid))
    for cfid,fn,kc,kac,fnt in [("cf1_1","未然形",["書","か"],["か","か"],"未然形（否定）"),("cf1_2","連用形",["書","き"],["か","き"],"连用形（连接）"),("cf1_3","終止形",["書","く"],["か","く"],"终止形（结束）"),("cf1_4","連体形",["書","く"],["か","く"],"连体形（修饰体言）"),("cf1_5","仮定形",["書","け"],["か","け"],"假定形（条件）"),("cf1_6","命令形",["書","け"],["か","け"],"命令形（命令）"),("cf1_7","て形",["書","い","て"],["か","い","て"],"て形（连接）"),("cf1_8","た形",["書","い","た"],["か","い","た"],"过去形"),("cf1_9","ない形",["書","か","な","い"],["か","か","な","い"],"否定形"),("cf1_10","可能形",["書","け","る"],["か","け","る"],"可能形（能写）"),("cf1_11","受身形",["書","か","れ","る"],["か","か","れ","る"],"被动形（被写）"),("cf1_12","使役形",["書","か","せ","る"],["か","か","せ","る"],"使役形（让写）"),("cf1_13","意志形",["書","こ","う"],["か","こ","う"],"意志形（想写）"),("cf1_14","条件形",["書","け","ば"],["か","け","ば"],"条件形（如果写）")]:
        c.execute("INSERT INTO ConjugationForm VALUES (?,?,?,?,?,?)",(cfid,wid,fn,j(kc),j(kac),fnt))
    c.execute("INSERT INTO Etymology VALUES (?,?,?,?,?,?,?)",("et1_1",wid,"語源",j([["書","く"]]),j([["か","く"]]),j([]),"源自上古日语。汉字「書」由「聿」与「者」会意，表示用笔在书写。"))
    c.execute("INSERT INTO KanjiInfo VALUES (?,?,?,?,?,?,?)",("ki1_1",wid,"書",j(["ショ"]),j(["か.く","-が.き","-が.きる"]),j(["書籍","図書","文書","書道"]),"写，书写"))
    c.execute("INSERT INTO UsageDistinction VALUES (?,?,?,?,?,?)",("ud1_1",wid,"書く指书写文字，描く指绘画图形",j(["0"]),j(["書","く"]),j(["か","く"])))
    c.execute("INSERT INTO GrammarPoint VALUES (?,?,?,?,?,?,?,?,?,?)",("gp1_1",wid,"てしまう","表示动作完成或遗憾","書いてしまった","かいてしまった",j(["て","し","ま","う"]),j(["て","し","ま","う"]),j([]),j([])))
    c.execute("INSERT INTO GrammarPoint VALUES (?,?,?,?,?,?,?,?,?,?)",("gp1_2",wid,"られる","表示被动或尊敬","書かれる","かかれる",j(["ら","れ","る"]),j(["ら","れ","る"]),j([]),j([])))
    c.execute("INSERT INTO Idiom VALUES (?,?,?,?,?,?)",("id1_1",wid,j([["白","紙"],["に"],["書","く"]]),j([["はく","し"],["に"],["か","く"]]),"在白纸上写→从零开始",j(["0","0",wid])))
    c.execute("INSERT INTO Idiom VALUES (?,?,?,?,?,?)",("id1_2",wid,j([["筆"],["に"],["書","く"]]),j([["ふで"],["に"],["か","く"]]),"用笔书写",j(["0","0",wid])))
    c.execute("INSERT INTO SynonymWord VALUES (?,?,?,?,?)",("sy1_1",wid,"0",j(["描","く"]),j(["か","く"])))
    c.execute("UPDATE Word SET synonymWordIdList=? WHERE wordId=?",(j(["sy1_1"]),wid))
    c.execute("INSERT INTO AntonymWord VALUES (?,?,?,?,?)",("a1_1",wid,"0",j(["読","む"]),j(["よ","む"])))
    c.execute("UPDATE Word SET antonymWordIdList=? WHERE wordId=?",(j(["a1_1"]),wid))
    print("  已插入 書く (0000001)")

def insert_word2_takai(c):
    wid="0000002"; r=REAL_IDS
    c.execute("INSERT INTO Word VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",(wid,j([]),j([]),j([]),j([]),j([]),j(["cf2_1","cf2_2","cf2_3","cf2_4","cf2_5","cf2_6","cf2_7","cf2_8"]),j(["et2_1"]),j(["ki2_1"]),j(["ud2_1"]),j(["gp2_1"]),j(["id2_1","id2_2"]),j([]),j([])))
    c.execute("INSERT INTO BasicWord VALUES (?,?,?,?,?,?,?,?)",(wid,j(["高","い"]),j(["たか","い"]),"","2","",4,900))
    c.execute("INSERT INTO WordMeaning VALUES (?,?,?,?,?)",("m2_1",wid,"高い","高的，昂贵的","形容詞"))
    c.execute("UPDATE Word SET meaningIdList=? WHERE wordId=?",(j(["m2_1"]),wid))
    sentences=[
        ("s2_1",[["こ","の"],["山"],["は"],["と","て","も"],["高","い"]],[["こ","の"],["やま"],["は"],["と","て","も"],["たか","い"]],j(["0",r["山"],"0","0",wid]),"这座山很高","manual"),
        ("s2_2",[["東","京"],["の"],["物","価"],["は"],["高","い"]],[["とう","きょう"],["の"],["ぶっ","か"],["は"],["たか","い"]],j([r["東京"],"0",r["物価"],"0",wid]),"东京的物价很高","manual"),
        ("s2_3",[["高","い"],["ビ","ル"],["が"],["た","く","さ","ん"],["あ","る"]],[["たか","い"],["び","る"],["が"],["た","く","さ","ん"],["あ","る"]],j([wid,"0","0","0","0"]),"有很多高楼","manual"),
        ("s2_4",[["こ","の"],["服"],["は"],["高","す","ぎ","る"]],[["こ","の"],["ふく"],["は"],["たか","す","ぎ","る"]],j(["0","0",r["服"],wid]),"这件衣服太贵了","manual"),
        ("s2_5",[["空"],["が"],["高","く"],["な","っ","て"],["き","た"]],[["そら"],["が"],["たか","く"],["な","っ","て"],["き","た"]],j([r["空"],"0",wid,"0","0"]),"天空变高了","manual"),
        ("s2_6",[["高","い"],["と","こ","ろ"],["が"],["好","き","だ"]],[["たか","い"],["と","こ","ろ"],["が"],["す","き","だ"]],j([wid,"0","0","0"]),"喜欢高的地方","manual"),
    ]
    for sid,kc,kac,wil,trans,src in sentences:
        c.execute("INSERT INTO WordSentence VALUES (?,?,?,?,?,?,?,?,?)",(sid,wid,"m2_1",j(kc),j(kac),wil,trans,src,""))
    c.execute("UPDATE Word SET sentenceIdList=? WHERE wordId=?",(j([s[0] for s in sentences]),wid))
    collocations=[
        ("c2_1",[["山"],["が"],["高","い"]],[["やま"],["が"],["たか","い"]],j([r["山"],"0",wid]),"山高"),
        ("c2_2",[["物","価"],["が"],["高","い"]],[["ぶっ","か"],["が"],["たか","い"]],j([r["物価"],"0",wid]),"物价高"),
        ("c2_3",[["値","段"],["が"],["高","い"]],[["ね","だん"],["が"],["たか","い"]],j([r["値段"],"0",wid]),"价格贵"),
        ("c2_4",[["空"],["が"],["高","い"]],[["そら"],["が"],["たか","い"]],j([r["空"],"0",wid]),"天高"),
    ]
    for cid,kc,kac,wil,trans in collocations:
        c.execute("INSERT INTO WordCollocation VALUES (?,?,?,?,?,?,?,?)",(cid,wid,j(kc),j(kac),wil,trans,"manual",""))
    c.execute("UPDATE Word SET collocationIdList=? WHERE wordId=?",(j([c[0] for c in collocations]),wid))
    for cfid,fn,kc,kac,fnt in [("cf2_1","連用形",["高","く"],["たか","く"],"连用形"),("cf2_2","終止形",["高","い"],["たか","い"],"终止形"),("cf2_3","連体形",["高","い"],["たか","い"],"连体形"),("cf2_4","仮定形",["高","け","れ"],["たか","け","れ"],"假定形"),("cf2_5","て形",["高","く","て"],["たか","く","て"],"て形"),("cf2_6","た形",["高","か","っ","た"],["たか","か","っ","た"],"过去形"),("cf2_7","ない形",["高","く","な","い"],["たか","く","な","い"],"否定形"),("cf2_8","条件形",["高","け","れ","ば"],["たか","け","れ","ば"],"条件形")]:
        c.execute("INSERT INTO ConjugationForm VALUES (?,?,?,?,?,?)",(cfid,wid,fn,j(kc),j(kac),fnt))
    c.execute("INSERT INTO Etymology VALUES (?,?,?,?,?,?,?)",("et2_1",wid,"語源",j([["高","い"]]),j([["たか","い"]]),j([]),"源自上古日语「たかい」。「高」字本义为从下到上距离大，引申为价格昂贵。"))
    c.execute("INSERT INTO KanjiInfo VALUES (?,?,?,?,?,?,?)",("ki2_1",wid,"高",j(["コウ"]),j(["たか.い","-だか","たか.める"]),j(["高校","高級","最高","高価"]),"高，昂贵"))
    c.execute("INSERT INTO UsageDistinction VALUES (?,?,?,?,?,?)",("ud2_1",wid,"高い可指高度或价格",j(["0"]),j(["高","い"]),j(["たか","い"])))
    c.execute("INSERT INTO GrammarPoint VALUES (?,?,?,?,?,?,?,?,?,?)",("gp2_1",wid,"すぎる","过于...","高すぎる","たかすぎる",j(["す","ぎ","る"]),j(["す","ぎ","る"]),j([]),j([])))
    c.execute("INSERT INTO Idiom VALUES (?,?,?,?,?,?)",("id2_1",wid,j([["鼻"],["が"],["高","い"]]),j([["はな"],["が"],["たか","い"]]),"骄傲自满",j([r["鼻"],"0",wid])))
    c.execute("INSERT INTO Idiom VALUES (?,?,?,?,?,?)",("id2_2",wid,j([["高","み"],["の"],["見","物"]]),j([["たか","み"],["の"],["けん","ぶつ"]]),"坐山观虎斗",j([wid,"0","0"])))
    c.execute("INSERT INTO SynonymWord VALUES (?,?,?,?,?)",("sy2_1",wid,"0",j(["高","価"]),j(["こう","か"])))
    c.execute("UPDATE Word SET synonymWordIdList=? WHERE wordId=?",(j(["sy2_1"]),wid))
    c.execute("INSERT INTO AntonymWord VALUES (?,?,?,?,?)",("a2_1",wid,"0",j(["低","い"]),j(["ひく","い"])))
    c.execute("UPDATE Word SET antonymWordIdList=? WHERE wordId=?",(j(["a2_1"]),wid))
    print("  已插入 高い (0000002)")

def insert_word3_shizuka(c):
    wid="0000003"; r=REAL_IDS
    c.execute("INSERT INTO Word VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",(wid,j([]),j([]),j([]),j([]),j([]),j(["cf3_1","cf3_2","cf3_3","cf3_4","cf3_5","cf3_6","cf3_7"]),j(["et3_1"]),j(["ki3_1"]),j(["ud3_1"]),j(["gp3_1"]),j(["id3_1"]),j([]),j([])))
    c.execute("INSERT INTO BasicWord VALUES (?,?,?,?,?,?,?,?)",(wid,j(["静","か"]),j(["しず","か"]),"","1","",3,600))
    c.execute("INSERT INTO WordMeaning VALUES (?,?,?,?,?)",("m3_1",wid,"静か","安静的，平静的","形容詞"))
    c.execute("UPDATE Word SET meaningIdList=? WHERE wordId=?",(j(["m3_1"]),wid))
    sentences=[
        ("s3_1",[["こ","の"],["部","屋"],["は"],["静","か"],["で","す"]],[["こ","の"],["へ","や"],["は"],["しず","か"],["で","す"]],j(["0",r["部屋"],"0",wid,"0"]),"这个房间很安静","manual"),
        ("s3_2",[["夜"],["の"],["公","園"],["は"],["静","か"],["だ"]],[["よる"],["の"],["こう","えん"],["は"],["しず","か"],["だ"]],j(["0","0",r["公園"],"0",wid,"0"]),"夜晚的公园很安静","manual"),
        ("s3_3",[["静","か"],["な"],["音","楽"],["が"],["好","き"],["だ"]],[["しず","か"],["な"],["おん","がく"],["が"],["す","き"],["だ"]],j([wid,"0",r["音楽"],"0","0","0"]),"喜欢安静的音乐","manual"),
        ("s3_4",[["静","か"],["に"],["し","て"],["く","だ","さ","い"]],[["しず","か"],["に"],["し","て"],["く","だ","さ","い"]],j([wid,"0","0","0"]),"请安静","manual"),
        ("s3_5",[["田","舎"],["は"],["静","か"],["で"],["い","い"]],[["い","なか"],["は"],["しず","か"],["で"],["い","い"]],j([r["田舎"],"0",wid,"0","0"]),"乡下安静很好","manual"),
        ("s3_6",[["静","か"],["な"],["場","所"],["が"],["好","き"]],[["しず","か"],["な"],["ば","しょ"],["が"],["す","き"]],j([wid,"0","0","0","0"]),"喜欢安静的场所","manual"),
    ]
    for sid,kc,kac,wil,trans,src in sentences:
        c.execute("INSERT INTO WordSentence VALUES (?,?,?,?,?,?,?,?,?)",(sid,wid,"m3_1",j(kc),j(kac),wil,trans,src,""))
    c.execute("UPDATE Word SET sentenceIdList=? WHERE wordId=?",(j([s[0] for s in sentences]),wid))
    for cfid,fn,kc,kac,fnt in [("cf3_1","連用形",["静","か","に"],["しず","か","に"],"连用形"),("cf3_2","終止形",["静","か","だ"],["しず","か","だ"],"终止形"),("cf3_3","連体形",["静","か","な"],["しず","か","な"],"连体形"),("cf3_4","仮定形",["静","か","な","ら"],["しず","か","な","ら"],"假定形"),("cf3_5","て形",["静","か","で"],["しず","か","で"],"て形"),("cf3_6","ない形",["静","か","で","は","な","い"],["しず","か","で","は","な","い"],"否定形"),("cf3_7","条件形",["静","か","な","ら"],["しず","か","な","ら"],"条件形")]:
        c.execute("INSERT INTO ConjugationForm VALUES (?,?,?,?,?,?)",(cfid,wid,fn,j(kc),j(kac),fnt))
    c.execute("INSERT INTO Etymology VALUES (?,?,?,?,?,?,?)",("et3_1",wid,"語源",j([["静","か"]]),j([["しず","か"]]),j([]),"源自上古日语「しずか」。汉字「静」由「青」与「争」会意，本义为色彩分布均匀，引申为安静。"))
    c.execute("INSERT INTO KanjiInfo VALUES (?,?,?,?,?,?,?)",("ki3_1",wid,"静",j(["セイ","ジョウ"]),j(["しず.か"]),j(["静寂","静観","冷静"]),"安静，平静"))
    c.execute("INSERT INTO UsageDistinction VALUES (?,?,?,?,?,?)",("ud3_1",wid,"静か指环境无噪音，静寂更强调完全没有声音",j(["0"]),j(["静","か"]),j(["しず","か"])))
    c.execute("INSERT INTO GrammarPoint VALUES (?,?,?,?,?,?,?,?,?,?)",("gp3_1",wid,"になる/にする","表示状态变化","静かになる","しずかになる",j(["に","な","る"]),j(["に","な","る"]),j([]),j([])))
    c.execute("INSERT INTO Idiom VALUES (?,?,?,?,?,?)",("id3_1",wid,j([["静","か"],["の"],["海"]]),j([["しず","か"],["の"],["うみ"]]),"平静的海",j([wid,"0","0"])))
    print("  已插入 静か (0000003)")

def insert_word4_tomodachi(c):
    wid="0000004"; r=REAL_IDS
    c.execute("INSERT INTO Word VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",(wid,j([]),j([]),j([]),j([]),j([]),j(["cf4_1","cf4_2","cf4_3","cf4_4","cf4_5","cf4_6"]),j(["et4_1"]),j(["ki4_1"]),j(["ud4_1"]),j([]),j(["id4_1"]),j([]),j([])))
    c.execute("INSERT INTO BasicWord VALUES (?,?,?,?,?,?,?,?)",(wid,j(["友","達"]),j(["と","も","だ","ち"]),"","3","",4,700))
    c.execute("INSERT INTO WordMeaning VALUES (?,?,?,?,?)",("m4_1",wid,"友達","朋友","名詞"))
    c.execute("UPDATE Word SET meaningIdList=? WHERE wordId=?",(j(["m4_1"]),wid))
    sentences=[
        ("s4_1",[["友","達"],["と"],["遊","ぶ"]],[["と","も","だ","ち"],["と"],["あそ","ぶ"]],j([wid,"0","0"]),"和朋友玩","manual"),
        ("s4_2",[["良","い"],["友","達"],["が"],["い","る"]],[["よ","い"],["と","も","だ","ち"],["が"],["い","る"]],j(["0",wid,"0","0"]),"有好的朋友","manual"),
        ("s4_3",[["友","達"],["の"],["家"],["に"],["行","く"]],[["と","も","だ","ち"],["の"],["い","え"],["に"],["い","く"]],j([wid,"0","0","0","0"]),"去朋友家","manual"),
        ("s4_4",[["学","校"],["の"],["友","達"]],[["がっ","こう"],["の"],["と","も","だ","ち"]],j(["0","0",wid]),"学校的朋友","manual"),
        ("s4_5",[["友","達"],["を"],["大","事"],["に"],["す","る"]],[["と","も","だ","ち"],["を"],["だ","い","じ"],["に"],["す","る"]],j([wid,"0","0","0","0"]),"珍惜朋友","manual"),
        ("s4_6",[["新","し","い"],["友","達"],["が"],["で","き","た"]],[["あたら","し","い"],["と","も","だ","ち"],["が"],["で","き","た"]],j(["0",wid,"0","0"]),"交了新朋友","manual"),
    ]
    for sid,kc,kac,wil,trans,src in sentences:
        c.execute("INSERT INTO WordSentence VALUES (?,?,?,?,?,?,?,?,?)",(sid,wid,"m4_1",j(kc),j(kac),wil,trans,src,""))
    c.execute("UPDATE Word SET sentenceIdList=? WHERE wordId=?",(j([s[0] for s in sentences]),wid))
    for cfid,fn,kc,kac,fnt in [("cf4_1","単数形",["友","達"],["と","も","だ","ち"],"单数"),("cf4_2","複数形",["友","達","達"],["と","も","だ","ち","だ","ち"],"复数"),("cf4_3","連体形",["友","達","の"],["と","も","だ","ち","の"],"连体形"),("cf4_4","て形",["友","達","で"],["と","も","だ","ち","で"],"て形"),("cf4_5","ない形",["友","達","で","は","な","い"],["と","も","だ","ち","で","は","な","い"],"否定形"),("cf4_6","条件形",["友","達","な","ら"],["と","も","だ","ち","な","ら"],"条件形")]:
        c.execute("INSERT INTO ConjugationForm VALUES (?,?,?,?,?,?)",(cfid,wid,fn,j(kc),j(kac),fnt))
    c.execute("INSERT INTO Etymology VALUES (?,?,?,?,?,?,?)",("et4_1",wid,"語源",j([["友","達"]]),j([["と","も","だ","ち"]]),j([]),"「友」表示志同道合的人，「達」为复数接尾，合起来表示同伴、朋友。"))
    c.execute("INSERT INTO KanjiInfo VALUES (?,?,?,?,?,?,?)",("ki4_1",wid,"友",j(["ユウ"]),j(["と.も"]),j(["友情","友好","親友"]),"朋友"))
    c.execute("INSERT INTO UsageDistinction VALUES (?,?,?,?,?,?)",("ud4_1",wid,"友達指一般朋友，親友指亲密好友",j(["0"]),j(["友","達"]),j(["と","も","だ","ち"])))
    c.execute("INSERT INTO Idiom VALUES (?,?,?,?,?,?)",("id4_1",wid,j([["患","難"],["の"],["友","達"]]),j([["かん","なん"],["の"],["と","も","だ","ち"]]),"患难之交",j(["0","0",wid])))
    c.execute("INSERT INTO SynonymWord VALUES (?,?,?,?,?)",("sy4_1",wid,"0",j(["仲","間"]),j(["なか","ま"])))
    c.execute("UPDATE Word SET synonymWordIdList=? WHERE wordId=?",(j(["sy4_1"]),wid))
    print("  已插入 友達 (0000004)")

if __name__=='__main__':
    main()
