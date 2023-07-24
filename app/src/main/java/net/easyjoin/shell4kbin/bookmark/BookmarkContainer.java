package net.easyjoin.shell4kbin.bookmark;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.ArrayList;

@Keep
public final class BookmarkContainer implements Serializable
{
  private static final long serialVersionUID = 1L;
  private ArrayList<MyBookmark> bookmarkList;

  public ArrayList<MyBookmark> getBookmarkList()
  {
    return bookmarkList;
  }

  public void setBookmarkList(ArrayList<MyBookmark> bookmarkList)
  {
    this.bookmarkList = bookmarkList;
  }
}
