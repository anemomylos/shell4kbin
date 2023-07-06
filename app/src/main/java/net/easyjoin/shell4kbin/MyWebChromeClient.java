package net.easyjoin.shell4kbin;

import android.app.Activity;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import net.easyjoin.utils.Miscellaneous;
import net.easyjoin.utils.MyResources;

public class MyWebChromeClient extends WebChromeClient
{
  private final String className = getClass().getName();
  private ProgressBar progressBar;

  public MyWebChromeClient(ProgressBar progressBar)
  {
    this.progressBar = progressBar;
  }
  @Override
  public boolean onConsoleMessage(ConsoleMessage consoleMessage)
  {
    //MyLog.e(className, "onReceivedError", "consoleMessage: " + consoleMessage.message());
    //return super.onConsoleMessage(consoleMessage);
    return true;
  }

  @Override
  public void onProgressChanged(WebView view, int newProgress)
  {
    super.onProgressChanged(view, newProgress);

    if(progressBar != null)
    {
      int progress = Math.min(100, newProgress);
      progressBar.setVisibility(progress < 100 ? View.VISIBLE : View.GONE);
      progressBar.setProgress(progress);
    }
  }
}
