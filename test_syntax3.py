with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

idx1 = code.find('replace("GeminiModels=')
idx2 = code.find('")', idx1)
print(code[idx1:idx1+1000])
