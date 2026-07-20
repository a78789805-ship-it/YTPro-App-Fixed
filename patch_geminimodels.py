import re

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

# Remove the broken replacement
broken_replace = re.search(r'\n                            \.replace\("GeminiModels=.*?"\)', code, re.DOTALL)
if broken_replace:
    code = code.replace(broken_replace.group(0), '')

old_gemini_actual = 'GeminiModels={"3.0 Pro":\'[1,null,null,null,"9d8ca3786ebdfbea",null,null,0,[4],null,null,1]\',"3.0 Flash":\'[1,null,null,null,"fbb127bbb056c959",null,null,0,[4],null,null,1]\',"3.0 Flash Thinking":\'[1,null,null,null,"5bf011840784117a",null,null,0,[4],null,null,1]\',"3.0 Pro Plus":\'[1,null,null,null,"e6fa609c3fa255c0",null,null,0,[4],null,null,4]\',"3.0 Flash Plus":\'[1,null,null,null,"56fdd199312815e2",null,null,0,[4],null,null,4]\',"3.0 Flash Thinking Plus":\'[1,null,null,null,"e051ce1aa80aa576",null,null,0,[4],null,null,4]\',"3.0 Pro Advanced":\'[1,null,null,null,"e6fa609c3fa255c0",null,null,0,[4],null,null,2]\',"3.0 Flash Advanced":\'[1,null,null,null,"56fdd199312815e2",null,null,0,[4],null,null,2]\',"3.0 Flash Thinking Advanced":\'[1,null,null,null,"e051ce1aa80aa576",null,null,0,[4],null,null,2]\'}'

new_gemini_models = ',"Gemini 3.5 Flash":\'[1,null,null,null,"gemini-3.5-flash",null,null,0,[4],null,null,1]\',"Gemini 3.1 Flash Lite":\'[1,null,null,null,"gemini-3.1-flash-lite",null,null,0,[4],null,null,1]\',"Gemini 3.0 Flash Preview":\'[1,null,null,null,"gemini-3-flash-preview",null,null,0,[4],null,null,1]\',"Gemini 3.1 Pro Preview":\'[1,null,null,null,"gemini-3.1-pro-preview",null,null,0,[4],null,null,1]\',"Gemini 3.1 Flash Lite Image":\'[1,null,null,null,"gemini-3.1-flash-lite-image",null,null,0,[4],null,null,1]\',"Gemma 4 31B IT":\'[1,null,null,null,"gemma-4-31b-it",null,null,0,[4],null,null,1]\'}'

# Escape for Java string literal
# Replace \ with \\, " with \", wait, there are no \ initially.
# " -> \"
# ' -> ' (doesn't need escaping in java double quote string, but we can leave it as is, or use it directly)
def escape_java(s):
    return s.replace('"', '\\"').replace("'", "\\'")

escaped_old = escape_java(old_gemini_actual)
escaped_old_no_brace = escape_java(old_gemini_actual[:-1])
escaped_new = escape_java(new_gemini_models)

replacement = '.replace("' + escaped_old + '", "' + escaped_old_no_brace + escaped_new + '}")'

search_str = '.replace("Done", "تم")'
code = code.replace(search_str, search_str + '\n                            ' + replacement)

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
