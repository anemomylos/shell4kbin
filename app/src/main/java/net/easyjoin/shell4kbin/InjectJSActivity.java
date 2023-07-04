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

public final class InjectJSActivity extends AppCompatActivity
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

      setContentView(MyResources.getLayout("activity_inject_js", this));

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

    SwitchCompat injectJSSwitch = findViewById(MyResources.getId("injectJSSwitch", this));
    injectJSSwitch.setChecked(VariousUtils.readPreference(Constants.injectJSKey, "0", this).equals("1"));
    injectJSSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
        VariousUtils.savePreference(Constants.injectJSKey, isChecked ? "1" : "0", activity);
        if(isChecked)
        {
          String jsText2Inject = VariousUtils.readPreference(Constants.injectJSTextKey, "", activity);
          if(Miscellaneous.isEmpty(jsText2Inject))
          {
            saveJSText();
          }
        }
        else
        {
          VariousUtils.setJS2Inject("");
        }
      }
    });

    String jsText2Inject = VariousUtils.readPreference(Constants.injectJSTextKey, "", this);
    if(Miscellaneous.isEmpty(jsText2Inject))
    {
      jsText2Inject = Constants.jsSource;
    }
    final AppCompatEditText jsText = findViewById(MyResources.getId("jsText", this));
    jsText.setText(jsText2Inject);

    AppCompatButton saveButton = findViewById(MyResources.getId("saveButton", this));
    saveButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View view)
      {
        saveJSText();
        finish();
      }
    });
  }

  private void saveJSText()
  {
    final AppCompatEditText jsText = findViewById(MyResources.getId("jsText", this));
    Editable text = jsText.getText();
    if(text != null)
    {
      String textValue =  String.valueOf(text);
      VariousUtils.setJS2Inject(textValue);
      VariousUtils.savePreference(Constants.injectJSTextKey,textValue, this);
    }
  }
}
