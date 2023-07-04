package net.easyjoin.shell4kbin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.ThemeUtils;

public class MainActivity extends AppCompatActivity
{

  private final String className = getClass().getName();
  private ModelWeb modelWeb;


  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);

      ThemeUtils.setTheme(this);

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
              modelWeb = new ModelWeb("webView", activity);
            }
          });
        }
      }).start();
    }
    catch (Throwable t)
    {
      MyLog.e(className, "onCreate", t);
      MyLog.notification(className, "onCreate", this, t);
      throw t;
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
      if ((modelWeb != null) && (modelWeb.backButtonPressed(keyCode, event)))
      {
        return true;
      }
    }

    return super.onKeyDown(keyCode, event);
  }
}