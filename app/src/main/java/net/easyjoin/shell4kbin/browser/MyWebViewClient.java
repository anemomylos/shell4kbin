package net.easyjoin.shell4kbin.browser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.easyjoin.shell4kbin.utils.CachedValues;
import net.easyjoin.utils.MyLog;

public class MyWebViewClient extends WebViewClient
{
  private final String className = getClass().getName();
  private final String htmlOnError = "<html><head><meta charset=\"UTF-8\"><title>Error on loading page</title></head><body><div style=\"text-align:center; width: 98%; margin-top: 180px\"><div id=\"urlInError\"></div>There was an error processing the page.<br><br>Check your Internet connection and retry.</div><div style=\"text-align:center; width: 98%; margin-top: 20px\"><button onclick=\"window.history.back();\">Back</button><button onclick=\"location.reload()\" style=\" margin-left: 30px\">Reload</button></div></body><script>document.getElementById(\"urlInError\").innerHTML = location.href;</script></html>";
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
    try
    {
      webView.loadDataWithBaseURL(failingUrl, htmlOnError, "text/html; charset=utf-8", "utf-8", failingUrl);
    }
    catch(Throwable t)
    {
      super.onReceivedError(webView, errorCode, description, failingUrl);
    }
  }

  @Override
  public void onReceivedError(WebView webView, WebResourceRequest req, WebResourceError rerr)
  {
    try
    {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      {
        webView.loadDataWithBaseURL(req.getUrl().toString(), htmlOnError, "text/html; charset=utf-8", "utf-8", req.getUrl().toString());
      }
      else
      {
        super.onReceivedError(webView, req, rerr);
      }
    }
    catch(Throwable t)
    {
      super.onReceivedError(webView, req, rerr);
    }
  }
}