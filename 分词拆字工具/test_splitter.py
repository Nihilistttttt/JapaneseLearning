"""
拆分引擎测试用例

覆盖：
  - 基础两汉字词
  - 多汉字词
  - 送假名词（食べ物、読み方等）
  - 纯假名词
  - 边界情况（お婆さん、お早うございます、お土産等）
  - 促音/拗音
  - 多音字干扰
  - 字典缺少汉字
  - 真实数据测试
"""

import json
import sys
import os

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from kanji_kana_splitter import split_word, is_kana_char, katakana_to_hiragana

PASS = 0
FAIL = 0


def test_case(name, kanji_str, kana_str, kanji_dict, expected_kanji, expected_kana):
    global PASS, FAIL
    result = split_word(kanji_str, kana_str, kanji_dict)
    if result is None:
        if expected_kanji is None:
            PASS += 1
            print(f'  PASS: {name} → None (expected)')
        else:
            FAIL += 1
            print(f'  FAIL: {name} → None, expected ({expected_kanji}, {expected_kana})')
        return

    kanji_comps, kana_comps = result
    if kanji_comps == expected_kanji and kana_comps == expected_kana:
        PASS += 1
        print(f'  PASS: {name}')
    else:
        FAIL += 1
        print(f'  FAIL: {name}')
        print(f'    got:      kanji={kanji_comps}, kana={kana_comps}')
        print(f'    expected: kanji={expected_kanji}, kana={expected_kana}')


def test_info(name, kanji_str, kana_str, kanji_dict):
    """打印拆分结果，不判断对错"""
    result = split_word(kanji_str, kana_str, kanji_dict)
    if result:
        kc, sc = result
        print(f'  INFO: {name} → kanji={kc}, kana={sc}')
    else:
        print(f'  INFO: {name} → None')


def run_basic_tests():
    print('\n=== 基础两汉字词 ===')
    d = {'大': ['だい', 'おお'], '本': ['ほん', 'ぽん'], '営': ['えい']}
    test_case('大本営', '大本営', 'だいほんえい', d,
              ['大', '本', '営'], ['だい', 'ほん', 'えい'])

    d = {'自': ['じ'], '動': ['どう'], '販': ['はん'], '売': ['ばい'], '機': ['き']}
    test_case('自動販売機', '自動販売機', 'じどうはんばいき', d,
              ['自', '動', '販', '売', '機'], ['じ', 'どう', 'はん', 'ばい', 'き'])

    d = {'生': ['せい', 'せ', 'しょう'], '活': ['かつ']}
    test_case('生活(せいかつ)', '生活', 'せいかつ', d,
              ['生', '活'], ['せい', 'かつ'])

    d = {'方': ['ほう', 'ほん', 'はん'], '向': ['こう', 'こ', 'む']}
    test_case('方向', '方向', 'ほうこう', d,
              ['方', '向'], ['ほう', 'こう'])

    d = {'研': ['けん', 'けんき'], '究': ['きゅう', 'きゅ'], '室': ['しつ', 'し']}
    test_case('研究室', '研究室', 'けんきゅうしつ', d,
              ['研', '究', '室'], ['けん', 'きゅう', 'しつ'])

    d = {'新': ['しん', 'し', 'あら', 'にい', 'じん'], '製': ['せい', 'せ', 'せいひ'], '品': ['ひん', 'しな', 'ひ']}
    test_case('新製品', '新製品', 'しんせいひん', d,
              ['新', '製', '品'], ['しん', 'せい', 'ひん'])

    d = {'飛': ['ひ', 'ひこ', 'ひこう'], '行': ['こう', 'こ'], '機': ['き', 'きい']}
    test_case('飛行機', '飛行機', 'ひこうき', d,
              ['飛', '行', '機'], ['ひ', 'こう', 'き'])

    d = {'水': ['すい', 'す', 'みず', 'すいえ'], '泳': ['えい', 'え', 'よう']}
    test_case('水泳', '水泳', 'すいえい', d,
              ['水', '泳'], ['すい', 'えい'])

    d = {'再': ['さい', 'さ'], '現': ['げん', 'あらわ']}
    test_case('再現', '再現', 'さいげん', d,
              ['再', '現'], ['さい', 'げん'])

    d = {'心': ['しん'], '電': ['でん'], '図': ['ず'], '検': ['けん'], '査': ['さ']}
    test_case('心電図検査', '心電図検査', 'しんでんずけんさ', d,
              ['心', '電', '図', '検', '査'], ['しん', 'でん', 'ず', 'けん', 'さ'])


