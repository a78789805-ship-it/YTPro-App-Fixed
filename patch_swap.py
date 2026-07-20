import sys

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

code = code.replace('.replace("Made with", "صُنع")', '.replace("Made with", "بواسطة عبدالرحمن المخلافي 🔥")')
code = code.replace('.replace("by Prateek Chaubey", "بواسطة عبدالرحمن المخلافي 🔥")', '.replace("by Prateek Chaubey", "صُنع بـ")')

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
