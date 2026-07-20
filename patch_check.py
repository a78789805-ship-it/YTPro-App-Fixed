with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

import re
print(re.findall(r'replace\("e\.children\[0\]\.style\.left=\\"2px\\"", "e\.children\[0\]\.style\.transform=\\"translateX\(0px\)\\""\)', code))
print(re.findall(r'replace\("left:2px;", "transform:translateX\(0px\);"\)', code))
