import re
with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

idx1 = code.find('.replace("GeminiModels=')
print(code[idx1-100:idx1+100])
