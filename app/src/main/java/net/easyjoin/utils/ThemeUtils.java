package net.easyjoin.utils;


import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

public final class ThemeUtils
{
  public static final String lightThemeId = "Purple";
  public static final String darkThemeId = "Black_Purple";
  private static final String lightThemePopupId = "Purple_Popup";
  private static final String darkThemePopupId = "Black_Purple_Popup";
  private static int previousUUIMode = -100;


  private static boolean useBlackTheme(Context context)
  {
    boolean useBlackTheme = false;

    if(previousUUIMode == -100)
    {
      previousUUIMode = context.getResources().getConfiguration().uiMode;
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    {
      if( (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
      {
        useBlackTheme = true;
      }
    }

    return useBlackTheme;
  }

  public static void setTheme(Context context)
  {
    String themeId2Use = lightThemeId;

    if(useBlackTheme(context))
    {
      themeId2Use = darkThemeId;
    }

    context.setTheme(MyResources.getTheme(themeId2Use, context));
  }

  public static void setTheme4Popup(Context context)
  {
    String themeId2Use = lightThemePopupId;

    if(useBlackTheme(context))
    {
      themeId2Use = darkThemePopupId;
    }

    context.setTheme(MyResources.getTheme(themeId2Use, context));
  }

  public static int getAlertTheme(boolean useBlackTheme)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      if (useBlackTheme)
      {
        return android.R.style.Theme_Material_Dialog_Alert;
      }
      else
      {
        return android.R.style.Theme_Material_Light_Dialog_Alert;
      }
    }
    else
    {
      if (useBlackTheme)
      {
        return AlertDialog.THEME_HOLO_DARK;
      }
      else
      {
        return AlertDialog.THEME_HOLO_LIGHT;
      }
    }
  }

  public static int getAlertTheme(Context context)
  {
    return getAlertTheme(useBlackTheme(context));
  }
}
