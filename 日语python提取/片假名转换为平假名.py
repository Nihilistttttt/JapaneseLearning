import pandas as pd

# 定义一个函数，将片假名转换为平假名
def katakana_to_hiragana(text):
    if isinstance(text, str):
        return ''.join(chr(ord(c) - 0x60) if 0x30A0 <= ord(c) <= 0x30FF else c for c in text)
    return text

# 读取Excel文件
file_path = 'D:\\Libraries\Documents\\666666.xlsx'
df = pd.read_excel(file_path)

# 打印列名以确认
print("列名：", df.columns)

# 检查是否存在列 'E'
if 'D' in df.columns:
    # 将列 'E' 中的片假名转换为平假名
    # df_1['B'] = df_1['B'].apply(katakana_to_hiragana)
    # df_1['C'] = df_1['C'].apply(katakana_to_hiragana)
    df['D'] = df['D'].apply(katakana_to_hiragana)
    df['E'] = df['E'].apply(katakana_to_hiragana)
    # 保存更改到新的Excel文件
    df.to_excel('D:\\Libraries\\Documents\\666668888.xlsx', index=False)
    print("转换完成，并已保存到新文件。")
else:
    print("列 'E' 不存在，请检查列名。")