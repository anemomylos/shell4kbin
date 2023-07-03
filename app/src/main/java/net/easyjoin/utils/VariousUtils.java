package net.easyjoin.utils;


import android.app.PendingIntent;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Base64;


public final class VariousUtils
{
  private static final  String className = VariousUtils.class.getName();
  private static final String sharedPreferencesName = "net.easyjoin.shell4kbin";

  private static int flag4UpdateCurrent = PendingIntent.FLAG_UPDATE_CURRENT;
  private static String js2Inject;

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

  private static SharedPreferences getSharedPreferences(Context context)
  {
    return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
  }

  public static String readPreference(String key, String defaultValue, Context context)
  {
    SharedPreferences sharedPreferences = getSharedPreferences(context);
    return sharedPreferences.getString(key, defaultValue);
  }

  public static void savePreference(String key, String value, Context context)
  {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putString(key, value);
    editor.apply();
  }

  public static void deletePreference(String key, Context context)
  {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.remove(key);
    editor.apply();
  }

  public static String getJS2Inject()
  {
    return js2Inject;
  }

  public static void setJS2Inject(String js)
  {
    if(js != null)
    {
      js2Inject = Base64.encodeToString(js.getBytes(), Base64.NO_WRAP);
    }
  }
}
