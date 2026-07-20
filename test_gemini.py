import re

with open('ytpro_fresh.js', 'r', encoding='utf-8') as f:
    js_code = f.read()

match = re.search(r'GeminiModels=({.*?})', js_code)
if match:
    print(match.group(1))
