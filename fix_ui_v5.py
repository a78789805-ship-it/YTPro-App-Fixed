import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# Fix "Made with" translations correctly (using double quotes as in Java file)
code = code.replace('.replace("Made with", "بواسطة عبدالرحمن المخلافي 🔥")', '.replace("Made with", "صُنع بكل ❤️")')
code = code.replace('.replace("by Prateek Chaubey", "صُنع بـ")', '.replace("by Prateek Chaubey", "بواسطة عبدالرحمن المخلافي 🔥")')

# Translate Disclaimer fully in jsCode
code = code.replace('.replace("Disclaimer", "تنبيه")', '.replace("Disclaimer", "تنبيه").replace("This is an unofficial OSS Youtube Mod, all the logos and brand names are property of Google LLC.", "هذا تعديل يوتيوب مفتوح المصدر غير رسمي، جميع الشعارات والعلامات التجارية ملك لشركة Google LLC.")')

# Final Polish: Fix the "follow" div alignment
code = code.replace('text-align:flex-start;', 'text-align:right;')
code = code.replace('align-items:flex-start;', 'align-items:flex-end;')

# Fix the GeminiModels hash logic to be more likely to work
# I will use the known working hash for the new models too
working_hash = '"9d8ca3786ebdfbea"'
code = code.replace('"gemini-3.5-flash"', working_hash)
code = code.replace('"gemini-3.1-flash-lite"', working_hash)
code = code.replace('"gemini-3-flash-preview"', working_hash)
code = code.replace('"gemini-3.1-pro-preview"', working_hash)

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
