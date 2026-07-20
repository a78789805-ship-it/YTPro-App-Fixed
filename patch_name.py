import sys

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

code = code.replace('.replace("Made with", "صُنع بـ")', '.replace("Made with", "صُنع")')
code = code.replace('.replace("by Prateek Chaubey", "عبدالرحمن المخلافي 🔥")', '.replace("by Prateek Chaubey", "بواسطة عبدالرحمن المخلافي 🔥")')

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