def run_okurigana_tests():
    print('\n=== 送假名词 ===')
    d = {'食': ['た', 'たべ'], '物': ['もの', 'も']}
    test_case('食べ物', '食べ物', 'たべもの', d,
              ['食', 'べ', '物'], ['た', '', 'もの'])

    d = {'読': ['よ'], '方': ['かた', 'ほう']}
    test_case('読み方', '読み方', 'よみかた', d,
              ['読', 'み', '方'], ['よ', '', 'かた'])

    d = {'流': ['はや', 'りゅう'], '行': ['り', 'こう']}
    test_case('流行(はやり)', '流行', 'はやり', d,
              ['流', '行'], ['はや', 'り'])

    d = {'空': ['くう', 'そら'], '想': ['そう', 'おも']}
    test_case('空想', '空想', 'くうそう', d,
              ['空', '想'], ['くう', 'そう'])

    d = {'振': ['ふり'], '替': ['かえ'], '日': ['び', 'にち', 'か']}
    test_case('振替日', '振替日', 'ふりかえび', d,
              ['振', '替', '日'], ['ふり', 'かえ', 'び'])

    d = {'書': ['かき'], '込': ['こ']}
    test_case('書込み', '書込み', 'かきこみ', d,
              ['書', '込', 'み'], ['かき', 'こ', ''])

    d = {'消': ['しょう'], '火': ['か'], '栓': ['せん']}
    test_case('消火栓', '消火栓', 'しょうかせん', d,
              ['消', '火', '栓'], ['しょう', 'か', 'せん'])

    d = {'切': ['きっ', 'き'], '符': ['ぷ', 'ふ']}
    test_case('切符(促音)', '切符', 'きっぷ', d,
              ['切', '符'], ['きっ', 'ぷ'])

    d = {'受': ['じゅ'], '話': ['わ'], '器': ['き']}
    test_case('受話器', '受話器', 'じゅわき', d,
              ['受', '話', '器'], ['じゅ', 'わ', 'き'])

    d = {'中': ['ちゅう', 'ちゅ'], '学': ['がく', 'が'], '校': ['こう']}
    test_case('中学校(がっこう-字典缺がっ)', '中学校', 'ちゅうがっこう', d,
              None, None)

    d = {'三': ['み', 'みっ'], '日': ['か', 'にち']}
    test_case('三日', '三日', 'みっか', d,
              ['三', '日'], ['みっ', 'か'])

    d = {'行': ['ゆく', 'い'], '方': ['え', 'かた'], '不': ['ふ'], '明': ['めい']}
    test_case('行方不明', '行方不明', 'ゆくえふめい', d,
              ['行', '方', '不', '明'], ['ゆく', 'え', 'ふ', 'めい'])

    d = {'小': ['た', 'こ'], '鳥': ['か', 'とり'], '遊': ['なし', 'あそ']}
    test_case('小鳥遊(たかなし)', '小鳥遊', 'たかなし', d,
              ['小', '鳥', '遊'], ['た', 'か', 'なし'])


def run_pure_kana_tests():
    print('\n=== 纯假名词 ===')
    test_case('を', 'を', 'を', {},
              ['を'], [''])
    test_case('する', 'する', 'する', {},
              ['する'], [''])
    test_case('こと', 'こと', 'こと', {},
              ['こと'], [''])


def run_edge_case_tests():
    print('\n=== 边界情况 ===')

    d = {'婆': ['ばあ', 'ば', 'ばば']}
    test_case('お婆さん(さん夹在汉字形式中)', 'お婆さん', 'おばあさん', d,
              ['お', '婆', 'さ', 'ん'], ['', 'ばあ', '', ''])

    d = {'早': ['はよ', 'はや', 'さ', 'そう']}
    test_case('お早うございます', 'お早うございます', 'おはようございます', d,
              ['お', '早', 'う', 'ご', 'ざ', 'い', 'ま', 'す'], ['', 'はよ', '', '', '', '', '', ''])

    d = {'土': ['みやげ', 'つち', 'と', 'ど'], '産': ['さん', 'ざん']}
    test_case('お土産(熟字训-无法拆分)', 'お土産', 'おみやげ', d,
              None, None)

    d = {'大': ['だい', 'おお'], '本': ['ほん', 'ぽん'], '営': ['えい']}
    test_case('大本営(不同读音)', '大本営', 'おおほんえい', d,
              ['大', '本', '営'], ['おお', 'ほん', 'えい'])


def run_missing_kanji_tests():
    print('\n=== 字典缺少汉字 ===')
    d = {'大': ['だい']}
    test_case('字典缺本', '大本', 'だいほん', d,
              None, None)

    d = {}
    test_case('空字典', '日本', 'にほん', d,
              None, None)


def run_real_data_tests():
    print('\n=== 真实数据测试（使用words.json）===')
    dict_path = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理\words.json'
    if not os.path.exists(dict_path):
        print(f'  跳过：words.json不存在于 {dict_path}')
        return

    with open(dict_path, encoding='utf-8') as f:
        w_list = json.load(f)

    kanji_dict = {}
    for item in w_list:
        kanji_dict.setdefault(item['kanji'], []).append(item['kana'])

    test_words = [
        ('日本', 'にほん'),
        ('学校', 'がっこう'),
        ('食べ物', 'たべもの'),
        ('読み方', 'よみかた'),
        ('自動車', 'じどうしゃ'),
        ('研究所', 'けんきゅうじょ'),
        ('お土産', 'おみやげ'),
        ('お婆さん', 'おばあさん'),
        ('お早うございます', 'おはようございます'),
        ('切符', 'きっぷ'),
        ('流行', 'はやり'),
        ('心電図', 'しんでんず'),
        ('飛行機', 'ひこうき'),
        ('水泳', 'すいえい'),
        ('新製品', 'しんせいひん'),
        ('大本営', 'だいほんえい'),
        ('中学校', 'ちゅうがっこう'),
        ('行方不明', 'ゆくえふめい'),
        ('小鳥遊', 'たかなし'),
    ]

    for kanji_str, kana_str in test_words:
        test_info(f'{kanji_str}-{kana_str}', kanji_str, kana_str, kanji_dict)


def run_all_tests():
    global PASS, FAIL
    PASS = 0
    FAIL = 0

    run_basic_tests()
    run_okurigana_tests()
    run_pure_kana_tests()
    run_edge_case_tests()
    run_missing_kanji_tests()
    run_real_data_tests()

    print(f'\n=== 结果: {PASS} PASS, {FAIL} FAIL ===')
    return FAIL == 0


if __name__ == '__main__':
    success = run_all_tests()
    sys.exit(0 if success else 1)
