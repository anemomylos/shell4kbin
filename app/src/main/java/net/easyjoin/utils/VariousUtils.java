package net.easyjoin.utils;


import android.app.PendingIntent;
import android.content.Context;

import android.content.pm.PackageManager;
import android.os.Build;


public final class VariousUtils
{
  private static final  String className = VariousUtils.class.getName();
  private static int flag4UpdateCurrent = PendingIntent.FLAG_UPDATE_CURRENT;

  static
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
    {
      flag4UpdateCurrent = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
    }
  }

  public static boolean hasPermission(String permission, Context context)
  {
    int res = context.checkCallingOrSelfPermission(permission);
    return (res == PackageManager.PERMISSION_GRANTED);
  }

  public static int getFlag4UpdateCurrent()
  {
    return flag4UpdateCurrent;
  }
}
