import sys

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# 1. Change name
code = code.replace('.replace("by Prateek Chaubey", "بواسطة Prateek Chaubey")', '.replace("by Prateek Chaubey", "عبدالرحمن المخلافي 🔥")')

# 2. Add empty github link replacement
target = '.replace("Disclaimer", "تنبيه")'
replacement = '.replace("You can find the source code at <a href=\\"https://www.youtube.com/redirect?q=https://github.com/prateek-chaubey/YTPRO\\" style=\\"font-family:monospace;\\" > https://github.com/prateek-chaubey/YTPRO</a>", "").replace("Disclaimer", "تنبيه")'
code = code.replace(target, replacement)

# 3. Change left:2px to right:2px in CSS
css_old = '#ssprodivI div span b{\\nheight:22px;\\nwidth:22px;\\nbackground:#fff;\\nborder-radius:50%;\\nposition:absolute;\\nleft:2px;\\ntop:2px;\\ntransition:transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1), background 0.3s;\\nbox-shadow:0 1px 3px rgba(0,0,0,0.3);\\n}'
css_new = '#ssprodivI div span b{\\nheight:22px;\\nwidth:22px;\\nbackground:#fff;\\nborder-radius:50%;\\nposition:absolute;\\nright:2px;\\ntop:2px;\\ntransition:transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1), background 0.3s;\\nbox-shadow:0 1px 3px rgba(0,0,0,0.3);\\n}'
code = code.replace(css_old, css_new)

# 4. Change translateX(20px) to translateX(-20px)
code = code.replace('.replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.transform=\\"translateX(20px)\\"")', '.replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.transform=\\"translateX(-20px)\\"")')
code = code.replace('.replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};transform:translateX(20px);`:`background:${a[2]};`")', '.replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};transform:translateX(-20px);`:`background:${a[2]};`")')

# 5. Remove duplicated lines 178-179
code = code.replace('\t\t\t\t\t\t\t.replace("Please follow Habitius on Instagram", "تواصل مع المطور (WhatsApp)")\n', '')
code = code.replace('\t\t\t\t\t\t\t.replace("For daily habit,lifestyle and health tips", "لأي استفسار أو دعم فني تواصل معنا");\n', ';\n')

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
