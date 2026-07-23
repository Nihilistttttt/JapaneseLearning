import json
import pandas as pd

from splitword import kana_list


def build_kanji_kana_mapping(json_path):
    """构建汉字到假名的映射字典"""
    with open(json_path, encoding="utf-8") as f:
        w_list = json.load(f)

    w_dic = {}
    for item in w_list:
        kanji = item["kanji"]
        kana = item["kana"]
        if kanji in w_dic:
            w_dic[kanji].append(kana)
        else:
            w_dic[kanji] = [kana]
    return w_dic


def process_word(input_str, kanji_dict):
    """处理单个单词的转换逻辑，使用改进的回溯机制"""
    if input_str.count("-") != 1:
        return ""

    kanji_part, kana_part = input_str.split("-")
    kana_len = len(kana_part)
    used = [False] * kana_len
    result = []
    kanji_list = []
    kana_list = []
    missing_chars = set()  # 记录缺失的汉字

    def backtrack(index, current_pos):
        if index == len(kanji_part):
            return current_pos == kana_len

        char = kanji_part[index]
        if char not in kanji_dict:
            missing_chars.add(char)
            return False

        possible_kanas = sorted(kanji_dict[char], key=len, reverse=True)

        for kana in possible_kanas:
            end = current_pos + len(kana)
            if end > kana_len:
                continue

            # 检查匹配和占用状态
            if kana_part[current_pos:end] == kana and not any(used[current_pos:end]):
                # 标记占用
                for i in range(current_pos, end):
                    used[i] = True
                result.append(f"{char}-{kana}")

                if backtrack(index + 1, end):
                    return True

                # 回溯
                result.pop()
                for i in range(current_pos, end):
                    used[i] = False

        return False

    success = backtrack(0, 0)

    if missing_chars:
        return f"字典缺少汉字: {','.join(missing_chars)}","null"
    if success:
        for res in result:
            kanji_list.append(res.split("-")[0])
            kana_list.append(res.split("-")[1])
        print(kanji_list, kana_list)
        return  kanji_list, kana_list
        # return ",".join(result)
    else:
        return "无法匹配（可能字典不完整）","null"


# ----------------- 测试部分 -----------------
test_cases = [
    {
        "input": "鼻曲がり-はなまがり",
        "convert_dict": {'が': ['が'], 'り': ['り'], '鼻': ['はな', 'はなま'], '曲': ['ま']},
    },

    # 长假名优先导致后续失败
    {
        "input": "大本営-だいほんえい",
        "convert_dict": {'大': ['だい', 'おお'], '本': ['ほん', 'ぽん'], '営': ['えい']},
    },
    {
        "input": "自動販売機-じどうはんばいき",
        "convert_dict": {'自': ['じ'], '動': ['どう'], '販': ['はん'], '売': ['ばい']},
    },
{
        "input": "生活-せいかつ",
        "convert_dict": {
            '生': ['せ', 'せい', 'せいか', 'しょう', 'いき'],  # 包含多个长度候选
            '活': ['かつ', 'か', 'かつき']
        },
    },

    # 案例26：同长度候选冲突
    {
        "input": "方向-ほうこう",
        "convert_dict": {
            '方': ['ほう', 'ほん', 'はん'],  # 同长度不同内容
            '向': ['こう', 'こ', 'む']
        },
    },

    # 案例27：前缀重叠候选
    {
        "input": "研究室-けんきゅうしつ",
        "convert_dict": {
            '研': ['けん', 'けんき'],
            '究': ['きゅう', 'きゅ'],
            '室': ['しつ', 'し']
        },
    },

    # 案例28：超多候选干扰
    {
        "input": "新製品-しんせいひん",
        "convert_dict": {
            '新': ['しん', 'し', 'あら', 'にい', 'じん'],  # 5个候选
            '製': ['せい', 'せ', 'せいひ'],
            '品': ['ひん', 'しな', 'ひ']
        },
    },

    # 案例29：包含错误候选
    {
        "input": "飛行機-ひこうき",
        "convert_dict": {
            '飛': ['ひ', 'ひこ', 'ひこう'],  # 包含正确和干扰项
            '行': ['こう', 'こ'],
            '機': ['き', 'きい']
        },
    },

    # 案例30：候选包含子串
    {
        "input": "水泳-すいえい",
        "convert_dict": {
            '水': ['すい', 'す', 'みず', 'すいえ'],
            '泳': ['えい', 'え', 'よう']
        },
    },
    # 假名重叠冲突
    {
        "input": "流行-はやり",
        "convert_dict": {'流': ['はや', 'りゅう'], '行': ['り', 'こう']},
    },

    # 共享假名片段
    {
        "input": "中学校-ちゅうがっこう",
        "convert_dict": {'中': ['ちゅう', 'ちゅ'], '学': ['がく', 'が'], '校': ['こう']},
    },

    # 短假名优先导致冗余
    {
        "input": "食べ物-たべもの",
        "convert_dict": {'食': ['た', 'たべ'], 'べ': ['べ'], '物': ['もの', 'も']},
    },

    # 多选项回溯验证
    {
        "input": "空想-くうそう",
        "convert_dict": {'空': ['くう', 'そら'], '想': ['そう', 'おも']},
    },

    # 连续小假名干扰
    {
        "input": "小鳥遊-たかなし",
        "convert_dict": {'小': ['た', 'こ'], '鳥': ['か', 'とり'], '遊': ['なし', 'あそ']},
    },

    # 长链回溯验证
    {
        "input": "心電図検査-しんでんずけんさ",
        "convert_dict": {'心': ['しん'], '電': ['でん'], '図': ['ず'], '検': ['けん'], '査': ['さ']},
    },

    # 顺序错误检测
    {
        "input": "再現-さいげん",
        "convert_dict": {'再': ['さい', 'さ'], '現': ['げん', 'あらわ']},
    },

    # 多音字干扰
    {
        "input": "行方不明-ゆくえふめい",
        "convert_dict": {'行': ['ゆく', 'い'], '方': ['え', 'かた'], '不': ['ふ'], '明': ['めい']},
    },

    # 极小假名组合
    {
        "input": "三日-みっか",
        "convert_dict": {'三': ['み', 'みっ'], '日': ['か', 'にち']},
    },

    # 特殊促音处理
    {
        "input": "切符-きっぷ",
        "convert_dict": {'切': ['きっ', 'き'], '符': ['ぷ', 'ふ']},
    },

    # 跨字匹配检测
    {
        "input": "受話器-じゅわき",
        "convert_dict": {'受': ['じゅ'], '話': ['わ'], '器': ['き']},
    }
    # 其他测试案例...
]


