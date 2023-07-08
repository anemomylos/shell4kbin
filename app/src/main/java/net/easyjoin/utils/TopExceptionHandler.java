package net.easyjoin.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;

public final class TopExceptionHandler implements Thread.UncaughtExceptionHandler
{
  private Thread.UncaughtExceptionHandler defaultUEH;
  private Activity app = null;

  public TopExceptionHandler(Activity app)
  {
    this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    this.app = app;
  }

  public void uncaughtException(Thread t, Throwable e)
  {
    StackTraceElement[] arr = e.getStackTrace();
    String report = e.toString() + "\n\n";
    report += "--------- Stack trace ---------\n\n";
    for (int i = 0; i < arr.length; i++)
    {
      report += "    " + arr[i].toString() + "\n";
    }
    report += "-------------------------------\n\n";

    // If the exception was thrown in a background thread inside
    // AsyncTask, then the actual exception can be found with getCause

    report += "--------- Cause ---------\n\n";
    Throwable cause = e.getCause();
    if (cause != null)
    {
      report += cause.toString() + "\n\n";
      arr = cause.getStackTrace();
      for (int i = 0; i < arr.length; i++)
      {
        report += "    " + arr[i].toString() + "\n";
      }
    }
    report += "-------------------------------\n\n";

    try
    {
      FileOutputStream trace = app.openFileOutput("stack.trace", Context.MODE_PRIVATE);
      trace.write(report.getBytes());
      trace.close();

      try
      {
        ClipboardManager clipboard = (ClipboardManager) app.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Error", report);
        clipboard.setPrimaryClip(clip);
      }
      catch (Throwable t1){}

      MyLog.notificationError("", "", app, e.getMessage(), report);
    }
    catch (IOException ioe)
    {
      // ...
    }

    defaultUEH.uncaughtException(t, e);
  }
}