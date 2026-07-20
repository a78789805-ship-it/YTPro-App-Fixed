with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'r', encoding='utf-8') as f:
    code = f.read()

rtl_fixes = """
                            .replace("left:2px;", "right:2px;left:auto;")
                            .replace("e.children[0].style.left=\\"2px\\"", "e.children[0].style.right=\\"2px\\";e.children[0].style.left=\\"auto\\"")
                            .replace("e.children[0].style.left=\\"auto\\",e.children[0].style.right=\\"2px\\"", "e.children[0].style.left=\\"2px\\";e.children[0].style.right=\\"auto\\"")
                            .replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};`:`background:${a[2]};left:2px;right:auto;`")"""

code = code.replace('.replace("#ssprodivI div{\\nheight:10px;\\nwidth:calc(100% - 20px);\\npadding:10px;\\nfont-size:1.45rem;\\ntext-align:left;\\ndisplay:flex;\\nalign-items:center;\\nposition:relative;\\nmargin-top:3px;\\n}", "#ssprodivI div{\\nmin-height:20px;\\nwidth:calc(100% - 20px);\\npadding:10px;\\nfont-size:1.45rem;\\ntext-align:left;\\ndisplay:flex;\\njustify-content:space-between;\\nalign-items:center;\\nposition:relative;\\nmargin-top:3px;\\ngap:15px;\\n}")', 
'.replace("#ssprodivI div{\\nheight:10px;\\nwidth:calc(100% - 20px);\\npadding:10px;\\nfont-size:1.45rem;\\ntext-align:left;\\ndisplay:flex;\\nalign-items:center;\\nposition:relative;\\nmargin-top:3px;\\n}", "#ssprodivI div{\\nmin-height:20px;\\nwidth:calc(100% - 20px);\\npadding:10px;\\nfont-size:1.45rem;\\ntext-align:left;\\ndisplay:flex;\\njustify-content:space-between;\\nalign-items:center;\\nposition:relative;\\nmargin-top:3px;\\ngap:15px;\\n}")' + rtl_fixes)

with open('app/src/main/java/com/google/android/youtube/pro/webview/YTProWebViewClient.java', 'w', encoding='utf-8') as f:
    f.write(code)
