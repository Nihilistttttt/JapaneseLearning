import copy
import json
import pandas as pd

# 匹配算法
def find_unmatched_parts(convert_dict, kanji_str, kana_str):
    """综合优化版匹配算法，支持多汉字组合的非字典匹配"""
    kanji_list = list(kanji_str)
    best_split = []
    min_unmatched = float('inf')
    max_coverage = 0

    def is_valid_kana(s):
        return all(0x3040 <= ord(c) <= 0x30FF for c in s)

    def backtrack(kanji_idx, kana_idx, current_split, unmatched_count):
        nonlocal best_split, min_unmatched, max_coverage

        # 终止条件：所有汉字处理完毕
        if kanji_idx == len(kanji_list):
            if kana_idx == len(kana_str):
                current_coverage = sum(len(k) for _, k in current_split)
                # 更新最优解的判断条件
                if (unmatched_count < min_unmatched) or \
                        (unmatched_count == min_unmatched and current_coverage > max_coverage):
                    best_split = copy.deepcopy(current_split)
                    min_unmatched = unmatched_count
                    max_coverage = current_coverage
            return

        current_char = kanji_list[kanji_idx]
        remaining_kana = len(kana_str) - kana_idx

        # 处理汉字中的假名字符（直接匹配）
        if is_valid_kana(current_char):
            if kana_str.startswith(current_char, kana_idx):
                backtrack(
                    kanji_idx + 1,
                    kana_idx + 1,
                    current_split + [(current_char, current_char)],
                    unmatched_count
                )
            return

        # 阶段1：字典匹配（按长度降序尝试）
        for kana in sorted(convert_dict.get(current_char, []), key=lambda x: -len(x)):
            k_len = len(kana)
            if k_len > remaining_kana:
                continue
            if kana_str[kana_idx:kana_idx + k_len] == kana:
                backtrack(
                    kanji_idx + 1,
                    kana_idx + k_len,
                    current_split + [(current_char, kana)],
                    unmatched_count
                )

        # 阶段2：非字典匹配（处理多汉字组合）
        max_kanji_group_size = len(kanji_list) - kanji_idx
        for n in range(max_kanji_group_size, 0, -1):  # 优先尝试更大的组合
            kanji_group = ''.join(kanji_list[kanji_idx:kanji_idx + n])
            remaining_kana_group = len(kana_str) - kana_idx
            max_try_kana = min(remaining_kana_group, 8)  # 最大尝试分割长度设为8

            for l in range(max_try_kana, 0, -1):
                candidate = kana_str[kana_idx:kana_idx + l]
                if not is_valid_kana(candidate):
                    continue

                # 检查后续匹配可能性
                remaining_kanji_after = len(kanji_list) - (kanji_idx + n)
                remaining_kana_after = remaining_kana_group - l
                if remaining_kanji_after > remaining_kana_after:
                    continue  # 后续假名不足以覆盖剩余汉字

                # 跳过字典中存在的精确匹配（已在阶段1处理）
                # 检查组合中的每个字符是否在字典中存在该候选分割
                conflict = False
                for i in range(n):
                    char = kanji_list[kanji_idx + i]
                    if char in convert_dict and candidate in convert_dict[char]:
                        conflict = True
                        break
                if conflict:
                    continue

                backtrack(
                    kanji_idx + n,
                    kana_idx + l,
                    current_split + [(kanji_group, candidate)],
                    unmatched_count + n
                )

    backtrack(0, 0, [], 0)

    # 生成最终结果
    return [f"{k}-{v}" for k, v in best_split
            if k not in convert_dict or v not in convert_dict.get(k, [])]

# 加载JSON文件并构建字典
with open("D:\\Libraries\\Documents\\日语数据处理\\words.json", encoding="utf-8") as f:
    w_list = json.load(f)

w_dic = {}

for item in w_list:
    if item["kanji"] in w_dic:
        w_dic[item["kanji"]].add(item["kana"])
    else:
        w_dic[item["kanji"]] = {item["kana"]}

