import re
with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# find the string that starts with GeminiModels=
match = re.search(r'GeminiModels=({.*?})', code)
if match:
    print("Found GeminiModels:")
    print(match.group(0)[:100] + "...")
else:
    print("Could not find GeminiModels!")
