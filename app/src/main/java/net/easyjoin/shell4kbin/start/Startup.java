package net.easyjoin.shell4kbin.start;

import android.content.Context;

import net.easyjoin.shell4kbin.bookmark.BookmarkManager;
import net.easyjoin.utils.MyLog;

public final class Startup
{
  private final String className = Startup.class.getName();
  private boolean isInit = false;
  public static final Startup instance = new Startup();
  private final StringBuilder forSynchronize = new StringBuilder();
  private Context applicationContext;

  private Startup()
  {
  }

  public static Startup getInstance()
  {
    return instance;
  }

  public void init(Context context)
  {
    try
    {
      if (!isInit)
      {
        synchronized (forSynchronize)
        {
          if (!isInit)
          {
            applicationContext = context.getApplicationContext();

            BookmarkManager.getInstance().setContext(applicationContext);
          }
        }
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "init", t);
      MyLog.notification(className, "init", context, t);
    }
  }
}
