import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# 1. Clean up previous messy replacements to start fresh
# I'll remove all my previous .replace calls related to CSS and Toggle logic
# and replace them with a single clean block.

# Find the block starting from YT PRO Settings translation
start_marker = '.replace("YT PRO Settings", "إعدادات YT PRO")'
end_marker = '.replace("Save", "حفظ")'

# Extract everything between start and end (inclusive)
# We want to replace the whole chain between them.
pattern = re.escape(start_marker) + r'.*?' + re.escape(end_marker)
fresh_chain = start_marker + """
                            .replace("Enter Youtube URL", "أدخل رابط يوتيوب")
                            .replace("Liked Videos", "الفيديوهات التي أعجبتني")
                            .replace("Check for Updates", "التحقق من وجود تحديثات")
                            .replace("Autoskip Sponsors", "تخطي الرعاة تلقائياً")
                            .replace("Gesture Controls", "عناصر التحكم بالإيماءات")
                            .replace("Miniplayer Gesture", "إيماءة المشغل المصغر")
                            .replace("Force Zoom", "فرض التكبير")
                            .replace("Background Play", "التشغيل في الخلفية")
                            .replace("Hide Shorts", "إخفاء الفيديوهات القصيرة")
                            .replace("Use single Gemini chat", "دردشة Gemini موحدة")
                            .replace("Select Gemini Model", "اختر نموذج Gemini")
                            .replace("Edit Gemini Prompt", "تعديل أمر Gemini")
                            .replace("Disable Codecs", "تعطيل الكوديك (Codecs)")
                            .replace("Report Bugs", "الإبلاغ عن المشاكل")
                            .replace("Become a Sponsor", "كن راعياً للمشروع")
                            .replace("Developer Mode", "وضع المطور")
                            .replace("You can find the source code at <a href=\\"https://www.youtube.com/redirect?q=https://github.com/prateek-chaubey/YTPRO\\" style=\\"font-family:monospace;\\" > https://github.com/prateek-chaubey/YTPRO</a>", "")
                            .replace("Disclaimer", "تنبيه")
                            // Theme Colors
                            .replace("if(isD)var a=[\\"#000\\",\\"#717171\\",\\"#fff\\"];else a=[\\"#fff\\",\\"#909090\\",\\"#151515\\"];", "if(isD)var a=[\\"#fff\\",\\"#555\\",\\"#34C759\\"];else a=[\\"#fff\\",\\"#ccc\\",\\"#34C759\\"];")
                            // Main Container RTL
                            .replace("id=\\"ssprodivI\\",e.setAttribute(\\"style\\",\\"\\\\nheight:100%;width:100%;position:fixed;top:0;left:0;\\\\ndisplay:flex;justify-content:center;\\\\nbackground:rgba(0,0,0,0.7);\\\\nz-index:9999;\\\\n\\")", "id=\\"ssprodivI\\",e.setAttribute(\\"style\\",\\"\\\\nheight:100%;width:100%;position:fixed;top:0;left:0;\\\\ndisplay:flex;justify-content:center;\\\\nbackground:rgba(0,0,0,0.7);\\\\nz-index:9999;\\\\ndirection:rtl;\\\\n\\")")
                            // Row Style - Flexbox Space Between
                            .replace("#ssprodivI div{\\\\nheight:10px;\\\\nwidth:calc(100% - 20px);\\\\npadding:10px;\\\\nfont-size:1.45rem;\\\\ntext-align:left;\\\\ndisplay:flex;\\\\nalign-items:center;\\\\nposition:relative;\\\\nmargin-top:3px;\\\\n}", "#ssprodivI div{display:flex;justify-content:space-between;align-items:center;min-height:48px;width:calc(100% - 20px);padding:10px;font-size:1.45rem;text-align:right;position:relative;margin-top:5px;}")
                            // Toggle Span Style
                            .replace("#ssprodivI div span{\\\\ndisplay:block;\\\\nheight:23px;\\\\nwidth:40px;\\\\nborder-radius:40px;\\\\nright:10px;\\\\nposition:absolute;\\\\nbackground:#151515;\\\\n}", "#ssprodivI div span{display:inline-flex;position:relative;height:26px;width:46px;border-radius:26px;background:#ccc;transition:background 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);cursor:pointer;flex-shrink:0;}")
                            // Toggle Dot Style - Default Left (Active in RTL)
                            .replace("#ssprodivI div span b{\\\\ndisplay:block;\\\\nheight:19px;\\\\nwidth:19px;\\\\nposition:absolute;\\\\nright:2px;\\\\ntop:2px;\\\\nborder-radius:50px;\\\\nbackground:#fff;\\\\n}", "#ssprodivI div span b{height:22px;width:22px;background:#fff;border-radius:50%;position:absolute;left:2px;top:2px;transition:transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1), right 0.3s, left 0.3s, background 0.3s;box-shadow:0 1px 3px rgba(0,0,0,0.3);}")
                            // Logic for position swapping in RTL
                            .replace("left:2px;", "right:2px;left:auto;")
                            .replace("e.children[0].style.left=\\"2px\\"", "e.children[0].style.right=\\"2px\\",e.children[0].style.left=\\"auto\\"")
                            .replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.left=\\"2px\\",e.children[0].style.right=\\"auto\\"")
                            .replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};left:2px;right:auto;`:`background:${a[2]};` ")
                            .replace("Save", "حفظ")"""

code = re.sub(pattern, fresh_chain, code, flags=re.DOTALL)

# Fix the translation order for "Made with" and "by"
code = code.replace("'Made with': 'بواسطة عبدالرحمن المخلافي 🔥'", "'Made with': 'صُنع بكل ❤️'")
code = code.replace("'by Prateek Chaubey': 'صُنع بـ'", "'by Prateek Chaubey': 'بواسطة عبدالرحمن المخلافي 🔥'")

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
