import gzip, json

path = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\room_import.json.gz'
try:
    with gzip.open(path, 'rt', encoding='utf-8') as f:
        data = json.load(f)
    print('Valid JSON! Keys:', list(data.keys()))
    print('Words:', len(data['words']))
    print('WordSentences:', len(data.get('wordSentences', [])))
    
    has_s = sum(1 for w in data['words'] if json.loads(w['sentenceIdList']))
    print('Words with sentences:', has_s)
    
    # 检查antonym kana
    a = data['antonymWords'][:3]
    for x in a:
        print('antonym:', json.loads(x['kanjiComponents']), json.loads(x['kanaComponents']))
    
    # 检查wordSentence样例
    if data.get('wordSentences'):
        s = data['wordSentences'][0]
        print('Sentence sample:', s['wordId'], s['translation'][:30])
except Exception as e:
    print('ERROR:', e)