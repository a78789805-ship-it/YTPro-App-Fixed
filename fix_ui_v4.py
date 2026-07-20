import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# Fix buttons alignment
code = code.replace('text-align:left;\\n}', 'text-align:right;\\n}')
code = code.replace('float:right;', 'float:left;')

# Fix the disclaimer alignment
code = code.replace('text-align:left"><b style="font-weight:bold">Disclaimer</b>', 'text-align:right"><b style="font-weight:bold">تنبيه</b>')

# Improve the "Done" and "Save" buttons alignment
code = code.replace('float:right;text-align:center;', 'float:left;text-align:center;')

# Ensure direction: rtl on sub-menus
code = code.replace('id=\\"ssprodivI\\",e.setAttribute(\\"style\\",\\"\\\\nheight:100%;width:100%;position:fixed;top:0;left:0;\\\\ndisplay:flex;justify-content:center;\\\\nbackground:rgba(0,0,0,0.7);\\\\nz-index:9999;\\\\ndirection:rtl;\\\\n\\")',
                    'id=\\"ssprodivI\\",e.setAttribute(\\"style\\",\\"\\\\nheight:100%;width:100%;position:fixed;top:0;left:0;\\\\ndisplay:flex;justify-content:center;\\\\nbackground:rgba(0,0,0,0.7);\\\\nz-index:9999;\\\\ndirection:rtl;\\\\ntext-align:right;\\\\n\\")')

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
