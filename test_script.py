with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

import re
print("translateX(-20px) count:", code.count("translateX(-20px)"))
print("translateX(20px) count:", code.count("translateX(20px)"))
print("translateX(0px) count:", code.count("translateX(0px)"))
print("translateX count:", code.count("translateX"))
