package net.easyjoin.utils;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public final class NotificationUtils
{
  public static Notification create(Class mainActivityClass, Context context, String iconName, String smallIconName, String contentTextName, String title, String ticker)
  {
    Intent toLaunch = new Intent(context, mainActivityClass);
    toLaunch.setAction("android.intent.action.MAIN");
    toLaunch.addCategory("android.intent.category.LAUNCHER");
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, toLaunch, VariousUtils.getFlag4UpdateCurrent());

    Bitmap icon = BitmapFactory.decodeResource(context.getResources(), MyResources.getDrawable(iconName, context));

    Notification notification = new NotificationCompat.Builder(context)
      .setContentTitle(title)
      .setTicker(ticker)
      .setContentText(contentTextName)
      .setSmallIcon(MyResources.getDrawable(smallIconName, context))
      .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
      .setContentIntent(pendingIntent)
      .setOngoing(false)
      .setAutoCancel(true)
      .build();
    //notification.flags |= Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

    return notification;
  }

  public static NotificationCompat.Builder createBuilder(Class mainActivityClass, Context context, String iconName, String smallIconName, String contentTextName, String title, String ticker)
  {
    Intent toLaunch = new Intent(context, mainActivityClass);
    toLaunch.setAction("android.intent.action.MAIN");
    toLaunch.addCategory("android.intent.category.LAUNCHER");
    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, toLaunch, VariousUtils.getFlag4UpdateCurrent());

    Bitmap icon = BitmapFactory.decodeResource(context.getResources(), MyResources.getDrawable(iconName, context));

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
      .setContentTitle(title)
      .setTicker(ticker)
      .setContentText(contentTextName)
      .setSmallIcon(MyResources.getDrawable(smallIconName, context))
      .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
      .setContentIntent(pendingIntent)
      .setOngoing(false)
      .setAutoCancel(true);

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
    {
      notificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(contentTextName)).build();
    }

    return notificationBuilder;
  }

  public static Notification create(Class mainActivityClass, Context context, String iconName, String smallIconName, String contentTextName, String tickerTextName)
  {
    String title = MyResources.getString("app_name", context);
    String ticker = MyResources.getString("app_name", context) + "\n" + MyResources.getString(tickerTextName, context);
    return create(mainActivityClass, context, iconName, smallIconName, contentTextName, title, ticker);
  }

  public static void playSound(Context context)
  {
    try
    {
      Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      Ringtone r = RingtoneManager.getRingtone(context, notification);
      r.play();
    }
    catch (Throwable t) { }
  }

  public static void show(Notification notification, Context context, int id, boolean playSound)
  {
    if(playSound)
    {
      playSound(context);
    }

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(id, notification);
  }

  public static void show(Notification notification, Context context, String tag, int id, boolean playSound)
  {
    if(playSound)
    {
      playSound(context);
    }

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(tag, id, notification);
  }

  public static void show(Notification notification, Context context, boolean playSound)
  {
    show(notification, context, "", 0, playSound);
  }

  public static void clean(Context context, String tag, int notificationId)
  {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(tag, notificationId);
  }

  public static void cleanAll(Context context)
  {
    String ns = Context.NOTIFICATION_SERVICE;
    NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
    nMgr.cancelAll();
  }

  public static void setChannel(String id, String name, int importance, boolean showBadge, boolean hasSound, Context context, NotificationCompat.Builder builder)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    {
      NotificationChannel channel = new NotificationChannel(id, name, importance);
      channel.setShowBadge(showBadge);
      if(!hasSound)
      {
        channel.setSound(null, null);
      }
      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.createNotificationChannel(channel);
      builder.setChannelId(channel.getId());
    }
  }
}
