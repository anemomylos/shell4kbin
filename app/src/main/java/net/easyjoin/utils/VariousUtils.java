package net.easyjoin.utils;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;


public final class VariousUtils
{
  private static final String className = VariousUtils.class.getName();

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

  public static void requestPermission(String permission, Activity activity)
  {
    if(!hasPermission(permission, activity))
    {
      ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
    }
  }

  public static int getFlag4UpdateCurrent()
  {
    return flag4UpdateCurrent;
  }

  private static SharedPreferences getSharedPreferences(String sharedPreferencesName, Context context)
  {
    return context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
  }

  public static String readPreference(String sharedPreferencesName, String key, String defaultValue, Context context)
  {
    SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, context);
    return sharedPreferences.getString(key, defaultValue);
  }

  public static void savePreference(String sharedPreferencesName, String key, String value, Context context)
  {
    SharedPreferences.Editor editor = getSharedPreferences(sharedPreferencesName, context).edit();
    editor.putString(key, value);
    editor.apply();
  }

  public static void deletePreference(String sharedPreferencesName, String key, Context context)
  {
    SharedPreferences.Editor editor = getSharedPreferences(sharedPreferencesName, context).edit();
    editor.remove(key);
    editor.apply();
  }

  public static void openDialogActivity(Class className, Context context)
  {
    Intent activityIntent = new Intent(context.getApplicationContext(), className);
    activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.getApplicationContext().startActivity(activityIntent);
  }

  /**
  <queries>
    <intent>
      <action android:name="android.intent.action.VIEW" />
      <data android:scheme="http" />
    </intent>
  </queries>
   */
  public static boolean intentCanBeHandled(Intent intent, Context context)
  {
    boolean exist = false;

    try
    {
      PackageManager packageManager = context.getPackageManager();
      exist = (intent.resolveActivity(packageManager) != null);
    }
    catch (Throwable t)
    {
      MyLog.e(className, "intentCanBeHandled", t);
    }

    return exist;
  }
}
