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
		
		// Shorts Download Button Injection
		web.evaluateJavascript("(function() { " +
				"  function addShortsDownloadButton() { " +
				"    if (!window.location.href.includes('/shorts/')) return; " +
				"    var actions = document.querySelectorAll('ytm-reel-player-overlay-actions, .reel-player-overlay-actions-container, #actions-container.ytm-reel-player-overlay-actions'); " +
				"    actions.forEach(function(action) { " +
				"      if (action.querySelector('.ytpro-shorts-download')) return; " +
				"      var btn = document.createElement('div'); " +
				"      btn.className = 'ytpro-shorts-download'; " +
				"      btn.style.display = 'flex'; btn.style.flexDirection = 'column'; btn.style.alignItems = 'center'; btn.style.marginTop = '12px'; btn.style.cursor = 'pointer'; " +
				"      btn.innerHTML = '<div style=\"display:flex;flex-direction:column;align-items:center;color:white;\"><svg viewBox=\"0 0 24 24\" style=\"width:28px;height:28px;fill:white;\"><path d=\"M17 18V19H6V18H17ZM16.5 11.4L15.8 10.7L12 14.4V4H11V14.4L7.2 10.6L6.5 11.3L11.5 16.3L16.5 11.4Z\"></path></svg><span style=\"font-size:10px;margin-top:4px;font-weight:bold;text-shadow: 1px 1px 2px rgba(0,0,0,0.8);\">تنزيل</span></div>'; " +
				"      btn.onclick = function(e) { " +
				"        e.preventDefault(); e.stopPropagation(); " +
				"        Android.openSeal(window.location.href.split('?')[0]); " +
				"      }; " +
				"      if (action.firstChild) action.insertBefore(btn, action.firstChild); else action.appendChild(btn); " +
				"    }); " +
				"  } " +
				"  if (!window.ytproShortsInterval) window.ytproShortsInterval = setInterval(addShortsDownloadButton, 1500); " +
				"})();", null);
		
		if (!url.contains("youtube.com/watch") && !url.contains("youtube.com/shorts") && activity.isPlaying) {
			activity.isPlaying = false;
			activity.mediaSession = false;
			activity.stopService(new Intent(activity.getApplicationContext(), ForegroundService.class));
		}
		super.onPageFinished(view, url);
	}
}
