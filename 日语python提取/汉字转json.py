import pandas as pd
import json
import os

# 定义保存JSON文件的路径
json_file_path = 'D:\\Libraries\\Documents\\日语数据处理\\words.json'


# 读取Excel文件
file_path = 'D:\\Libraries\\Documents\\日语数据处理\\常用日语汉字表 - 副本.xlsx'
df = pd.read_excel(file_path)

# 创建Word对象的列表
words = []



for index, row in df.iterrows():
    word = {
        'kanji': row.iloc[0],
        'kana': row.iloc[1] if pd.notnull(row.iloc[1]) else "" ,
        # 'id': f'{id_counter:04d}'  # 格式化id为4位数
    }
    words.append(word)

# 将Word对象列表序列化为JSON字符串
json_data = json.dumps(words, ensure_ascii=False, indent=4)

# 将JSON字符串写入文件
with open(json_file_path, 'w', encoding='utf-8') as f:
    f.write(json_data)