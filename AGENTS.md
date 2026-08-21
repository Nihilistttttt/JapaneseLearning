# LearnWord 项目笔记

## 项目概述

日语单词学习App，21.7万词数据库，三种学习模式（初学/复习/最后一关），间隔重复(SRS)算法。
技术栈：Java 8 + MVVM + Room + ViewPager2

## 构建步骤

### 环境要求

- Android Studio（含JBR）
- Gradle 8.11.1
- AGP 8.7.3
- Java 8（系统JAVA_HOME）+ JBR 17（构建用）

### 构建命令

系统 JAVA_HOME 指向 Java 8，会导致 AGP 8.7.3 失败。必须用 Android Studio 的 JBR：

```powershell
$env:JAVA_HOME = "D:\InstallData\ProgrammingTools\AndroidStudio\AndroidStudio\jbr"
& "C:\Users\hp\.gradle\wrapper\dists\gradle-8.11.1-bin\bpt9gzteqjrbo1mjrsomdt32c\gradle-8.11.1\bin\gradle.bat" assembleDebug -p "D:\Libraries\Projects\AndroidStudioProjects\Learn"
```

### 安装到设备

```powershell
adb install -r "D:\Libraries\Projects\AndroidStudioProjects\Learn\app\build\outputs\apk\debug\app-debug.apk"
```

### 查看SRS调试日志

```powershell
adb logcat -s SRS
```

## 数据库相关

### 数据库文件

- 预建数据库：`app/src/main/assets/databases/word_database.db`
- Room version：5（@Database注解）
- PREBUILT_DB_VERSION：23（改这个会触发设备上数据库重建）
- room_master_table identity hash：`3c56aa50799eeb20c068834becc5bfde`

### 修改预建数据库后必须做的事

1. 用Python直接修改 `word_database.db`
2. 确保 `room_master_table` 的 identity hash 与 Room 期望的一致（否则启动崩溃）
3. 确保 `user_version = 0`（让Room自行处理版本）
4. 增加 `WordDatabase.PREBUILT_DB_VERSION`（触发设备上数据库删除重建）
5. 增加 `PresetBookInitializer.CURRENT_VERSION`（触发词书重新生成）

### JLPT等级分配

脚本：`日语python提取/assign_jlpt_levels.py`
数据源：`JLPT_vocab_ALL.json`（来自 https://github.com/Bluskyo/JLPT_Vocabulary）
- 34181词匹配真实JLPT等级，其余随机分配
- N5约2万 / N4约3万 / N3约5.4万 / N2约5万 / N1约6.3万

## 应用图标

脚本：`日语python提取/generate_app_icon.py`
- 背景：深蓝色(#1A237E)
- 前景：白色"語"字 + 黄色装饰条
- 生成PNG到各mipmap目录 + foreground bitmap到drawable

## SRS学习逻辑

### 队列机制

- `buildInterleavedQueue`：每词3条目(NEW/REVIEW/FINAL)，多词随机混杂
- 阶段由 `correctCount` 动态决定：0→NEW(四选一), 1→REVIEW, 2→FINAL, ≥3→完成
- `advanceToNext`/`submitFail`：当 `count < 3 且队列无该词未处理条目` 时重新插入

### 关键文件

- `Page/ViewModel/LearnPageViewModel.java` — SRS核心逻辑
- `Fragment/LearnPage/LearnPageFragment.java` — SRS按钮
- `Fragment/LearnPage/MainLearnPage/MainLearnPageFragment.java` — 四选一/复习UI
- `Algorithm/StudyQueueEntry.java` — 队列条目
- `Algorithm/StudyStage.java` — 学习阶段枚举

## 注意事项

- 不用管已安装用户，还在前期开发，选择最好的方案
- 禁止未经允许commit或push
- 主要参考不背单词