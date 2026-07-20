import sys

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# 1. Toggle Button RTL Fix
css_old_left = '#ssprodivI div span b{\\nheight:22px;\\nwidth:22px;\\nbackground:#fff;\\nborder-radius:50%;\\nposition:absolute;\\nleft:2px;\\ntop:2px;\\ntransition:transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1), background 0.3s;\\nbox-shadow:0 1px 3px rgba(0,0,0,0.3);\\n}'
css_new_right = '#ssprodivI div span b{\\nheight:22px;\\nwidth:22px;\\nbackground:#fff;\\nborder-radius:50%;\\nposition:absolute;\\nright:2px;\\ntop:2px;\\ntransition:transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1), background 0.3s;\\nbox-shadow:0 1px 3px rgba(0,0,0,0.3);\\n}'
code = code.replace(css_old_left, css_new_right)

# 2. JS replacements for toggle translation
# Inactive -> right: 2px (transform: translateX(0px))
# Active -> left: 2px (transform: translateX(-20px))

# old: .replace("left:2px;", "transform:translateX(0px);") -> inactive
# old: .replace("e.children[0].style.left=\"2px\"", "e.children[0].style.transform=\"translateX(0px)\"") -> setting to inactive
# old: .replace("e.children[0].style.left=\"auto\",e.children[0].style.right=\"2px\"", "e.children[0].style.transform=\"translateX(20px)\"") -> setting to active
# old: .replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};transform:translateX(20px);`:`background:${a[2]};`") -> active init

code = code.replace('.replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.transform=\\"translateX(20px)\\"")', '.replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.transform=\\"translateX(-20px)\\"")')
code = code.replace('.replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};transform:translateX(20px);`:`background:${a[2]};`")', '.replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};transform:translateX(-20px);`:`background:${a[2]};`")')


# 3. Fix translations for "Made with" and "by Prateek Chaubey"
# The jsCode string replacement was:
# .replace("Made with", "بواسطة عبدالرحمن المخلافي 🔥")
# .replace("by Prateek Chaubey", "صُنع بـ")
# We also need to fix it in the JS translation dictionary at line 233!
code = code.replace('"    \'Made with\': \'صُنع بـ\', " +', '"    \'Made with\': \'بواسطة عبدالرحمن المخلافي 🔥\', " +')
code = code.replace('"    \'by Prateek Chaubey\': \'بواسطة Prateek Chaubey\', " +', '"    \'by Prateek Chaubey\': \'صُنع بـ\', " +')
code = code.replace('"    \'by Prateek Chaubey\': \'صُنع بـ\', " +', '"    \'by Prateek Chaubey\': \'صُنع بـ\', " +') # in case I messed up earlier

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
