package net.easyjoin.shell4kbin.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

import net.easyjoin.shell4kbin.browser.BrowserModel;
import net.easyjoin.shell4kbin.start.Startup;
import net.easyjoin.shell4kbin.utils.Constants;
import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.ThemeUtils;
import net.easyjoin.utils.TopExceptionHandler;
import net.easyjoin.utils.VariousUtils;

public class MainActivity extends AppCompatActivity
{

  private final String className = getClass().getName();
  private BrowserModel browserModel;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);

      Startup.getInstance().init(this);

      Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

      ThemeUtils.setTheme(this);

      MyLog.setAppName(MyResources.getString("app_name", this));

      setContentView(MyResources.getLayout("activity_main", this));

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
                browserModel = new BrowserModel("webView", activity);
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

      if("0".equals(VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.requestedPermissionPostNotificationsKey, "0", this)))
      {
        VariousUtils.requestPermission("android.permission.POST_NOTIFICATIONS", this);
        VariousUtils.savePreference(Constants.sharedPreferencesName, Constants.requestedPermissionPostNotificationsKey, "1", this);
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "onCreate", t);
      MyLog.notification(className, "onCreate", this, t);
      finish();
    }
  }

  @Override
  protected void onActivityResult (int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);

    if(resultCode == Activity.RESULT_OK)
    {
      if (requestCode == Constants.bookmarkUrlRequestCode)
      {
        String url = data.getStringExtra(Constants.bookmarkUrlKey);
        if(browserModel != null)
        {
          browserModel.loadUrl(url);
        }
      }
    }
  }


  @Override
  protected void onStart()
  {
    super.onStart();
  }

  @Override
  protected void onRestart()
  {
    super.onRestart();
  }

  @Override
  protected void onResume()
  {
    super.onResume();
  }

  @Override
  protected void onPause()
  {
    super.onPause();
  }

  @Override
  protected void onStop()
  {
    super.onStop();
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig)
  {
    super.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if (keyCode == KeyEvent.KEYCODE_BACK)
    {
      if ((browserModel != null) && (browserModel.backButtonPressed(keyCode, event)))
      {
        return true;
      }
    }

    return super.onKeyDown(keyCode, event);
  }
}