package net.easyjoin.utils;

import android.app.Activity;
import android.os.Build;
import android.util.Base64;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

public final class WebViewUtils
{
  public static void cleanWebViewContent(WebView webView)
  {
    webView.stopLoading();
    webView.loadUrl("javascript:document.open();document.close();");
    webView.clearView();
  }

  public static void loadEmptyPage(WebView webView)
  {
    String html = "<html><body style=\"background-color: black\"></body></html>";
    String encodedHtml = Base64.encodeToString(html.getBytes(), Base64.NO_PADDING);
    webView.loadData(encodedHtml, "text/html", "base64");
  }

  public static void clean(WebView webView, Activity activity)
  {
    cleanWebViewContent(webView);
    webView.clearCache(true);
    webView.clearFormData();
    webView.clearSslPreferences();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
    {
      CookieManager.getInstance().removeAllCookies(null);
      CookieManager.getInstance().flush();
    }
    else
    {
      CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(activity);
      cookieSyncMngr.startSync();
      CookieManager cookieManager=CookieManager.getInstance();
      cookieManager.removeAllCookie();
      cookieManager.removeSessionCookie();
      cookieSyncMngr.stopSync();
      cookieSyncMngr.sync();
    }
  }
}
