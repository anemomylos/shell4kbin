package net.easyjoin.shell4kbin.bookmark;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import net.easyjoin.shell4kbin.activity.BookmarksActivity;
import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.ReplaceText;
import net.easyjoin.utils.ThemeUtils;
import net.easyjoin.utils.VariousUtils;

import java.util.List;

public final class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>
{
  private final String className = BookmarkAdapter.class.getName();
  private List<MyBookmark> bookmarkList;
  private BookmarksActivity bookmarksActivity;

  public BookmarkAdapter(List<MyBookmark> bookmarkList, BookmarksActivity bookmarksActivity)
  {
    this.bookmarkList = bookmarkList;
    this.bookmarksActivity = bookmarksActivity;
  }

  @Override
  public int getItemCount()
  {
    return bookmarkList.size();
  }

  @Override
  public BookmarkViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
  {
    View v = LayoutInflater.from(viewGroup.getContext()).inflate(MyResources.getLayout("bookmark_item", viewGroup.getContext()), viewGroup, false);
    return new BookmarkViewHolder(v);
  }

  @Override
  public void onBindViewHolder(BookmarkViewHolder bookmarkViewHolder, int i)
  {
    try
    {
      MyBookmark myBookmark = bookmarkList.get(i);
      bookmarkViewHolder.myBookmark = myBookmark;

      bookmarkViewHolder.bookmarkTitle.setText(myBookmark.getTitle());
      bookmarkViewHolder.bookmarkUrl.setText(myBookmark.getUrl());
    }
    catch (Throwable t)
    {
      MyLog.notification(className, "onBindViewHolder", bookmarksActivity, t);
    }
  }

  @SuppressLint("RestrictedApi")
  private void showOptions(View view, MyBookmark myBookmark, int index)
  {
    PopupMenu popupMenu = new PopupMenu(bookmarksActivity, view);
    MenuInflater inflater = popupMenu.getMenuInflater();
    inflater.inflate(MyResources.getMenu("bookmark_options_menu" , bookmarksActivity), popupMenu.getMenu());
    popupMenu.setOnMenuItemClickListener(new BookmarkMenuClickListener(myBookmark, index));

    MenuPopupHelper menuHelper = new MenuPopupHelper(bookmarksActivity, (MenuBuilder) popupMenu.getMenu(), view);
    menuHelper.setForceShowIcon(true);

    popupMenu.show();
    menuHelper.show();
  }

  class BookmarkMenuClickListener implements PopupMenu.OnMenuItemClickListener
  {
    private MyBookmark myBookmark;
    private int index;

    public BookmarkMenuClickListener(MyBookmark myBookmark, int index)
    {
      this.myBookmark = myBookmark;
      this.index = index;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem)
    {
      if(menuItem.getItemId() == MyResources.getId("shareBookmark", bookmarksActivity))
      {
        share(myBookmark);
      }
      else if(menuItem.getItemId() == MyResources.getId("deleteBookmark", bookmarksActivity))
      {
        delete(myBookmark, index);
      }
      else
      {
        return false;
      }

      return true;
    }
  }

  private void share(MyBookmark myBookmark)
  {
    VariousUtils.shareText(myBookmark.getUrl(), bookmarksActivity);
  }

  private void delete(MyBookmark myBookmark, int index)
  {
    String msg = MyResources.getString("delete_bookmark_question", bookmarksActivity);
    msg = ReplaceText.replace(msg, "$1", myBookmark.getTitle());
    AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(bookmarksActivity, ThemeUtils.getAlertTheme(ThemeUtils.useBlackTheme(bookmarksActivity)))
      .setMessage(msg)
      .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int which)
        {
          notifyItemRemoved(index);
          bookmarksActivity.remove(index);
        }
      })
      .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int which)
        {
        }
      })
      .setIcon(android.R.drawable.ic_dialog_info);

    AlertDialog dlg = dlgBuilder.create();
    dlg.show();
  }

  class BookmarkViewHolder extends RecyclerView.ViewHolder
  {
    MyBookmark myBookmark;
    View itemView;
    ViewGroup bookmarkContainer;
    TextView bookmarkTitle;
    TextView bookmarkUrl;
    View bookmarkMoreOptions;


    BookmarkViewHolder(final View itemView)
    {
      super(itemView);

      this.itemView = itemView;

      bookmarkContainer = itemView.findViewById(MyResources.getId("bookmarkContainer", bookmarksActivity));
      bookmarkTitle = itemView.findViewById(MyResources.getId("bookmarkTitle", bookmarksActivity));
      bookmarkUrl = itemView.findViewById(MyResources.getId("bookmarkUrl", bookmarksActivity));
      bookmarkMoreOptions = itemView.findViewById(MyResources.getId("bookmarkMoreOptions", bookmarksActivity));

      bookmarkContainer.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
          bookmarksActivity.returnUrl(myBookmark.getUrl());
        }
      });

      bookmarkMoreOptions.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
          showOptions(itemView, myBookmark, getAbsoluteAdapterPosition());
        }
      });
    }
  }
}
