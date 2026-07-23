import sys
import os
from PyQt5.QtCore import QUrl
from PyQt5.QtWidgets import QApplication, QMainWindow, QPushButton, QVBoxLayout, QWidget
from PyQt5.QtWebEngineWidgets import QWebEngineView # pip install PyQtWebEngine

# 获取项目目录
project_dir = os.path.dirname(os.path.abspath(__file__))
# 指定HTML文件的路径
html_file_path = r'D:\Libraries\Documents\三省堂　スーパー大辞林［第四版］new.html'
# 指定输出文件夹的路径
output_folder = os.path.join(project_dir, 'html资源')

# 将HTML文件按行分割成三份
def split_html_file(html_file_path, output_folder):
    with open(html_file_path, 'r', encoding='utf-8') as file:
        lines = file.readlines()
        total_lines = len(lines)
        part_size = (total_lines + 2) // 3  # 优化：确保最后一部分也能被正确分割

        for i in range(3):
            start = i * part_size
            end = (i + 1) * part_size if i < 2 else total_lines
            part_file_path = os.path.join(output_folder, f'part_{i + 1}.html')
            with open(part_file_path, 'w', encoding='utf-8') as part_file:
                part_file.writelines(lines[start:end])
            print(f"Created {part_file_path}")  # 输出创建的文件信息

class HtmlRenderer(QMainWindow):
    def __init__(self, part_number):
        super().__init__()
        self.part_number = part_number
        self.initUI()

    def initUI(self):
        # 创建一个中心Widget
        central_widget = QWidget()
        self.setCentralWidget(central_widget)

        # 创建布局
        layout = QVBoxLayout()

        # 创建QWebEngineView对象
        self.browser = QWebEngineView()
        layout.addWidget(self.browser)

        # 创建按钮
        self.button = QPushButton("Next Part")
        self.button.clicked.connect(self.load_next_part)
        layout.addWidget(self.button)

        # 应用布局
        central_widget.setLayout(layout)

        # 使用文件路径直接加载本地文件
        self.browser.load(QUrl.fromLocalFile(os.path.join(output_folder, f'part_{self.part_number}.html')))

    def load_next_part(self):
        if self.part_number < 3:
            self.part_number += 1
            self.browser.load(QUrl.fromLocalFile(os.path.join(output_folder, f'part_{self.part_number}.html')))
        else:
            self.close()  # 如果是最后一部分，关闭窗口

if __name__ == '__main__':
    app = QApplication(sys.argv)

    # 检查文件是否存在
    if not os.path.exists(html_file_path):
        print("指定的HTML文件不存在")
        sys.exit(1)

    # 分割HTML文件
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)
    split_html_file(html_file_path, output_folder)

    # 显示第一个部分
    ex = HtmlRenderer(1)
    ex.show()

    sys.exit(app.exec_())