import re
with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

idx = code.find('replace("GeminiModels=')
if idx != -1:
    print(code[idx:idx+500])
