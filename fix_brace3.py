with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

code = code.replace('gemma-4-31b-it\\",null,null,0,[4],null,null,1]\\\'}}")', 'gemma-4-31b-it\\",null,null,0,[4],null,null,1]\\\'}")')

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
