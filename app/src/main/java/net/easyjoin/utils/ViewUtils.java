package net.easyjoin.utils;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.graphics.drawable.DrawableCompat;

public final class ViewUtils
{
  public static void showAnimated(View myView)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      int cx = myView.getWidth() / 2;
      int cy = myView.getHeight() / 2;
      float finalRadius = (float) Math.hypot(cx, cy);
      Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
      myView.setVisibility(View.VISIBLE);
      anim.start();
    }
    else
    {
      myView.setVisibility(View.VISIBLE);
    }
  }

  public static void hideAnimated(final View myView, final boolean setGone)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      int cx = myView.getWidth() / 2;
      int cy = myView.getHeight() / 2;

      float initialRadius = (float) Math.hypot(cx, cy);

      Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

      anim.addListener(new AnimatorListenerAdapter()
      {
        @Override
        public void onAnimationEnd(Animator animation)
        {
          super.onAnimationEnd(animation);
          if(setGone)
          {
            myView.setVisibility(View.GONE);
          }
          else
          {
            myView.setVisibility(View.INVISIBLE);
          }
        }
      });

      anim.start();
    }
    else
    {
      if(setGone)
      {
        myView.setVisibility(View.GONE);
      }
      else
      {
        myView.setVisibility(View.INVISIBLE);
      }
    }
  }

  public static void hideAnimated(final View myView)
  {
    hideAnimated(myView, false);
  }

  public static void goneAnimated(final View myView)
  {
    hideAnimated(myView, true);
  }

  public static int getScreenWidth(Activity activity)
  {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    return displayMetrics.widthPixels;
  }

  public static int getScreenHeight(Activity activity)
  {
    DisplayMetrics displayMetrics = new DisplayMetrics();
    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    return displayMetrics.heightPixels;
  }

  public static int getScreenOrientation(Activity activity)
  {
    return activity.getResources().getConfiguration().orientation;
  }

  public static boolean isPortrait(Activity activity)
  {
    return (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
  }

  public static boolean isLandscape(Activity activity)
  {
    return (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
  }

  public static void setNotificationBarBackground(Activity activity)
  {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      Window window = activity.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      TypedValue typedValue = new TypedValue();
      Resources.Theme theme = activity.getTheme();
      theme.resolveAttribute(MyResources.getAttr("colorPrimary", activity), typedValue, true);
      int background = typedValue.data;
      window.setStatusBarColor(background);
    }
  }

  public static int dp2Pixel(int dp, Context context)
  {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
  }

  public static float getScreenWidthDP(Context context)
  {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return displayMetrics.widthPixels / displayMetrics.density;
  }

  public static int getConfigurationScreenWidthDP(Context context)
  {
    Configuration configuration = context.getResources().getConfiguration();
    return configuration.screenWidthDp;
  }

  public static int getConfigurationScreenWidthSmallestDP(Context context)
  {
    Configuration configuration = context.getResources().getConfiguration();
    return configuration.smallestScreenWidthDp;
  }

  public static float getScreenHeightDP(Context context)
  {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return displayMetrics.heightPixels / displayMetrics.density;
  }

  public static int getConfigurationScreenHeightDP(Context context)
  {
    Configuration configuration = context.getResources().getConfiguration();
    return configuration.screenHeightDp;
  }

  public static Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight)
  {
    Bitmap resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

    float scaleX = newWidth / (float) bitmap.getWidth();
    float scaleY = newHeight / (float) bitmap.getHeight();
    float pivotX = 0;
    float pivotY = 0;

    Matrix scaleMatrix = new Matrix();
    scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

    Canvas canvas = new Canvas(resizedBitmap);
    canvas.setMatrix(scaleMatrix);
    canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

    return resizedBitmap;
  }

  public static Bitmap getResizedBitmap2(Bitmap bitmap, int newWidth, int newHeight)
  {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();
    float scaleWidth = ((float) newWidth) / width;
    float scaleHeight = ((float) newHeight) / height;

    Matrix matrix = new Matrix();
    matrix.postScale(scaleWidth, scaleHeight);

    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

    return resizedBitmap;
  }

  public static void viewClicked(final View view, final Activity activity)
  {
    new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        activity.runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            view.setBackgroundColor(Color.GREEN);
          }
        });

        try{
          Thread.sleep(100);}catch (Throwable t){}

        activity.runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            view.setBackgroundColor(Color.TRANSPARENT);
          }
        });
      }
    }).start();
  }

  public static void viewClickedAlpha(final View view, long delay, final Activity activity)
  {
    new Thread(new Runnable()
    {
      @Override
      public void run()
      {
        activity.runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            view.setAlpha(0.54f);
          }
        });

        try{
          Thread.sleep(100);}catch (Throwable t){}

        activity.runOnUiThread(new Runnable()
        {
          @Override
          public void run()
          {
            view.setAlpha(1f);
          }
        });
      }
    }).start();
  }

  public static void setMenuItemIconTint(Menu menu, String item, int color, Context context)
  {
    try
    {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
      {
        MenuItem menuItem = menu.findItem(MyResources.getId(item, context));
        if (menuItem != null)
        {
          Drawable drawable = menuItem.getIcon();
          if (drawable != null)
          {
            final Drawable wrapped = DrawableCompat.wrap(drawable);
            drawable.mutate();
            DrawableCompat.setTint(wrapped, color);
            menuItem.setIcon(drawable);
          }
        }
      }
    }
    catch (Throwable t){}
  }

  public static void animateAlpha(float alpha, long duration, View view)
  {
    view.animate().alpha(alpha).setDuration(duration).setListener(null);
  }

  private static int getGridColumns(int itemWidthPixels, int occupiedVerticalWidth, Activity activity)
  {
    int numberOfColumns = (getScreenWidth(activity) - occupiedVerticalWidth) / dp2Pixel(itemWidthPixels, activity);
    numberOfColumns -= 1;
    if(ViewUtils.isLandscape(activity))
    {
      numberOfColumns -= 1;
    }
    return numberOfColumns;
  }

  public static int getGridColumns(int itemWidthPixels, int occupiedVerticalWidth, Activity activity, int defaultValue)
  {
    int numberOfColumns = -1;

    try
    {
      numberOfColumns = getGridColumns(itemWidthPixels, occupiedVerticalWidth, activity);
    }
    catch (Throwable t)
    {
      MyLog.e(ViewUtils.class.getName(), "getGridColumns", t);
    }
    finally
    {
      if(numberOfColumns == -1)
      {
        numberOfColumns = defaultValue;
      }
    }

    return numberOfColumns;
  }
}
