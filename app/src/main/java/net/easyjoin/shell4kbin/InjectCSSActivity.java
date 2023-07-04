package net.easyjoin.shell4kbin;

import android.app.Activity;
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

import net.easyjoin.utils.Constants;
import net.easyjoin.utils.Miscellaneous;
import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.ThemeUtils;
import net.easyjoin.utils.VariousUtils;

public final class InjectCSSActivity extends AppCompatActivity
{
  private final String className = getClass().getName();

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    try
    {
      super.onCreate(savedInstanceState);

      ThemeUtils.setTheme4Popup(this);

      supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

      setContentView(MyResources.getLayout("activity_inject_css", this));

      getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

      setLayout();
    }
    catch (Throwable t)
    {
      MyLog.e(className, "onCreate", t);
      finish();
    }
  }

  private void setLayout()
  {
    final Activity activity = this;

    SwitchCompat injectCSSSwitch = findViewById(MyResources.getId("injectCSSSwitch", this));
    injectCSSSwitch.setChecked(VariousUtils.readPreference(Constants.injectCSSKey, "0", this).equals("1"));
    injectCSSSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        VariousUtils.savePreference(Constants.injectCSSKey, isChecked ? "1" : "0", activity);
        if(isChecked)
        {
          String cssText2Inject = VariousUtils.readPreference(Constants.injectCSSTextKey, "", activity);
          if(Miscellaneous.isEmpty(cssText2Inject))
          {
            saveCSSText();
          }
        }
        else
        {
          VariousUtils.setCSS2Inject("");
        }
      }
    });

    String cssText2Inject = VariousUtils.readPreference(Constants.injectCSSTextKey, "", this);
    if(Miscellaneous.isEmpty(cssText2Inject))
    {
      cssText2Inject = Constants.cssSource;
    }
    final AppCompatEditText cssText = findViewById(MyResources.getId("cssText", this));
    cssText.setText(cssText2Inject);

    AppCompatButton saveButton = findViewById(MyResources.getId("saveButton", this));
    saveButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        saveCSSText();
        finish();
      }
    });
  }

  private void saveCSSText()
  {
    final AppCompatEditText cssText = findViewById(MyResources.getId("cssText", this));
    Editable text = cssText.getText();
    if(text != null)
    {
      String textValue =  String.valueOf(text);
      VariousUtils.setCSS2Inject(textValue);
      VariousUtils.savePreference(Constants.injectCSSTextKey,textValue, this);
    }
  }
}
