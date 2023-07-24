package net.easyjoin.shell4kbin.bookmark;

import android.content.Context;

import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.Serialize;

import java.util.ArrayList;

public final class BookmarkManager
{
  private final String className = BookmarkManager.class.getName();
  private static final BookmarkManager instance = new BookmarkManager();
  private boolean isInit = false;
  private final StringBuilder forSynchronize = new StringBuilder();
  private Context context;
  private BookmarkContainer bookmarkContainer;
  private ArrayList<MyBookmark> bookmarkList;
  public static final String bookmarksFilename = "bookmark_container";


  private BookmarkManager()
  {
  }

  public static BookmarkManager getInstance()
  {
    return instance;
  }

  private boolean waitInit()
  {
    if(!isInit)
    {
      int loops = 0;
      while (!isInit)
      {
        loops++;
        if (loops > 300) break;
        try {Thread.sleep(10);}catch (Throwable t) {}
      }
    }

    return isInit;
  }

  public void setContext(Context context)
  {
    if (bookmarkList == null)
    {
      synchronized (forSynchronize)
      {
        if (bookmarkList == null)
        {
          this.context = context.getApplicationContext();

          try
          {
            bookmarkContainer = getSaved();
          }
          catch (Throwable t)
          {
            MyLog.e(className, "setContext", t);
          }

          if (bookmarkContainer == null)
          {
            bookmarkContainer = new BookmarkContainer();
            bookmarkContainer.setBookmarkList(new ArrayList<>());
            save();
          }

          bookmarkList = bookmarkContainer.getBookmarkList();

          isInit = true;
        }
      }
    }
  }

  private BookmarkContainer getSaved()
  {
    return (BookmarkContainer) Serialize.read(bookmarksFilename, null, context);
  }

  private void save()
  {
    synchronized (forSynchronize)
    {
      if(bookmarkContainer != null)
      {
        Serialize.save(bookmarkContainer, bookmarksFilename, null, context);
      }
    }
  }

  public void add(MyBookmark myBookmark)
  {
    if(!waitInit()) return;

    bookmarkList.add(myBookmark);
    save();
  }

  public void remove(int index)
  {
    if(!waitInit()) return;

    synchronized (forSynchronize)
    {
      bookmarkList.remove(index);
      save();
    }
  }

  public ArrayList<MyBookmark> get()
  {
    return bookmarkList;
  }
}
