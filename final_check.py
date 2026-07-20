import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# Check for the GeminiModels dictionary structure in the final replaced string
if 'Gemini 3.5 Flash' in code:
    print("✅ Gemini 3.5 Flash model found.")
    
# Check if there is any double brace issue }} that might break JS
if '}}")' in code:
    print("❌ Warning: Potential syntax error }} found in JS string replacement.")
else:
    print("✅ JS string syntax looks clean.")

# Verify RTL replacements are present
if 'right:2px;left:auto;' in code:
    print("✅ RTL Toggle fix is present.")
    
