package net.easyjoin.shell4kbin;


import android.text.Html;
import android.webkit.JavascriptInterface;

import net.droidopoulos.utils.MyLog;

public final class MyJavascript
{
  private final String className = getClass().getName();

  @JavascriptInterface
  public void processHTML(final String html)
  {
    Thread oAuthFetcher=new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        String oAuthDetails = Html.fromHtml(html).toString();
        MyLog.w(className, "processHTML:run", oAuthDetails);
      }
    });
    oAuthFetcher.start();
  }
}
