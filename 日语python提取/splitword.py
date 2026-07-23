import openpyxl

# 打开Excel文件
file_path = r"D:\Libraries\Documents\常用日语汉字表_转换后.xlsx"
wb = openpyxl.load_workbook(file_path)
sheet = wb.active  # 获取活动工作表

# 获取D列和E列的数据
kanji_list = []
kana_list = []

# 假设D列是kanji，E列是kana
for row in sheet.iter_rows(min_row=2, max_col=5, values_only=True):
    kanji = row[0]  # D列是kanji
    kana = row[1]  # E列是kana
    if kanji and kana:
        kanji_list.append(kanji)
        kana_list.append(kana)

# 输出数据，模拟创建Word对象并打印
word_objects = []

# 为每个kanji和kana分配一个ID，ID从0000开始，直到9999
for index, (kanji, kana) in enumerate(zip(kanji_list, kana_list)):
    # 使用format函数给ID补零，确保4位
    id_str = f"{index:04d}"
    word_objects.append(f"new Word(\"{id_str}\", \"{kanji}\", \"{kana}\")")

# 打印出Java代码片段
print("ArrayList<Word> wordList = new ArrayList<>();")
for word_obj in word_objects:
    print(f"wordList.add({word_obj});")
