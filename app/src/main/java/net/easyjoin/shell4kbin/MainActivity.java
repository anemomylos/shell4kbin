package net.easyjoin.shell4kbin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.droidopoulos.utils.MyLog;
import net.droidopoulos.various.MyResources;

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
      setContentView(MyResources.getLayout("activity_main", this));

      modelWeb = new ModelWeb("webView", this);
      modelWeb.loadUrl("https://kbin.social/");
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
      if ((event != null) && (event.isLongPress()))
      {
        finish();
        return true;
      }
      else if ((modelWeb != null) && (modelWeb.backButtonPressed(keyCode, event)))
      {
        return true;
      }
    }

    return super.onKeyDown(keyCode, event);
  }
}