package net.easyjoin.shell4kbin.browser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.easyjoin.shell4kbin.utils.CachedValues;
import net.easyjoin.utils.MyLog;

public class MyWebViewClient extends WebViewClient
{
  private final String className = getClass().getName();
  private final String htmlOnError = "<html><head><meta charset=\"UTF-8\"></head><body><div style=\"text-align:center; width: 98%; margin-top: 80px\">There was an error processing the page.<br>Check your Internet connection.</div><div style=\"text-align:center; width: 98%; margin-top: 20px\"><button onclick=\"window.history.back();\">Back</button></div></body></html>";
  protected BrowserModel browserModel;
  protected String webviewName;

  public MyWebViewClient(BrowserModel browserModel, String webviewName)
  {
    this.browserModel = browserModel;
    this.webviewName = webviewName;
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView webView, String url)
  {
    if( (CachedValues.isExternalLinksDefaultBrowser()) && (CachedValues.isBrowserIntentCanBeHandled()) )
    {
      if(!url.startsWith("https://kbin."))
      {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        webView.getContext().startActivity(browserIntent);
        return true;
      }
    }

    return super.shouldOverrideUrlLoading(webView, url);
  }

  @Override
  public void onPageStarted(final WebView webView, final String url, Bitmap favicon)
  {
    super.onPageStarted(webView, url, favicon);
    browserModel.addNextPage(url);
    browserModel.setPageTitle();
  }

  @Override
  public void onPageFinished(WebView webView, String url)
  {
    super.onPageFinished(webView, url);
    browserModel.setPageTitle();
    browserModel.elaborateHtml();
    browserModel.inject(url);
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