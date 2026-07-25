import sqlite3
path = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\app\src\main\assets\databases\word_database.db'
conn = sqlite3.connect(path)
cursor = conn.cursor()
for t in ['Word','BasicWord','WordMeaning','WordSentence','AntonymWord','SynonymWord','WordCollocation']:
    cursor.execute(f'SELECT COUNT(*) FROM {t}')
    print(f'{t}: {cursor.fetchone()[0]}')
cursor.execute('SELECT identity_hash FROM room_master_table WHERE id=42')
r = cursor.fetchone()
print(f'identity_hash: {r}')
conn.close()