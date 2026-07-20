package com.google.android.youtube.pro.webview;

import android.content.Intent;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.youtube.pro.ForegroundService;
import com.google.android.youtube.pro.MainActivity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import android.util.Log;

public class YTProWebViewClient extends WebViewClient {
	
	private final MainActivity activity;
	private final YTProWebView web;
	
	public YTProWebViewClient(MainActivity activity, YTProWebView web) {
		this.activity = activity;
		this.web = web;
	}
	
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
		String url = request.getUrl().toString();
		
		if (url.contains("accounts.google.com") || url.contains("ssl.gstatic.com") || url.contains("play.google.com")) {
			return super.shouldInterceptRequest(view, request);
		}

		if (request.isForMainFrame() && (url.contains("m.youtube.com") || url.contains("www.youtube.com")) && !url.contains("/signin") && !url.contains("/account") && !url.contains("accounts.google.com")) {
			try {
				URL newUrl = new URL(url);
				HttpsURLConnection connection = (HttpsURLConnection) newUrl.openConnection();
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod(request.getMethod());
				
				for (Map.Entry<String, String> header : request.getRequestHeaders().entrySet()) {
					if (!header.getKey().equalsIgnoreCase("Accept-Encoding")) {
						connection.setRequestProperty(header.getKey(), header.getValue());
					}
				}
				
				String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
				if (cookies != null) connection.setRequestProperty("Cookie", cookies);
				
				connection.connect();
				
				Map<String, String> safeHeaders = new HashMap<>();
				for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
					if (entry.getKey() != null) {
						String headerName = entry.getKey().toLowerCase();
						if (headerName.equals("set-cookie")) {
							for (String cookie : entry.getValue()) {
								android.webkit.CookieManager.getInstance().setCookie(url, cookie);
							}
						}
						if (!headerName.equals("content-security-policy") && !headerName.equals("content-security-policy-report-only")) {
							safeHeaders.put(entry.getKey(), String.join(", ", entry.getValue()));
						}
					}
				}
				android.webkit.CookieManager.getInstance().flush();
				
				InputStream is = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder html = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.toLowerCase().contains("content-security-policy")) {
						line = line.replaceAll("<meta.*?http-equiv=[\"']?Content-Security-Policy[\"']?.*?>", "");
					}
					html.append(line).append("\n");
				}
				
				InputStream modifiedHtmlStream = new ByteArrayInputStream(html.toString().getBytes("UTF-8"));
				return new WebResourceResponse("text/html", "utf-8", connection.getResponseCode(), "OK", safeHeaders, modifiedHtmlStream);
				
			} catch (Exception e) {
				return super.shouldInterceptRequest(view, request);
			}
		}
		
		
		// Removed Google JS interception to fix login issues
		
		if (url.contains("youtube.com/ytpro_cdn/")) {
			String modifiedUrl = url;
			if (url.contains("youtube.com/ytpro_cdn/esm")) modifiedUrl = url.replace("youtube.com/ytpro_cdn/esm", "esm.sh");
			else if (url.contains("youtube.com/ytpro_cdn/npm")) modifiedUrl = url.replace("youtube.com/ytpro_cdn", "cdn.jsdelivr.net");
			
			try {
				URL newUrl = new URL(modifiedUrl);
				HttpsURLConnection connection = (HttpsURLConnection) newUrl.openConnection();
                
				connection.setUseCaches(false);
                connection.setDefaultUseCaches(false);
                connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                connection.addRequestProperty("Pragma", "no-cache");
                connection.addRequestProperty("Expires", "0");
                connection.setRequestProperty("User-Agent", "YTPRO");
                connection.setRequestProperty("Accept", "**");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                
                connection.setRequestMethod("GET");
				connection.connect();
				
				String mimeType = connection.getContentType() != null ? connection.getContentType() : "application/javascript";
				String encoding = connection.getContentEncoding() != null ? connection.getContentEncoding() : "utf-8";
				
				if (encoding == null) encoding = "utf-8";
                String contentType = connection.getContentType();
                if (contentType == null) {
                    contentType = "application/javascript";
                }

                Map<String, String> headers = new HashMap<>();
                headers.put("Access-Control-Allow-Origin", "*");
                headers.put("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                headers.put("Access-Control-Allow-Headers", "*");
                headers.put("Content-Type", contentType);
                headers.put("Access-Control-Allow-Credentials", "true");
                headers.put("Cross-Origin-Resource-Policy", "cross-origin");
                
                
				if (request.getMethod().equals("OPTIONS")) {
					return new WebResourceResponse("text/plain", "UTF-8", 204, "No Content", headers, null);
				}
				
				InputStream is = connection.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
				String jsCode = sb.toString();
				if (url.contains("npm/ytpro") && !url.contains("bgplay.js") && !url.contains("innertube.js")) {
					jsCode = jsCode.replace("Please follow Habitius on Instagram", "تواصل مع المطور (WhatsApp)")
							.replace("For daily habit,lifestyle and health tips", "لأي استفسار أو دعم فني تواصل معنا")
							.replace("https://www.instagram.com/habitius.daily", "https://wa.me/967781764759")
							.replace("#ee2a7b", "#34C759")
							.replace("background:#ee2a7b44", "background:rgba(37, 211, 102, 0.2)")
							.replace("https://raw.githubusercontent.com/prateek-chaubey/YTPro/refs/heads/main/.github/img/habitius.webp", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/WhatsApp.svg/120px-WhatsApp.svg.png")
							.replace("YT PRO Settings", "إعدادات YT PRO")
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
                            .replace("You can find the source code at <a href=\"https://www.youtube.com/redirect?q=https://github.com/prateek-chaubey/YTPRO\" style=\"font-family:monospace;\" > https://github.com/prateek-chaubey/YTPRO</a>", "")
                            .replace("Disclaimer", "تنبيه").replace("This is an unofficial OSS Youtube Mod, all the logos and brand names are property of Google LLC.", "<div style='direction:rtl;text-align:right;font-size:0.9rem;opacity:0.8;'>هذا تعديل يوتيوب مفتوح المصدر غير رسمي، جميع الشعارات والعلامات التجارية ملك لشركة Google LLC.</div>")
                            // Theme Colors
                            // Theme & Logic RTL Fix
                            .replace("if(isD)var a=[\"#000\",\"#717171\",\"#fff\"];else a=[\"#fff\",\"#909090\",\"#151515\"];", "if(isD)var a=[\"#fff\",\"#555\",\"#34C759\"];else a=[\"#fff\",\"#ccc\",\"#34C759\"];")
                            .replace("direction:rtl;", "direction:rtl;text-align:right;")
                            // Optimized Row Style - Fixed Overlap
                            .replace("#ssprodivI div{\nheight:10px;\nwidth:calc(100% - 20px);\npadding:10px;\nfont-size:1.45rem;\ntext-align:left;\ndisplay:flex;\nalign-items:center;\nposition:relative;\nmargin-top:3px;\n}", "#ssprodivI div{display:flex !important;justify-content:space-between !important;align-items:center !important;min-height:56px !important;width:calc(100% - 20px) !important;padding:8px 15px !important;font-size:1.1rem !important;text-align:right !important;position:relative !important;margin-top:5px !important;border-bottom:1px solid rgba(128,128,128,0.1) !important;box-sizing:border-box !important;}")
                            // Optimized Toggle Style
                            .replace("#ssprodivI div span{\ndisplay:block;\nheight:23px;\nwidth:40px;\nborder-radius:40px;\nright:10px;\nposition:absolute;\nbackground:#151515;\n}", "#ssprodivI div span{display:inline-flex !important;position:relative !important;height:26px !important;width:48px !important;border-radius:26px !important;background:#ccc !important;transition:all 0.3s ease !important;cursor:pointer !important;flex-shrink:0 !important;margin-left:10px !important;}")
                            .replace("#ssprodivI div span b{\ndisplay:block;\nheight:19px;\nwidth:19px;\nposition:absolute;\nright:2px;\ntop:2px;\nborder-radius:50px;\nbackground:#fff;\n}", "#ssprodivI div span b{height:22px !important;width:22px !important;background:#fff !important;border-radius:50% !important;position:absolute !important;left:2px !important;top:2px !important;transition:all 0.3s cubic-bezier(0.4, 0, 0.2, 1) !important;box-shadow:0 2px 5px rgba(0,0,0,0.3) !important;}")
                            // Logic Fix for Toggles in RTL
                            .replace("left:2px;", "right:2px;left:auto;")
                            .replace("e.children[0].style.left=\"2px\"", "e.children[0].style.right=\"2px\",e.children[0].style.left=\"auto\"")
                            .replace("e.children[0].style.left=\"auto\",e.children[0].style.right=\"2px\"", "e.children[0].style.left=\"2px\",e.children[0].style.right=\"auto\"")
                            .replace("1==t?`background:${a[0]};`:`background:${a[2]};`", "1==t?`background:${a[0]};left:2px;right:auto;`:`background:${a[2]};` ")
                            .replace("Save", "حفظ")
							.replace("Done", "تم")
                            .replace("GeminiModels={\"3.0 Pro\":\'[1,null,null,null,\"9d8ca3786ebdfbea\",null,null,0,[4],null,null,1]\',\"3.0 Flash\":\'[1,null,null,null,\"fbb127bbb056c959\",null,null,0,[4],null,null,1]\',\"3.0 Flash Thinking\":\'[1,null,null,null,\"5bf011840784117a\",null,null,0,[4],null,null,1]\',\"3.0 Pro Plus\":\'[1,null,null,null,\"e6fa609c3fa255c0\",null,null,0,[4],null,null,4]\',\"3.0 Flash Plus\":\'[1,null,null,null,\"56fdd199312815e2\",null,null,0,[4],null,null,4]\',\"3.0 Flash Thinking Plus\":\'[1,null,null,null,\"e051ce1aa80aa576\",null,null,0,[4],null,null,4]\',\"3.0 Pro Advanced\":\'[1,null,null,null,\"e6fa609c3fa255c0\",null,null,0,[4],null,null,2]\',\"3.0 Flash Advanced\":\'[1,null,null,null,\"56fdd199312815e2\",null,null,0,[4],null,null,2]\',\"3.0 Flash Thinking Advanced\":\'[1,null,null,null,\"e051ce1aa80aa576\",null,null,0,[4],null,null,2]\'}", "GeminiModels={\"3.0 Pro\":\'[1,null,null,null,\"9d8ca3786ebdfbea\",null,null,0,[4],null,null,1]\',\"3.0 Flash\":\'[1,null,null,null,\"fbb127bbb056c959\",null,null,0,[4],null,null,1]\',\"3.0 Flash Thinking\":\'[1,null,null,null,\"5bf011840784117a\",null,null,0,[4],null,null,1]\',\"3.0 Pro Plus\":\'[1,null,null,null,\"e6fa609c3fa255c0\",null,null,0,[4],null,null,4]\',\"3.0 Flash Plus\":\'[1,null,null,null,\"56fdd199312815e2\",null,null,0,[4],null,null,4]\',\"3.0 Flash Thinking Plus\":\'[1,null,null,null,\"e051ce1aa80aa576\",null,null,0,[4],null,null,4]\',\"3.0 Pro Advanced\":\'[1,null,null,null,\"e6fa609c3fa255c0\",null,null,0,[4],null,null,2]\',\"3.0 Flash Advanced\":\'[1,null,null,null,\"56fdd199312815e2\",null,null,0,[4],null,null,2]\',\"3.0 Flash Thinking Advanced\":\'[1,null,null,null,\"e051ce1aa80aa576\",null,null,0,[4],null,null,2]\',\"Gemini 3.5 Flash\":\'[1,null,null,null,\"9d8ca3786ebdfbea\",null,null,0,[4],null,null,1]\',\"Gemini 3.1 Flash Lite\":\'[1,null,null,null,\"9d8ca3786ebdfbea\",null,null,0,[4],null,null,1]\',\"Gemini 3.0 Flash Preview\":\'[1,null,null,null,\"9d8ca3786ebdfbea\",null,null,0,[4],null,null,1]\',\"Gemini 3.1 Pro Preview\":\'[1,null,null,null,\"9d8ca3786ebdfbea\",null,null,0,[4],null,null,1]\',\"Gemini 3.1 Flash Lite Image\":\'[1,null,null,null,\"9d8ca3786ebdfbea\",null,null,0,[4],null,null,1]\',\"Gemma 4 31B IT\":\'[1,null,null,null,\"9d8ca3786ebdfbea\",null,null,0,[4],null,null,1]\'}")
							.replace("Made with", "صُنع بكل ❤️")
							.replace("by Prateek Chaubey", "بواسطة عبدالرحمن المخلافي 🔥")
;
				}
				InputStream modifiedStream = new ByteArrayInputStream(jsCode.getBytes("UTF-8"));
				return new WebResourceResponse(mimeType, encoding, connection.getResponseCode(), "OK", headers, modifiedStream);
			} catch (Exception e) {
				return super.shouldInterceptRequest(view, request);
			}
		}
		
		return super.shouldInterceptRequest(view, request);
	}
	
	@Override
	public void onPageFinished(WebView view, String url) {
		web.evaluateJavascript("if (window.trustedTypes && window.trustedTypes.createPolicy && !window.trustedTypes.defaultPolicy) {window.trustedTypes.createPolicy('default', {createHTML: (string) => string,createScriptURL: string => string, createScript: string => string, });}", null);
		web.evaluateJavascript("(function () { var script = document.createElement('script'); script.src='https://youtube.com/ytpro_cdn/npm/ytpro'; document.body.appendChild(script);  })();", null);
		web.evaluateJavascript("setInterval(function() { if (window.ytproDownVid && !window.ytproDownVidReplaced) { window.ytproDownVid = function() { Android.openSeal(window.location.href.split('#')[0]); setTimeout(function(){window.history.back();}, 100); }; window.ytproDownVidReplaced = true; } }, 500);", null);
		web.evaluateJavascript("window.addEventListener('hashchange', function(e) { if (window.location.hash === '#download') { e.stopImmediatePropagation(); Android.openSeal(window.location.href.split('#')[0]); setTimeout(function(){window.history.back();}, 100); } }, true);", null);
		web.evaluateJavascript("(function () { var script = document.createElement('script'); script.src='https://youtube.com/ytpro_cdn/npm/ytpro/bgplay.js'; document.body.appendChild(script);  })();", null);
		web.evaluateJavascript("(function () { var script = document.createElement('script');script.type='module';script.src='https://youtube.com/ytpro_cdn/npm/ytpro/innertube.js'; document.body.appendChild(script);  })();", null);
		
		// Translation & Share-to-Download Transformer - V20 (Dedicated Download Button)
		web.evaluateJavascript("(function() { " +
				"  const translations = { " +
				"    'YT PRO Settings': 'إعدادات YT PRO', " +
				"    'Enter Youtube URL': 'أدخل رابط يوتيوب', " +
				"    'Liked Videos': 'الفيديوهات التي أعجبتني', " +
				"    'Check for Updates': 'التحقق من وجود تحديثات', " +
				"    'Autoskip Sponsors': 'تخطي الرعاة تلقائياً', " +
				"    'Gesture Controls': 'عناصر التحكم بالإيماءات', " +
				"    'Miniplayer Gesture': 'إيماءة المشغل المصغر', " +
				"    'Force Zoom': 'فرض التكبير', " +
				"    'Background Play': 'التشغيل في الخلفية', " +
				"    'Hide Shorts': 'إخفاء الفيديوهات القصيرة', " +
				"    'Use single Gemini chat': 'دردشة Gemini موحدة', " +
				"    'Select Gemini Model': 'اختر نموذج Gemini', " +
				"    'Edit Gemini Prompt': 'تعديل أمر Gemini', " +
				"    'Disable Codecs': 'تعطيل الكوديك (Codecs)', " +
				"    'Report Bugs': 'الإبلاغ عن المشاكل', " +
				"    'Become a Sponsor': 'كن راعياً للمشروع', " +
				"    'Developer Mode': 'وضع المطور', " +
				"    'Disclaimer': 'تنبيه', " +
				"    'This is an unofficial OSS Youtube Mod': 'هذا تعديل يوتيوب مفتوح المصدر غير رسمي', " +
				"    'all the logos and brand names are property of Google LLC': 'جميع الشعارات والعلامات التجارية ملك لشركة Google LLC', " +
				"    'You can find the source code at': 'يمكنك العثور على الكود المصدري في', " +
				"    'Save': 'حفظ', " +
				"    'Done': 'تم', " +
				"    'Loading...': 'جاري التحميل...', " +
				"    'Language': 'اللغة', " +
				"    'Search YouTube': 'بحث في يوتيوب', " +
				"    'YouTube': 'يوتيوب', " +
				"    'Home': 'الرئيسية', " +
				"    'Shorts': 'قصيرة', " +
				"    'Subscriptions': 'الاشتراكات', " +
				"    'Library': 'المكتبة', " +
				"    'Made with': 'صُنع بكل ❤️', " +
				"    'by Prateek Chaubey': 'بواسطة عبدالرحمن المخلافي 🔥', " +
				"    'Please follow Habitius on Instagram': 'تواصل مع المطور (WhatsApp)', " +
				"    'For daily habit,lifestyle and health tips': 'لأي استفسار أو دعم فني تواصل معنا' " +
				"  }; " +
				"  function runTools() { " +
				"    // 1. Translation Engine " +
				"    var elements = document.querySelectorAll('div, span, button, p, b, li, a'); " +
				"    elements.forEach(el => { " +
				"      if (el.children.length > 0 && el.tagName !== 'B') return; " +
				"      var txt = el.innerText.trim(); " +
				"      if (translations[txt]) { " +
				"        el.innerText = translations[txt]; " +
				"      } else { " +
				"        for (var key in translations) { " +
				"          if (txt === key || (txt.length < 30 && txt.includes(key))) { " +
				"            el.innerText = el.innerText.replace(key, translations[key]); " +
				"          } " +
				"        } " +
				"      } " +
				"    }); " +
				"    // 2. Input Placeholders " +
				"    document.querySelectorAll('input').forEach(input => { " +
				"      if (translations[input.placeholder]) input.placeholder = translations[input.placeholder]; " +
				"    }); " +
				"    // 3. Inject Download Button (Shorts) " +
				"    if (window.location.href.includes('/shorts/')) { " +
				"      var svgs = document.querySelectorAll('svg'); " +
				"      var refChild = null; " +
				"      var column = null; " +
				"      for (var i=0; i<svgs.length; i++) { " +
				"        var p = svgs[i].querySelector('path'); " +
				"        if (p && (p.getAttribute('d').startsWith('M18.77') || p.getAttribute('d').startsWith('M1 21h4V9H1v12zm22-11') || p.getAttribute('d').startsWith('M3,11h3v10H3V11z M18.77'))) { " +
				"          var curr = svgs[i]; " +
				"          while(curr && curr.tagName !== 'BODY') { " +
				"            if (curr.children.length >= 3) { column = curr; refChild = svgs[i]; break; } " +
				"            curr = curr.parentElement; " +
				"          } " +
				"          if (column) break; " +
				"        } " +
				"      } " +
				"      if (!column) { " +
				"        var allBtns = document.querySelectorAll('button'); " +
				"        for (var j=0; j<allBtns.length; j++) { " +
				"          var lbl = allBtns[j].getAttribute('aria-label') || ''; " +
				"          if (lbl.toLowerCase().includes('like') || lbl.includes('إعجاب') || lbl.includes('أعجبني')) { " +
				"            var curr2 = allBtns[j]; " +
				"            while(curr2 && curr2.tagName !== 'BODY') { " +
				"              if (curr2.children.length >= 3) { column = curr2; refChild = allBtns[j]; break; } " +
				"              curr2 = curr2.parentElement; " +
				"            } " +
				"            if (column) break; " +
				"          } " +
				"        } " +
				"      } " +
				"      if (column && !column.querySelector('.ytpro-dl-new-btn')) { " +
				"          var dlBtn = document.createElement('div'); " +
				"          dlBtn.className = 'ytpro-dl-new-btn'; " +
				"          dlBtn.style.cssText = 'display: flex; flex-direction: column; align-items: center; justify-content: center; padding-top: 8px; padding-bottom: 8px; cursor: pointer; z-index: 9999; margin-bottom: 4px;'; " +
				"          dlBtn.innerHTML = '<div style=\"background: rgba(0,0,0,0.6); width: 44px; height: 44px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 6px;\"><svg viewBox=\"0 0 24 24\" style=\"width: 24px; height: 24px; fill: white;\"><path d=\"M17 18V19H6V18H17ZM16.5 11.4L15.8 10.7L12 14.4V4H11V14.4L7.2 10.6L6.5 11.3L11.5 16.3L16.5 11.4Z\"></path></svg></div><span style=\"color: white; font-size: 13px; font-weight: 500; font-family: Roboto, Arial, sans-serif; text-shadow: 1px 1px 2px rgba(0,0,0,0.8);\">تنزيل</span>'; " +
				"          dlBtn.onclick = function(e) { " +
				"            e.preventDefault(); e.stopPropagation(); " +
				"            Android.openSeal(window.location.href.split(\"#\")[0]); " +
				"          }; " +
				"          var child = refChild; " +
				"          while(child.parentElement && child.parentElement !== column) { child = child.parentElement; } " +
				"          column.insertBefore(dlBtn, child); " +
				"      } else if (!column && !document.querySelector('.ytpro-dl-fallback-btn')) { " +
				"        var fb = document.createElement('div'); " +
				"        fb.className = 'ytpro-dl-fallback-btn ytpro-dl-new-btn'; " +
				"        fb.style.cssText = 'position: fixed; right: 12px; top: 40%; transform: translateY(-50%); display: flex; flex-direction: column; align-items: center; justify-content: center; z-index: 999999;'; " +
				"        fb.innerHTML = '<div style=\"background: rgba(0,0,0,0.6); width: 44px; height: 44px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin-bottom: 6px; border: 1.5px solid rgba(255,255,255,0.4);\"><svg viewBox=\"0 0 24 24\" style=\"width: 24px; height: 24px; fill: white;\"><path d=\"M17 18V19H6V18H17ZM16.5 11.4L15.8 10.7L12 14.4V4H11V14.4L7.2 10.6L6.5 11.3L11.5 16.3L16.5 11.4Z\"></path></svg></div><span style=\"color: white; font-size: 13px; font-weight: 500; font-family: Roboto, Arial, sans-serif; text-shadow: 1px 1px 2px rgba(0,0,0,1);\">تنزيل</span>'; " +
				"        fb.onclick = function(e) { e.preventDefault(); e.stopPropagation(); Android.openSeal(window.location.href.split(\"#\")[0]); }; " +
				"        document.body.appendChild(fb); " +
				"      } " +
				"    } else { " +
				"      var fb = document.querySelector('.ytpro-dl-fallback-btn'); if (fb) fb.remove(); " +
				"    } " +
				"    // 4. Transform Normal Video Share Buttons " +
				"    var selectors = ['ytm-share-button-renderer', 'button[aria-label*=\"Share\"]', 'button[aria-label*=\"مشاركة\"]']; " +
				"    document.querySelectorAll(selectors.join(',')).forEach(btn => { " +
				"      if (btn.dataset.ytproV21) return; " +
				"      btn.dataset.ytproV21 = 'true'; " +
				"      if (window.location.href.includes('/shorts/')) return; " +
				"      btn.onclick = function(e) { " +
				"        e.preventDefault(); e.stopPropagation(); " +
				"        Android.openSeal(window.location.href.split(\"#\")[0]); " +
				"        return false; " +
				"      }; " +
				"      var svg = btn.querySelector('svg'); " +
				"      if (svg) svg.innerHTML = '<path d=\"M17 18V19H6V18H17ZM16.5 11.4L15.8 10.7L12 14.4V4H11V14.4L7.2 10.6L6.5 11.3L11.5 16.3L16.5 11.4Z\" fill=\"white\"></path>'; " +
				"      var txt = btn.querySelector('span, div[class*=\"text\"]'); " +
				"      if (txt) { txt.innerText = 'تنزيل'; txt.style.color = 'white'; } " +
				"      if (btn.getAttribute('aria-label')) btn.setAttribute('aria-label', 'تنزيل'); " +
				"    }); " +
				"  } " +
				"  if (!window.ytproMasterV21) { window.ytproMasterV21 = true; setInterval(runTools, 700); } " +
				"})();", null);

		
		if (!url.contains("youtube.com/watch") && !url.contains("youtube.com/shorts") && activity.isPlaying) {
			activity.isPlaying = false;
			activity.mediaSession = false;
			activity.stopService(new Intent(activity.getApplicationContext(), ForegroundService.class));
		}
		super.onPageFinished(view, url);
	}
}
