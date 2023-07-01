package net.easyjoin.utils;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;


public final class MyLog
{
  private static String appName = "MyApp";
  private static int notificationId = 20000;
  private static final String notificationChannelId = "log_1";
  private static final String notificationChannelName = "log";
  private static final int NOTIFICATION_TYPE_ERROR = 1;
  private static final int NOTIFICATION_TYPE_WARN = 2;
  private static final int NOTIFICATION_TYPE_INFO = 3;

  public static void setAppName(String name)
  {
    appName = name;
  }

  public static void w(String className, String method, String text)
  {
    String prefix = className + ":" + method + " : ";
    int prefixLength = prefix.length();
    if( (text != null) && ((prefixLength + text.length()) > 4000) )
    {
      int maxSize = (4000 - prefixLength);
      int chunkCount = text.length() / maxSize;
      for (int i = 0; i <= chunkCount; i++)
      {
        int max = maxSize * (i + 1);
        if (max >= text.length())
        {
          Log.w(appName, prefix + text.substring(maxSize * i));
        }
        else
        {
          Log.w(appName, prefix + text.substring(maxSize * i, max));
        }
      }
    }
    else
    {
      Log.w(appName, prefix + text);
    }
  }

  public static void e(String className, String method, String text)
  {
    Log.e(appName, className + ":" + method + " : " + text);
  }

  public static void e(String className, String method, String text, Throwable t)
  {
    Log.e(appName, className + ":" + method + " " + text, t);
  }

  public static void e(String className, String method, Throwable t)
  {
    Log.e(appName, className + ":" + method, t);
  }

  public static int notification(String className, String method, Context context, String msgShort, String msg, int type)
  {
    int notificationId2Use = notificationId++;

    try
    {
      String appCode = "0";
      try
      {
        appCode = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
      }
      catch (Throwable th)
      {
      }

      String typeStr = "";
      if(type == 1)
      {
        typeStr = "Error";
      }
      else if(type == 2)
      {
        typeStr = "Warning";
      }
      else if(type == 3)
      {
        typeStr = "Info";
      }
      String title = typeStr + ": " + appName;

      String content = msgShort;
      String bigContent = msg;

      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
        .setContentTitle(title)
        .setTicker(title)
        .setContentText(content)
        .setOngoing(false)
        .setAutoCancel(true);

      if(type == 1)
      {
        String mailSubject = title + " (v." + appCode + ") (" + className + "." + method + ")";
        String deviceInfo = Build.VERSION.RELEASE + " | " + Build.MANUFACTURER + " | " + Build.MODEL;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mailTo = MyResources.getString("mail_to_errors", context);
        if (Miscellaneous.isEmpty(mailTo))
        {
          mailTo = "therealanemomylos@gmail.com";
        }
        Uri data = Uri.parse("mailto:" + mailTo + "?subject=" + mailSubject + "&body=" + deviceInfo + " \n\n" + bigContent);
        intent.setData(data);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId2Use, intent, VariousUtils.getFlag4UpdateCurrent());
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_dialog_alert);
        notificationBuilder.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false));
      }
      else if(type == 2)
      {
        notificationBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_dialog_alert);
        notificationBuilder.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false));
      }
      else if(type == 3)
      {
        notificationBuilder.setSmallIcon(android.R.drawable.ic_dialog_info);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_dialog_info);
        notificationBuilder.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false));
      }

      NotificationUtils.setChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT, false, false, context, notificationBuilder);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
      {
        if (bigContent != null)
        {
          notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(bigContent));
        }
      }

      Notification notification = notificationBuilder.build();

      NotificationUtils.show(notification, context, appName, notificationId2Use, false);
    }
    catch (Throwable t) { }

    return notificationId2Use;
  }

  public static int notificationInfo(Context context, String infoMsg)
  {
    return notification("", "", context, infoMsg, infoMsg, NOTIFICATION_TYPE_INFO);
  }

  public static int notificationWarn(Context context, String warnMsg)
  {
    return notification("", "", context, warnMsg, warnMsg, NOTIFICATION_TYPE_WARN);
  }

  public static int notificationError(String className, String method, Context context, String errorMsgShort, String errorMsg)
  {
    return notification(className, method, context, errorMsgShort, errorMsg, NOTIFICATION_TYPE_ERROR);
  }

  public static int notification(String className, String method, Context context, Throwable t)
  {
    return notificationError(className, method, context, t.getMessage(), stackTrace(t));
  }

  public static void removeNotification(Context context, int notificationId2Close)
  {
    NotificationUtils.clean(context, appName, notificationId2Close);
  }

  public static String stackTrace(Throwable t)
  {
    if(t != null)
    {
      ByteArrayOutputStream buf = new ByteArrayOutputStream(1024);
      PrintWriter pw = new PrintWriter(buf);
      t.printStackTrace( pw );
      pw.flush();
      return buf.toString();
    }
    else
      return "";
  }
}
