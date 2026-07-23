import MeCab
import pandas as pd

# 初始化全局变量
first_word_dict = set()
prefixes = []
suffixes = []
second_word_dict = set()
splitList = []

def create_kanji_kana_dict(file_path):
    """
    将Excel中的Kanji列作为键，Kana列的值合并为列表的字典

    :param file_path: Excel文件路径
    :return: 格式为 {Kanji: [kana1, kana2,...]} 的字典
    """
    df = pd.read_excel(file_path)
    result_dict = {}

    for _, row in df.iterrows():
        kanji = row['kanji']
        kana = row['kana']

        # 跳过空值
        if pd.isna(kanji) or pd.isna(kana):
            continue

        # 统一类型并去前后空格
        kanji = str(kanji).strip()
        kana = str(kana).strip()

        if kanji not in result_dict:
            result_dict[kanji] = []

        # 避免重复添加完全相同的kana（可选）
        if kana not in result_dict[kanji]:
            result_dict[kanji].append(kana)

    return result_dict

words_dict = create_kanji_kana_dict("D:\\Libraries\\Documents\\日语数据处理\\单词.xlsx")

# 加载字典
def load_dict():
    global first_word_dict, prefixes, suffixes, second_word_dict
    # first_word_dict Excel 文件路径
    first_word_dict_file_path = 'D:\\Libraries\\Documents\\分词字典.xlsx'
    # second_word_dict Excel 文件路径
    second_word_dict_file_path = 'D:\\Libraries\\Documents\\单词666.xlsx'

    # 读取 Excel 文件
    df_1 = pd.read_excel(first_word_dict_file_path, engine='openpyxl')
    # 从第一列获取字典
    first_word_dict = set(df_1['Word'].tolist())
    # 从第二列获取前缀
    prefixes = df_1['prefixes'].tolist()
    # 从第三列获取后缀
    suffixes = df_1['Suffix'].tolist()

    # 读取 Excel 文件
    df_2 = pd.read_excel(second_word_dict_file_path, engine='openpyxl')
    # 从第一列获取单词字典
    second_word_dict = set(df_2['汉字'].tolist())


# mecab分词
def mecab_process():
    global splitList
    sentence_file_path = 'D:\\Libraries\\Documents\\例句.txt'
    # mecab分词
    tagger = MeCab.Tagger("-Owakati")
    with open(sentence_file_path, 'r', encoding='utf-8') as file:  # 打开文件并读取句子
        sentence = file.read()
    splitList = tagger.parse(sentence).split()
    print(splitList)


# 初步处理分词结果
def process_words(word_list, word_dict):
    i = 0
    result = []
    while i < len(word_list):
        # 尝试拼接当前词与下一个词
        if i + 1 < len(word_list):
            combined = word_list[i] + word_list[i + 1]
            # 如果拼接后的词在词库中，则替换为拼接后的词
            if combined in word_dict:
                result.append(combined)
                i += 2  # 跳过下一个词，因为它已经与当前词合并
                continue
        # 否则直接添加当前词
        result.append(word_list[i])
        i += 1
    return result


# 处理前缀、后缀
def process_words_final(word_list):
    i = 0
    result = []
    # 后前数组
    global suffixes
    # 后缀数组
    global prefixes
    while i < len(word_list):
        # 检查当前词是否是前缀之一，并且后面有词可以合并
        if word_list[i] in prefixes and i + 1 < len(word_list):
            combined = word_list[i] + word_list[i + 1]
            result.append(combined)
            i += 2  # 跳过下一个词，因为它已经与当前词合并
            continue
        # 检查当前词是否是后缀之一，并且结果列表中至少有一个词（即前一个词存在）
        if word_list[i] in suffixes and result:
            # 将当前词与前一个词合并
            last_word = result.pop()  # 取出最后一个词
            combined = last_word + word_list[i]  # 与当前词合并
            result.append(combined)  # 将合并后的词放回结果列表
            i += 1  # 移动到下一个词
            continue
        # 直接添加当前词
        result.append(word_list[i])
        i += 1
    return result


# 循坏处理，得到结果
def get_process_result():
    # 输入单词列表
    input_words = splitList
    output_words = []
    # 处理
    for _ in range(10):
        output_words = process_words(input_words, first_word_dict)
        input_words = output_words  # 更新输入列表为最新的输出列表
    print(output_words)
    # 最后一次循环结束后调用 process_words_final处理前缀、后缀
    output_list = process_words(process_words_final(output_words), second_word_dict)
    return output_list


load_dict()
mecab_process()
output_list = get_process_result()
print(output_list)


def print_output_words(output_words):
    output_file_path = 'D:\\Libraries\\Documents\\例句_结果.txt'
    with open(output_file_path, 'w', encoding='utf-8') as file:  # 打开文件准备写入
        word_list=[]
        word_not_in_dict_list=[]
        for word in output_words:
            if word in words_dict:
                kana = words_dict[word][0]
            else:
                word_not_in_dict_list.append(word)
            file.write("\"" +word + "-" + kana + "\"" + ',')  # 写入单词并追加下划线
            word_list.append(  word +"-" + kana )
            # print("\"" + word + "-" + kana + "\"" + ',')
            kana = ""
        print(word_list)
        print(word_not_in_dict_list)

print_output_words(output_list)
