import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

working_hash = '\\"9d8ca3786ebdfbea\\"'
code = code.replace('\\"gemini-3.5-flash\\"', working_hash)
code = code.replace('\\"gemini-3.1-flash-lite\\"', working_hash)
code = code.replace('\\"gemini-3-flash-preview\\"', working_hash)
code = code.replace('\\"gemini-3.1-pro-preview\\"', working_hash)
code = code.replace('\\"gemini-3.1-flash-lite-image\\"', working_hash)
code = code.replace('\\"gemma-4-31b-it\\"', working_hash)

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
