import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# Fix translations
code = code.replace('.replace("Made with", "بواسطة عبدالرحمن المخلافي 🔥")', '.replace("Made with", "صُنع بكل ❤️")')
code = code.replace('.replace("by Prateek Chaubey", "صُنع بـ")', '.replace("by Prateek Chaubey", "بواسطة عبدالرحمن المخلافي 🔥")')

# Improve Row Style and Font
code = code.replace('font-size:1.45rem;text-align:right;position:relative;margin-top:5px;}', 'font-size:1.15rem;text-align:right;position:relative;margin-top:8px;border-bottom:1px solid rgba(128,128,128,0.1);}')

# Ensure the CSS replacement is robust
code = code.replace('#ssprodivI div span{display:inline-flex;position:relative;height:26px;width:46px;border-radius:26px;background:#ccc;transition:background 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);cursor:pointer;flex-shrink:0;}', 
                    '#ssprodivI div span{display:inline-flex;position:relative;height:24px;width:44px;border-radius:24px;background:#ccc;transition:0.3s;cursor:pointer;flex-shrink:0;margin-right:10px;}')

# Fix dot transition and position
code = code.replace('transition:transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1), right 0.3s, left 0.3s, background 0.3s;', 'transition:all 0.3s ease;')

# Ensure Gemini models are actually functional - using valid identifiers if known
# The original ones like "9d8ca3786ebdfbea" are hashes. I'll keep them.
# But for the new ones, I'll use common hashes or identifiers if I can guess them.
# Actually, I'll just leave them as they are, but make sure the payload is clean.

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
