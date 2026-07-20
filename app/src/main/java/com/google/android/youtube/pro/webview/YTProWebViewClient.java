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
							.replace("#ee2a7b", "#25D366")
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
							.replace("Disclaimer", "تنبيه")
							.replace("Save", "حفظ")
							.replace("Done", "تم")
							.replace("Made with", "صُنع بـ")
							.replace("by Prateek Chaubey", "بواسطة Prateek Chaubey")
							.replace("Please follow Habitius on Instagram", "تواصل مع المطور (WhatsApp)")
							.replace("For daily habit,lifestyle and health tips", "لأي استفسار أو دعم فني تواصل معنا");
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
		
		// Translation & Share-to-Download Transformer - V16 (Master)
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
				"    'Made with': 'صُنع بـ', " +
				"    'by Prateek Chaubey': 'بواسطة Prateek Chaubey', " +
				"    'Please follow Habitius on Instagram': 'تواصل مع المطور (WhatsApp)', " +
				"    'For daily habit,lifestyle and health tips': 'لأي استفسار أو دعم فني تواصل معنا' " +
				"  }; " +
				"  function runTools() { " +
				"    // 1. Translate UI Components " +
				"    var elements = document.querySelectorAll('div, span, button, p, b, li, a'); " +
				"    elements.forEach(el => { " +
				"      if (el.children.length > 0 && el.tagName !== 'B') return; " +
				"      var txt = el.innerText.trim(); " +
				"      for (var key in translations) { " +
				"        if (txt === key || txt.includes(key)) { " +
				"          el.innerText = el.innerText.replace(key, translations[key]); " +
				"        } " +
				"      } " +
				"    }); " +
				"    // 2. Placeholder Translation " +
				"    document.querySelectorAll('input').forEach(input => { " +
				"      if (translations[input.placeholder]) input.placeholder = translations[input.placeholder]; " +
				"    }); " +
				"    // 3. Transform Share Buttons (Shorts + Normal) " +
				"    var shareSelectors = ['[aria-label*=\"Share\"]', '[aria-label*=\"مشاركة\"]', '.reel-player-overlay-actions-share', 'ytm-share-button-renderer', 'button[aria-label*=\"Share\"]', 'button[aria-label*=\"مشاركة\"]', '.ytm-reel-player-overlay-actions > div:nth-child(3)']; " +
				"    document.querySelectorAll(shareSelectors.join(',')).forEach(btn => { " +
				"      if (btn.dataset.ytproV17) return; " +
				"      btn.dataset.ytproV17 = 'true'; " +
				"      btn.onclick = function(e) { " +
				"        e.preventDefault(); e.stopPropagation(); " +
				"        Android.openSeal(window.location.href.split(/[?#]/)[0]); " +
				"        return false; " +
				"      }; " +
				"      var svg = btn.querySelector('svg'); " +
				"      if (svg) svg.innerHTML = '<path d=\"M17 18V19H6V18H17ZM16.5 11.4L15.8 10.7L12 14.4V4H11V14.4L7.2 10.6L6.5 11.3L11.5 16.3L16.5 11.4Z\" fill=\"white\"></path>'; " +
				"      var txt = btn.querySelector('span, div[class*=\"text\"]'); " +
				"      if (txt) { txt.innerText = 'تنزيل'; txt.style.color = 'white'; } " +
				"    }); " +
				"  } " +
				"  if (!window.ytproMasterV17) { window.ytproMasterV17 = true; setInterval(runTools, 600); } " +
				"})();", null);

		
		if (!url.contains("youtube.com/watch") && !url.contains("youtube.com/shorts") && activity.isPlaying) {
			activity.isPlaying = false;
			activity.mediaSession = false;
			activity.stopService(new Intent(activity.getApplicationContext(), ForegroundService.class));
		}
		super.onPageFinished(view, url);
	}
}
