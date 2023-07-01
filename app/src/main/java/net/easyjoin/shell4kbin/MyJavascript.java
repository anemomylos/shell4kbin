package net.easyjoin.shell4kbin;


import android.webkit.JavascriptInterface;

import net.easyjoin.utils.MyLog;

public final class MyJavascript
{
  private final String className = getClass().getName();

  @JavascriptInterface
  public void processHTML(final String html)
  {
    MyLog.w(className, "processHTML", html);
  }
}
