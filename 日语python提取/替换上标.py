import os
import re

# 定义替换规则
replacements = {
    '0': '⁰', '1': '¹', '2': '²', '3': '³', '4': '⁴',
    '5': '⁵', '6': '⁶', '7': '⁷', '8': '⁸', '9': '⁹',
    '＋': '⁺', '-': '⁻', '－': '⁻', '=': '⁼', '(': '⁽', ')': '⁾',
    'a': 'ᵃ', 'b': 'ᵇ', 'c': 'ᶜ', 'd': 'ᵈ', 'e': 'ᵉ', 'f': 'ᶠ',
    'g': 'ᵍ', 'h': 'ʰ', 'i': 'ⁱ', 'j': 'ʲ', 'k': 'ᵏ', 'l': 'ˡ',
    'm': 'ᵐ', 'n': 'ⁿ', 'o': 'ᵒ', 'p': 'ᵖ', 'q': 'ᑫ', 'r': 'ʳ',
    's': 'ˢ', 't': 'ᵗ', 'u': 'ᵘ', 'v': 'ᵛ', 'w': 'ʷ', 'x': 'ˣ',
    'y': 'ʸ', 'z': 'ᶻ',
    'A': 'ᴬ', 'B': 'ᴮ', 'C': 'ᶜ', 'D': 'ᴰ', 'E': 'ᴱ', 'F': 'ᶠ',
    'G': 'ᴳ', 'H': 'ᴴ', 'I': 'ᴵ', 'J': 'ᴶ', 'K': 'ᴷ', 'L': 'ᴸ',
    'M': 'ᴹ', 'N': 'ᴺ', 'O': 'ᵒ', 'P': 'ᵖ', 'Q': 'ᵠ', 'R': 'ᴿ',
    'S': 'ˢ', 'T': 'ᵀ', 'U': 'ᵁ', 'V': 'ⱽ', 'W': 'ᵂ', 'X': 'ˣ',
    'Y': 'ʸ', 'Z': 'ᶻ',

    # 可以根据需要添加更多的替换规则
}


# 读取文件
def read_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        return file.read()


# 写入文件
def write_file(file_path, content):
    with open(file_path, 'w', encoding='utf-8') as file:
        file.write(content)


# 替换上标字符
def replace_superscript(text_content):
    # 使用正则表达式查找所有span标签内的内容
    pattern = r'span data-name="sub">(.*?)<'
    spans = re.findall(pattern, text_content)

    for span_text in spans:
        original_span = 'span data-name="sub">{}<'.format(span_text)
        new_span = 'span data-name="sub">{}<'.format(''.join(replacements.get(char, char) for char in span_text))
        text_content = text_content.replace(original_span, new_span)
        for key, value in replacements.items():
            if key in span_text:
                print(f"把原来的字符 '{key}' 改成了 '{value}'")
    return text_content


# 主函数
def main():
    original_file_path = r'D:\Desktop\三省堂　スーパー大辞林［第四版］.txt'
    new_file_path = r'D:\Desktop\三省堂　スーパー大辞林［第四版］new.txt'

    # 读取原始文件内容
    original_content = read_file(original_file_path)

    # 替换上标字符
    new_content = replace_superscript(original_content)

    # 保存到新文件
    write_file(new_file_path, new_content)
    print(f'文件已保存到：{new_file_path}')


if __name__ == '__main__':
    main()
