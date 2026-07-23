import re

# 定义输入和输出文件路径
input_file_path = r"D:\Desktop\111.txt"
output_file_path = r"D:\Desktop\222.txt"

# 读取输入文件
try:
    with open(input_file_path, 'r', encoding='utf-8') as file:
        data = file.read()
except FileNotFoundError:
    print(f"文件 {input_file_path} 未找到。")
    exit()

# 正则表达式模式
pattern1 = r"</>\n(.*?)\n@@@LINK=@daijirin2-\d+-0000\n"
pattern2 = r"</>\n@daijirin2-\d+-0000\n(.*?)</>\n"

# 找到所有匹配项
matches1 = re.findall(pattern1, data, re.DOTALL)
matches2 = re.findall(pattern2, data, re.DOTALL)

# 检查是否找到匹配项
if not matches1 or not matches2:
    print("没有找到匹配项，请检查正则表达式和输入文件的格式。")
else:
    # 处理匹配项并生成结果
    results = []
    for title, link in matches1:
        result = f"</>\n{title}\n@@@LINK=@{link}\n"
        results.append(result)

    for content in matches2:
        result = f"{content}\n</>"
        results.append(result)

    # 将结果写入输出文件
    try:
        with open(output_file_path, 'w', encoding='utf-8') as file:
            file.write('\n'.join(results))
        print("数据处理完成，结果已保存")
    except Exception as e:
        print(f"写入文件时发生错误：{e}")