def run_tests():
    """执行自动化测试"""
    JSON_PATH = "D:\\Libraries\\Documents\\日语数据处理\\words.json"
    kanji_dict = build_kanji_kana_mapping(JSON_PATH)
    wordList=['地上-ちじょう', 'を-を', '移動-いどう', 'すると-すると', '事件-じけん', 'が-が', '起こる-おこる', '危険-きけん', 'が-が', 'ある-ある', 'ため-ため', '動く-うごく', 'こと-こと', 'が-が', 'できない-できない', 'のだ-のだ']
    for word in wordList:
        kanji_list,kana_list=process_word(word,kanji_dict)
        print(kanji_list,kana_list)
    print("\n运行测试模式...")
    for i, case in enumerate(test_cases, 1):
        print(f"\n测试案例 {i}: {case['input']}")
        kanjiList,kanaList = process_word(case["input"], case["convert_dict"])
        print(f"结果: {kanjiList},{kanaList}")


# ----------------- 主程序 -----------------
def main():
    """主交互程序"""
    print("请选择运行模式：")
    print("1. 处理Excel文件")
    print("2. 运行测试")

    choice = input("请输入数字选择（1/2）: ")

    if choice == "1":
        # 处理Excel文件
        JSON_PATH = "D:\\Libraries\\Documents\\日语数据处理\\words.json"
        INPUT_EXCEL = "D:\\Libraries\\Documents\\日语数据处理\\单词.xlsx"
        OUTPUT_EXCEL = "D:\\Libraries\\Documents\\日语数据处理\\单词output.xlsx"

        kanji_dict = build_kanji_kana_mapping(JSON_PATH)
        df = pd.read_excel(INPUT_EXCEL)
        df[['汉字拆分', '假名拆分']] = df['word'].apply(
            lambda x: process_word(x, kanji_dict)
        ).apply(pd.Series)
        df.to_excel(OUTPUT_EXCEL, index=False)
        print(f"处理完成，结果已保存至 {OUTPUT_EXCEL}")

    elif choice == "2":
        run_tests()
    else:
        print("无效的输入，请重新运行程序")


if __name__ == "__main__":
    main()
