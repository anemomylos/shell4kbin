package net.easyjoin.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.TypedValue;

import java.io.File;
import java.io.FileOutputStream;

public final class MyResources
{
  public static String getString(String name, Context context)
  {
    int identifier = context.getResources().getIdentifier(name, "string", context.getPackageName());
    if(identifier > 0)
    {
      return context.getResources().getString(identifier);
    }
    else
    {
      return null;
    }
  }

  public static int getId(String name, Context context)
  {
    return context.getResources().getIdentifier(name, "id", context.getPackageName());
  }

  public static int getMenu(String name, Context context)
  {
    return context.getResources().getIdentifier(name, "menu", context.getPackageName());
  }

  public static int getDrawable(String name, Context context)
  {
    return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
  }

  public static int getRaw(String name, Context context)
  {
    return context.getResources().getIdentifier(name, "raw", context.getPackageName());
  }

  public static int getTheme(String name, Context context)
  {
    return context.getResources().getIdentifier(name, "style", context.getPackageName());
  }

  public static int getAttr(String name, Context context)
  {
    return context.getResources().getIdentifier(name, "attr", context.getPackageName());
  }

  public static String getAttrValue(String name, Context context)
  {
    TypedValue typedValue = new TypedValue();
    context.getTheme().resolveAttribute(MyResources.getAttr(name, context), typedValue, true);
    return typedValue.coerceToString().toString();
  }

  public static int getAnim(String name, Context context)
  {
    return context.getResources().getIdentifier(name, "anim", context.getPackageName());
  }

  public static int getLayout(String name, Context context)
  {
    return context.getResources().getIdentifier(name, "layout", context.getPackageName());
  }

  public static File drawable2File(String drawableName, String ext, boolean recreate, Context context)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    {
      return drawable2TempFile(drawableName, ext, true, context);
    }
    else
    {
      return drawable2InternalFile(drawableName, ext, recreate, context);
    }
  }

  public static File drawable2TempFile(String drawableName, String ext, boolean recreate, Context context)
  {
    File outputFile = null;
    try
    {
      String fileName = drawableName + "." + ext;
      outputFile = context.getFileStreamPath(fileName);

      if( (!recreate) && (outputFile.exists()) )
      {
        return outputFile;
      }
      else if(outputFile.exists())
      {
        outputFile.delete();
      }

      Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), getDrawable(drawableName, context));

      FileOutputStream fos = null;

      try
      {
        fos = FileUtils.getFileOutputStream(fileName, false, context);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      }
      finally
      {
        if(fos != null)
        {
          fos.close();
        }
      }
    }
    catch (Throwable t)
    {
      MyLog.e(MyResources.class.getName(), "drawable2TempFile", t);
    }

    return outputFile;
  }

  public static File drawable2InternalFile(String drawableName, String ext, boolean recreate, Context context)
  {
    File outputFile = null;
    try
    {
      String fileName = drawableName + "." + ext;
      outputFile = context.getFileStreamPath(fileName);

      if( (!recreate) && (outputFile.exists()) )
      {
        return outputFile;
      }
      else if(outputFile.exists())
      {
        outputFile.delete();
      }

      Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), getDrawable(drawableName, context));

      FileOutputStream fos = null;

      try
      {
        fos = FileUtils.getFileOutputStream(fileName, false, context);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      }
      finally
      {
        if(fos != null)
        {
          fos.close();
        }
      }
    }
    catch (Throwable t)
    {
      MyLog.e(MyResources.class.getName(), "drawable2InternalFile", t);
    }

    return outputFile;
  }
}
