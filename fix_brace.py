with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

code = code.replace("null,1]\\'}}", "null,1]\\'})") # wait, it's inside replace, let's just use replace on the python string

# find the exact string
code = code.replace("gemma-4-31b-it\\\",null,null,0,[4],null,null,1]\\'}}", "gemma-4-31b-it\\\",null,null,0,[4],null,null,1]\\'})") # wait, no, the closing quote of the replace is what it should be.

