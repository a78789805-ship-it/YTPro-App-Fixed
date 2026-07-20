import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

code = code.replace('.replace("e.children[0].style.left=\\"2px\\"", "e.children[0].style.right=\\"2px\\";e.children[0].style.left=\\"auto\\"")', '.replace("e.children[0].style.left=\\"2px\\"", "e.children[0].style.right=\\"2px\\",e.children[0].style.left=\\"auto\\"")')

code = code.replace('.replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.left=\\"2px\\";e.children[0].style.right=\\"auto\\"")', '.replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.left=\\"2px\\",e.children[0].style.right=\\"auto\\"")')

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
