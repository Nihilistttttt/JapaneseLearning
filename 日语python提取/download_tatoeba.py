import urllib.request
import os
import sys

base = r'D:\Libraries\Projects\AndroidStudioProjects\Learn\日语数据处理'

files = {
    'tatoeba_sentences.tar.bz2': 'https://downloads.tatoeba.org/exports/sentences.tar.bz2',
    'tatoeba_links.tar.bz2': 'https://downloads.tatoeba.org/exports/links.tar.bz2',
    'tatoeba_jpn_indices.tar.bz2': 'https://downloads.tatoeba.org/exports/jpn_indices.tar.bz2',
}

for fname, url in files.items():
    out_path = os.path.join(base, fname)
    if os.path.exists(out_path) and os.path.getsize(out_path) > 1000000:
        print(f'SKIP {fname} ({os.path.getsize(out_path)/1024/1024:.1f} MB)')
        continue
    print(f'Downloading {fname}...')
    try:
        urllib.request.urlretrieve(url, out_path)
        size = os.path.getsize(out_path)
        print(f'  Done: {size/1024/1024:.1f} MB')
    except Exception as e:
        print(f'  ERROR: {e}')

print('All downloads complete')