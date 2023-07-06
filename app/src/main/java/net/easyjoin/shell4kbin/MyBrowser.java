package net.easyjoin.shell4kbin;

import android.graphics.Bitmap;
import android.util.Base64;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.easyjoin.utils.MyLog;

import java.io.InputStream;
import java.net.URLEncoder;

public class MyBrowser extends WebViewClient
{
  protected final String className = getClass().getName();
  private final String htmlOnError = "<html><head><meta charset=\"UTF-8\"></head><body><div style=\"text-align:center; width: 98%; margin-top: 80px\">There was an error processing the page.<br>Check your Internet connection.</div><div style=\"text-align:center; width: 98%; margin-top: 20px\"><button onclick=\"window.history.back();\">Back</button></div></body></html>";
  protected ModelWeb modelWeb;
  protected String webviewName;

  public MyBrowser(ModelWeb modelWeb, String webviewName)
  {
    this.modelWeb = modelWeb;
    this.webviewName = webviewName;
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView webView, String url)
  {
    return super.shouldOverrideUrlLoading(webView, url);
    /*try
    {
      view.loadUrl(url);
    }
    catch(Throwable t)
    {
      MyLog.e(className, "shouldOverrideUrlLoading", t);
      return false;
    }

    return true;*/
  }

  @Override
  public void onPageStarted(final WebView webView, final String url, Bitmap favicon)
  {
    super.onPageStarted(webView, url, favicon);
    modelWeb.addNextPage(url);
    modelWeb.setPageTitle();
  }

  @Override
  public void onPageFinished(WebView webView, String url)
  {
    super.onPageFinished(webView, url);
    modelWeb.setPageTitle();
    modelWeb.elaborateHtml();
    modelWeb.inject();
  }

  @Override
  public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl)
  {
    /*try
    {
      view.loadDataWithBaseURL("about:blank", htmlOnError, "text/html; charset=utf-8", "utf-8", null);
    }
    catch(Throwable t)
    {
      super.onReceivedError(view, errorCode, description, failingUrl);
    }*/
    MyLog.e(className, "onReceivedError", "errorCode=" + errorCode + ", description=" + description + ", failingUrl=" + failingUrl);
    super.onReceivedError(webView, errorCode, description, failingUrl);
  }

  @Override
  public void onReceivedError(WebView webView, WebResourceRequest req, WebResourceError rerr)
  {
    MyLog.e(className, "onReceivedError", "rerr=" + rerr + ", failingUrl=" + req);
    super.onReceivedError(webView, req, rerr);
  }
}