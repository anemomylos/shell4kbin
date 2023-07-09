package net.easyjoin.shell4kbin.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.SwitchCompat;

import net.easyjoin.shell4kbin.utils.CachedValues;
import net.easyjoin.shell4kbin.utils.Constants;
import net.easyjoin.utils.Miscellaneous;
import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.ThemeUtils;
import net.easyjoin.utils.TopExceptionHandler;
import net.easyjoin.utils.VariousUtils;

public final class SettingsActivity extends AppCompatActivity
{
  private final String className = getClass().getName();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);

      Thread.setDefaultUncaughtExceptionHandler(new TopExceptionHandler(this));

      ThemeUtils.setTheme4Popup(this);

      supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

      setContentView(MyResources.getLayout("activity_settings", this));

      getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

      setLayout();
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
    final Activity activity = this;

    SwitchCompat externalLinksDefaultBrowserSwitch = findViewById(MyResources.getId("externalLinksDefaultBrowserSwitch", this));
    externalLinksDefaultBrowserSwitch.setChecked(VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.externalLinksDefaultBrowserKey, "0", this).equals("1"));
    externalLinksDefaultBrowserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        VariousUtils.savePreference(Constants.sharedPreferencesName, Constants.externalLinksDefaultBrowserKey, isChecked ? "1" : "0", activity);
        CachedValues.setExternalLinksDefaultBrowser(isChecked);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://easyjoin.net"));
        CachedValues.setBrowserIntentCanBeHandled(VariousUtils.intentCanBeHandled(browserIntent, activity));
      }
    });
  }
}
