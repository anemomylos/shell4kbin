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
import net.easyjoin.utils.ReplaceText;
import net.easyjoin.utils.ThemeUtils;

public class MyWebViewClient extends WebViewClient
{
  private final String className = getClass().getName();
  private final String htmlOnError = "<html><head><meta charset=\"UTF-8\"><title>Error on loading page</title></head><body style=\"background-color: white; color: black\"><div style=\"text-align:center; width: 98%; margin-top: 180px\">An error occurred while loading the page:<br><br><div>$urlInError$</div><br><br>Check your Internet connection and retry.</div><div style=\"text-align:center; width: 98%; margin-top: 20px\"><button onclick=\"window.history.back();\">Back</button><button onclick=\"location.href = '$urlInError$'\" style=\" margin-left: 30px\">Reload</button></div></body></html>";
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
    browserModel.showShareButton();
    browserModel.addNextPage(url);
    browserModel.setPageTitle();
  }

  @Override
  public void onPageFinished(WebView webView, String url)
  {
    super.onPageFinished(webView, url);
    browserModel.setPageTitle();
    browserModel.showShareButton();
    browserModel.elaborateHtml();
    browserModel.inject(url);
  }

  @Override
  public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl)
  {
    try
    {
      webView.loadData(getErrorHtml(webView, failingUrl), "text/html; charset=utf-8", null);
      browserModel.setPageTitle();
    }
    catch(Throwable t)
    {
      super.onReceivedError(webView, errorCode, description, failingUrl);
    }
  }

  @Override
  public void onReceivedError(WebView webView, WebResourceRequest req, WebResourceError error)
  {
    try
    {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      {
        webView.loadData(getErrorHtml(webView, req.getUrl().toString()), "text/html; charset=utf-8", null);
        browserModel.setPageTitle();
      }
      else
      {
        super.onReceivedError(webView, req, error);
      }
    }
    catch(Throwable t)
    {
      super.onReceivedError(webView, req, error);
    }
  }

  private String getErrorHtml(WebView webView, String url)
  {
    String html2USe = htmlOnError;
    html2USe = ReplaceText.replace(html2USe, "$urlInError$", url);
    if(ThemeUtils.useBlackTheme(webView.getContext()))
    {
      html2USe = ReplaceText.replace(html2USe, "color: black", "color: white");
      html2USe = ReplaceText.replace(html2USe, "-color: white", "-color: black");
    }
    return html2USe;
  }
}