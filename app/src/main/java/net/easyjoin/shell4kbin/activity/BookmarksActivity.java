package net.easyjoin.shell4kbin.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.easyjoin.shell4kbin.bookmark.BookmarkAdapter;
import net.easyjoin.shell4kbin.bookmark.BookmarkManager;
import net.easyjoin.shell4kbin.browser.BrowserModel;
import net.easyjoin.shell4kbin.utils.CachedValues;
import net.easyjoin.shell4kbin.utils.Constants;
import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.ThemeUtils;
import net.easyjoin.utils.TopExceptionHandler;
import net.easyjoin.utils.VariousUtils;

import java.util.ArrayList;

public final class BookmarksActivity extends AppCompatActivity
{
  private final String className = getClass().getName();
  private RecyclerView bookmarksRecycler;
  private View bookmarkListEmpty;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);

      Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

      ThemeUtils.setTheme4Popup(this);

      supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

      setContentView(MyResources.getLayout("activity_bookmarks", this));

      getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

      final Activity activity = this;
      new Thread(new Runnable() {
        @Override
        public void run()
        {
          activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
              try
              {
                setLayout();
              }
              catch (Throwable t)
              {
                MyLog.e(className, "onCreate.run", t);
                MyLog.notification(className, "onCreate", activity, t);
                activity.finish();
              }
            }
          });
        }
      }).start();
    }
    catch (Throwable t)
    {
      MyLog.e(className, "onCreate", t);
      MyLog.notification(className, "onCreate", this, t);
      finish();
    }
  }

  private void setLayout()
  {
    bookmarksRecycler = findViewById(MyResources.getId("bookmarksRecycler", this));
    BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(BookmarkManager.getInstance().get(), this);
    LinearLayoutManager llm = new LinearLayoutManager(this);
    bookmarksRecycler.setLayoutManager(llm);
    bookmarksRecycler.setAdapter(bookmarkAdapter);

    bookmarkListEmpty = findViewById(MyResources.getId("bookmarkListEmpty", this));

    viewWhenListIsEmpty();
  }

  public void returnUrl(String url)
  {
    Intent returnIntent = new Intent();
    returnIntent.putExtra(Constants.bookmarkUrlKey, url);
    setResult(Activity.RESULT_OK, returnIntent);
    finish();
  }

  public void remove(int index)
  {
    BookmarkManager.getInstance().remove(index);
    viewWhenListIsEmpty();
  }

  private void viewWhenListIsEmpty()
  {
    if(BookmarkManager.getInstance().get().size() > 0)
    {
      bookmarkListEmpty.setVisibility(View.GONE);
      bookmarksRecycler.setVisibility(View.VISIBLE);
    }
    else
    {
      bookmarksRecycler.setVisibility(View.GONE);
      bookmarkListEmpty.setVisibility(View.VISIBLE);
    }
  }
}
