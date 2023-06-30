package net.easyjoin.shell4kbin;

import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

import net.droidopoulos.utils.MyLog;

public class MyWebChromeClient extends WebChromeClient
{
  private final String className = getClass().getName();

  @Override
  public boolean onConsoleMessage(ConsoleMessage consoleMessage)
  {
    //MyLog.e(className, "onReceivedError", "consoleMessage: " + consoleMessage.message());
    //return super.onConsoleMessage(consoleMessage);
    return true;
  }

}
