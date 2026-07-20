import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# 1. CSS Clean Overhaul - Use a single block replacement for all CSS to avoid fragmentation
# I will find the start of the CSS replacement block and replace it with a clean one.

css_replacements = """
                            // Theme & Logic RTL Fix
                            .replace("if(isD)var a=[\\"#000\\",\\"#717171\\",\\"#fff\\"];else a=[\\"#fff\\",\\"#909090\\",\\"#151515\\"];", "if(isD)var a=[\\"#fff\\",\\"#555\\",\\"#34C759\\"];else a=[\\"#fff\\",\\"#ccc\\",\\"#34C759\\"];")
                            .replace("direction:rtl;", "direction:rtl;text-align:right;")
                            // Optimized Row Style - Fixed Overlap
                            .replace("#ssprodivI div{\\\\nheight:10px;\\\\nwidth:calc(100% - 20px);\\\\npadding:10px;\\\\nfont-size:1.45rem;\\\\ntext-align:left;\\\\ndisplay:flex;\\\\nalign-items:center;\\\\nposition:relative;\\\\nmargin-top:3px;\\\\n}", "#ssprodivI div{display:flex !important;justify-content:space-between !important;align-items:center !important;min-height:56px !important;width:calc(100% - 20px) !important;padding:8px 15px !important;font-size:1.1rem !important;text-align:right !important;position:relative !important;margin-top:5px !important;border-bottom:1px solid rgba(128,128,128,0.1) !important;box-sizing:border-box !important;}")
                            // Optimized Toggle Style
                            .replace("#ssprodivI div span{\\\\ndisplay:block;\\\\nheight:23px;\\\\nwidth:40px;\\\\nborder-radius:40px;\\\\nright:10px;\\\\nposition:absolute;\\\\nbackground:#151515;\\\\n}", "#ssprodivI div span{display:inline-flex !important;position:relative !important;height:26px !important;width:48px !important;border-radius:26px !important;background:#ccc !important;transition:all 0.3s ease !important;cursor:pointer !important;flex-shrink:0 !important;margin-left:10px !important;}")
                            .replace("#ssprodivI div span b{\\\\ndisplay:block;\\\\nheight:19px;\\\\nwidth:19px;\\\\nposition:absolute;\\\\nright:2px;\\\\ntop:2px;\\\\nborder-radius:50px;\\\\nbackground:#fff;\\\\n}", "#ssprodivI div span b{height:22px !important;width:22px !important;background:#fff !important;border-radius:50% !important;position:absolute !important;left:2px !important;top:2px !important;transition:all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;box-shadow:0 2px 5px rgba(0,0,0,0.3) !important;}")
                            // Logic Fix for Toggles in RTL
                            .replace("left:2px;", "right:2px;left:auto;")
                            .replace("e.children[0].style.left=\\"2px\\"", "e.children[0].style.right=\\"2px\\",e.children[0].style.left=\\"auto\\"")
                            .replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.left=\\"2px\\",e.children[0].style.right=\\"auto\\"")
                            .replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};left:2px;right:auto;`:`background:${a[2]};` ")
"""

# Apply the new CSS block
# We need to find the part between Theme Colors and Save
pattern = re.escape('// Theme Colors') + r'.*?' + re.escape('.replace("Save", "حفظ")')
new_block = '// Theme Colors' + css_replacements + '                            .replace("Save", "حفظ")'
code = re.sub(pattern, new_block, code, flags=re.DOTALL)

# Fix Footer and Disclaimer
code = code.replace('.replace("Disclaimer", "تنبيه").replace("This is an unofficial OSS Youtube Mod, all the logos and brand names are property of Google LLC.", "هذا تعديل يوتيوب مفتوح المصدر غير رسمي، جميع الشعارات والعلامات التجارية ملك لشركة Google LLC.")', 
                    '.replace("Disclaimer", "تنبيه").replace("This is an unofficial OSS Youtube Mod, all the logos and brand names are property of Google LLC.", "<div style=\'direction:rtl;text-align:right;font-size:0.9rem;opacity:0.8;\'>هذا تعديل يوتيوب مفتوح المصدر غير رسمي، جميع الشعارات والعلامات التجارية ملك لشركة Google LLC.</div>")')

# Final check for GeminiModels - Ensure it is exactly as expected
working_hash = '\\"9d8ca3786ebdfbea\\"'
# Ensure all our new models have the working hash payload
code = re.sub(r'\"Gemini (.*?)\":\'\[1,null,null,null,\\"9d8ca3786ebdfbea\\"', r'"Gemini \1":\'[1,null,null,null,' + working_hash, code)

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