# 定义处理函数，将未匹配的部分返回
def proc(convert_dict, kanji_str, kana_str):
    return find_unmatched_parts(convert_dict, kanji_str, kana_str)

# 处理Excel文件
def simplify_kanji(input_s):
    # 简化输入字符串为汉字和假名部分，这里假设输入格式为"汉字-假名"
    return input_s.split("-", 1)

# 指定Excel文件路径
file_path = 'D:\\Libraries\\Documents\\日语数据处理\\单词.xlsx'
file_output_path = 'D:\\Libraries\\Documents\\日语数据处理\\单词666.xlsx'

# 使用pandas读取Excel文件
df = pd.read_excel(file_path)

# 遍历每一行的第一列，获取input_s
for index, row in df.iterrows():
    try:
        input_s = row['word']  # 假设第一列的列名为'word'
        p1, p2 = simplify_kanji(input_s)
        r_dict = proc(w_dic, p1, p2)

        # 如果返回的是错误信息，则直接记录错误信息
        if isinstance(r_dict, str) and r_dict.startswith("处理错误:"):
            df.at[index, '第四列的列名'] = r_dict  # 替换'第四列的列名'为实际的列名
        else:
            df.at[index, '第四列的列名'] = ','.join(r_dict)  # 将结果格式化为字符串
    except Exception as e:
        df.at[index, '第四列的列名'] = f"格式错误或处理失败: {str(e)}"  # 记录格式错误或处理失败的信息

# 保存修改后的DataFrame到新的Excel文件
df.to_excel(file_output_path, index=False)

# 测试案例
test_cases = [
    # 原失败案例（已修复）
    {
        "input": "鼻曲がり-はなままががり",
        "convert_dict": {'が': ['が'], 'り': ['り']},
        "expected": ['鼻曲-はなままが']
    },
    {
        "input": "鼻曲がり-はなままががり",
        "convert_dict": {'鼻': ['はな'], '曲': ['まが', 'ま'], 'が': ['が'], 'り': ['り']},
        "expected": ['曲-ままが']
    },
    {
        "input": "鼻曲がり-はなががり",
        "convert_dict": {'鼻': ['はな'], '曲': ['まが', 'ま'], 'が': ['が'], 'り': ['り']},
        "expected": ['曲-が']
    },
    # 新增混合假名案例
    {
        "input": "書込み-かきこみ",
        "convert_dict": {'書': ['かき'], 'み': ['み']},
        "expected": ['込-こ']
    },
    {
        "input": "吊革-つりかわ",
        "convert_dict": {'革': ['かわ']},
        "expected": ['吊-つり']
    },
    {
        "input": "打合せ-うちあわせ",
        "convert_dict": {'打': ['うち'], 'せ': ['せ']},
        "expected": ['合-あわ']
    },
    {
        "input": "読み方-よみかた",
        "convert_dict": {'読': ['よ'], 'み': ['み']},
        "expected": ['方-かた']
    },
    {
        "input": "消火栓-しょうかせん",
        "convert_dict": {'消': ['しょう'], '栓': ['せん']},
        "expected": ['火-か']
    },
    {
        "input": "振替日-ふりかえび",
        "convert_dict": {'振': ['ふり'], '替': ['かえ']},
        "expected": ['日-び']
    },
    # 复杂促音案例
    {
        "input": "切符-きっぷ",
        "convert_dict": {'符': ['ぷ']},
        "expected": ['切-きっ']
    },
    {
        "input": "雑誌-ざっし",
        "convert_dict": {'雑': ['ざっ']},
        "expected": ['誌-し']
    }
]

# 执行测试
for i, case in enumerate(test_cases, 1):
    print(f"\n测试案例 {i}: {case['input']} ({case.get('desc', '')})")
    kanji_part, kana_part = case["input"].split("-", 1)
    kanji_part = kanji_part.strip()
    kana_part = kana_part.strip()
    result = find_unmatched_parts(case["convert_dict"], kanji_part, kana_part)
    print(f"预期: {case['expected']}")
    print(f"结果: {result}")
    print(f"测试 {'通过' if sorted(result) == sorted(case['expected']) else '失败'}!")