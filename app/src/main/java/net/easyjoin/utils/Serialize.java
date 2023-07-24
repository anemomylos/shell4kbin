package net.easyjoin.utils;


import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public final class Serialize
{
  private static final String className = Serialize.class.getName();
  private static final StringBuilder forSynchronize = new StringBuilder(0);
  private static final String BACK_EXT = ".bak";

  public static boolean save(Object saveThis, String serializeFileName, String path, boolean createBackup, Context context)
  {
    synchronized (forSynchronize)
    {
      boolean result = false;

      try
      {
        if(createBackup)
        {
          saveIt(saveThis, serializeFileName + BACK_EXT, path, context);
        }
        result = saveIt(saveThis, serializeFileName, path, context);
      }
      catch (Throwable t)
      {
        MyLog.e(className, "save", t);
      }

      return result;
    }
  }

  public static boolean save(Object saveThis, String serializeFileName, String path, Context context)
  {
    return save(saveThis, serializeFileName, path, true, context);
  }

  private static boolean saveIt(Object saveThis, String serializeFileName, String path, Context context)
  {
    boolean result = false;

    FileOutputStream fos = null;
    ObjectOutputStream oos = null;

    try
    {
      if (saveThis != null)
      {
        if(path == null)
        {
          fos = context.openFileOutput(serializeFileName, Context.MODE_PRIVATE);
        }
        else
        {
          File file = new File(path, serializeFileName);
          fos = new FileOutputStream(file);
        }
        oos = new ObjectOutputStream(fos);
        oos.writeObject(saveThis);

        result = true;
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "saveIt", t);
    }
    finally
    {
      if (oos != null)
      {
        try
        {
          oos.close();
        }
        catch (Throwable t)
        {
        }
      }
      if (fos != null)
      {
        try
        {
          fos.close();
        }
        catch (Throwable t)
        {
        }
      }
    }

    return result;
  }

  public static Object read(String serializeFileName, String path, Context context)
  {
    synchronized (forSynchronize)
    {
      Object readThis = null;

      try
      {
        readThis = readIt(serializeFileName, path, context);
        if(readThis == null)
        {
          readThis = readIt(serializeFileName + BACK_EXT, path, context);
        }
      }
      catch (Throwable t)
      {
        MyLog.e(className, "read", t);
      }

      return readThis;
    }
  }

  private static Object readIt(String serializeFileName, String path, Context context)
  {
    FileInputStream fis = null;
    ObjectInputStream ois = null;
    Object readThis = null;

    try
    {
      File file = null;

      if(path == null)
      {
        file = context.getFileStreamPath(serializeFileName);
      }
      else
      {
        file = new File(path, serializeFileName);
      }

      if (file != null && file.exists())
      {
        if (path == null)
        {
          fis = context.openFileInput(serializeFileName);
        }
        else
        {
          fis = new FileInputStream(file);
        }
      }

      if(fis != null)
      {
        ois = new ObjectInputStream(fis);
        readThis = ois.readObject();
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "readIt", t);
    }
    finally
    {
      if (ois != null)
      {
        try
        {
          ois.close();
        }
        catch (Throwable t)
        {
        }
      }
      if (fis != null)
      {
        try
        {
          fis.close();
        }
        catch (Throwable t)
        {
        }
      }
    }

    return readThis;
  }

  public static boolean delete(String serializeFileName, String path, Context context)
  {
    synchronized (forSynchronize)
    {
      boolean deleted = false;
      try
      {
        File file = getFile(serializeFileName + BACK_EXT, path, context);

        if (file != null && (file.exists()))
        {
          deleted = file.delete();
        }

        file = getFile(serializeFileName, path, context);

        if (file != null && (file.exists()))
        {
          deleted = file.delete();
        }
      }
      catch (Throwable t)
      {
        MyLog.e(className, "delete", t);
      }

      return deleted;
    }
  }

  public static boolean exist(String serializeFileName, String path, Context context)
  {
    synchronized (forSynchronize)
    {
      boolean exist = false;
      try
      {
        File file = getFile(serializeFileName, path, context);

        if (file != null && (file.exists()))
        {
          exist = true;
        }
      }
      catch (Throwable t)
      {
        MyLog.e(className, "exist", t);
      }

      return exist;
    }
  }

  private static File getFile(String serializeFileName, String path, Context context)
  {
    File file = null;

    if(path == null)
    {
      file = context.getFileStreamPath(serializeFileName);
    }
    else
    {
      file = new File(path, serializeFileName);
    }

    return file;
  }
}
