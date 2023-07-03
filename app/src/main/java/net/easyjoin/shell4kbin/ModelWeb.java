package net.easyjoin.shell4kbin;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;

import net.easyjoin.utils.Constants;
import net.easyjoin.utils.Miscellaneous;
import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.VariousUtils;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ModelWeb implements PopupMenu.OnMenuItemClickListener
{
  private final String className = getClass().getName();
  private List<String> pagesStack;
  private int currentPageIndex = -1;
  private Activity activity;
  public String initialUrl;
  private String webLayoutName;
  private String webViewName;
  private String loadingProgressName;
  private boolean isNormalView;
  private boolean isDesktopView;
  private WebView webView;
  private ProgressBar loadingProgress;
  private TextView pageTitle;
  private ImageButton arrowBackButton;
  private ImageButton arrowForwardButton;
  private ImageButton refreshButton;
  private ImageButton menuButton;
  private PopupMenu browserMenu;
  private MyJavascript myJavascript;
  private String profileName;
  private final String profileContainer = "a class=\\\"login\\\" href=\\\"/u/";

  public ModelWeb(String webViewName, Activity activity, String loadingProgressName, boolean isNormalView, boolean isDesktopView)
  {
    this.activity = activity;
    this.webViewName = webViewName;
    this.loadingProgressName = loadingProgressName;
    this.isNormalView = isNormalView;
    this.isDesktopView = isDesktopView;
    pagesStack = new ArrayList<>();

    initLayout();
    initWebView();

    profileName = VariousUtils.readPreference(Constants.profileNameKey, "", activity);

    String initialPage = "https://kbin.social/";
    if(!Miscellaneous.isEmpty(profileName))
    {
      initialPage = "https://kbin.social/sub";
      createBrowserMenu();
    }

    loadUrl(initialPage);

    String injectJSSwitch = VariousUtils.readPreference(Constants.injectJSKey, "0", activity);
    if("1".equals(injectJSSwitch))
    {
      String js2Inject = VariousUtils.readPreference(Constants.injectJSTextKey, "", activity);
      VariousUtils.setJS2Inject(js2Inject);
    }
  }

  public ModelWeb(String webViewName, Activity activity)
  {
    this(webViewName, activity, null, false, false);
  }

  private void initLayout()
  {
    webView = activity.findViewById(MyResources.getId(webViewName, activity));
    if(loadingProgressName != null)
    {
      loadingProgress = activity.findViewById(MyResources.getId(loadingProgressName, activity));
      loadingProgress.getIndeterminateDrawable().setColorFilter(Color.parseColor(MyResources.getAttrValue("colorPrimary", activity)), android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    pageTitle = activity.findViewById(MyResources.getId("pageTitle", activity));
    arrowBackButton = activity.findViewById(MyResources.getId("arrowBackButton", activity));
    arrowForwardButton = activity.findViewById(MyResources.getId("arrowForwardButton", activity));
    refreshButton = activity.findViewById(MyResources.getId("refreshButton", activity));
    menuButton = activity.findViewById(MyResources.getId("menuButton", activity));

    createBrowserMenu();
    //keepMenuOpenOnSelection();

    menuButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        showBrowserMenu();
      }
    });

    refreshButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        refresh();
      }
    });

    arrowBackButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        showPreviousPage();
      }
    });

    arrowForwardButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        showNextPage();
      }
    });

    pageTitle.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v)
      {
        if(currentPageIndex > -1)
        {
          String currentUrl = pagesStack.get(currentPageIndex);
          ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
          ClipData clip = ClipData.newPlainText(MyResources.getString("app_name", activity), currentUrl);
          clipboard.setPrimaryClip(clip);
          Toast.makeText(activity, MyResources.getString("urls_saved_clipboard", activity) + "\n\n" + currentUrl, Toast.LENGTH_SHORT).show();
        }
        return true;
      }
    });
  }

  private void initWebView()
  {
    webView.setWebViewClient(new MyBrowser(this, webViewName));
    myJavascript = new MyJavascript();
    webView.addJavascriptInterface(myJavascript, "HTMLOUT");
    webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    webView.setScrollbarFadingEnabled(false);
    webView.setWebChromeClient(new MyWebChromeClient());

    WebSettings webSettings = webView.getSettings();
    webSettings.setJavaScriptEnabled(true);
    webSettings.setLoadsImagesAutomatically(true);
    webSettings.setDefaultTextEncodingName("utf-8");
    webSettings.setDomStorageEnabled(true);
    webSettings.setDatabaseEnabled(true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

    webSettings.setLoadWithOverviewMode(false);
    webSettings.setSupportZoom(true);
    webSettings.setBuiltInZoomControls(true);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
    {
      webSettings.setDisplayZoomControls(false);
    }

    if (isNormalView)
    {
      webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
    }
    else
    {
      webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
    {
      CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
    }

    if (isDesktopView)
    {
      String newUA = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0) Gecko/20100101 Firefox/50.0";
      webSettings.setUserAgentString(newUA);
    }

    String fontSize = VariousUtils.readPreference(Constants.fontSizeKey, "", activity);
    if(!Miscellaneous.isEmpty(fontSize))
    {
      try
      {
        int fontSizeInt = Integer.parseInt(fontSize);
        webSettings.setDefaultFontSize(fontSizeInt);
      }
      catch (NumberFormatException nfe){}
    }
  }

  public void set()
  {

  }

  public void pageLoadProgress(boolean isProgressVisible)
  {
    int visibility = View.GONE;
    if(isProgressVisible)
    {
      visibility = View.VISIBLE;
      ImageButton commentIcon = (ImageButton) activity.findViewById(MyResources.getId("commentButton", activity));
      if(commentIcon != null)
      {
        commentIcon.setVisibility(View.GONE);
      }
    }

    if(loadingProgressName != null)
    {
      loadingProgress.setVisibility(visibility);
    }
  }

  public void loadUrl(String url, Map<String, String> extraHeaders)
  {
    initialUrl = url;
    reset();

    if(extraHeaders != null)
    {
      webView.loadUrl(url, extraHeaders);
    }
    else
    {
      webView.loadUrl(url);
    }
  }

  public void loadUrl(String url)
  {
    loadUrl(url, null);
  }

  public void reload()
  {
    webView.reload();
    //MyInfo.showToastShort(MyResources.getString("action_refresh", activity));
  }

  public String getCurrentURL()
  {
    return webView.getUrl();
  }

  public String getCurrentURLTitle()
  {
    return webView.getTitle();
  }

  public void hide()
  {
    activity.findViewById(MyResources.getId("webLayout", activity)).setVisibility(View.GONE);
    reset();
  }

  private synchronized void reset()
  {
    try
    {
      webView.stopLoading();
      webView.loadUrl("about:blank");
      pageLoadProgress(false);
    }
    catch (Throwable t)
    {
      MyLog.e(className, "reset", t);
    }
  }

  public void clean()
  {
    reset();
    webView.clearCache(true);
    webView.clearFormData();
    webView.clearSslPreferences();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
    {
      CookieManager.getInstance().removeAllCookies(null);
      CookieManager.getInstance().flush();
    }
    else
    {
      CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(activity);
      cookieSyncMngr.startSync();
      CookieManager cookieManager=CookieManager.getInstance();
      cookieManager.removeAllCookie();
      cookieManager.removeSessionCookie();
      cookieSyncMngr.stopSync();
      cookieSyncMngr.sync();
    }
  }

  public void setPageTitle()
  {
    pageTitle.setText(webView.getTitle());
  }

  public void addNextPage(String url)
  {
    if(!"about:blank".equals(url))
    {
      if (currentPageIndex > -1)
      {
        if (!pagesStack.get(currentPageIndex).equals(url))
        {
          currentPageIndex++;
          pagesStack.add(currentPageIndex, url);

          if (pagesStack.size() > (currentPageIndex + 1))
          {
            pagesStack.subList(currentPageIndex + 1, pagesStack.size()).clear();
          }
        }
      }
      else
      {
        currentPageIndex++;
        pagesStack.add(url);
      }
    }

    setArrowsStatus();
  }

  public void showPreviousPage()
  {
    currentPageIndex--;
    if(currentPageIndex < 0)
    {
      currentPageIndex = 0;
    }

    webView.loadUrl(pagesStack.get(currentPageIndex));

    setArrowsStatus();
  }

  public void showNextPage()
  {
    currentPageIndex++;
    if(currentPageIndex >= pagesStack.size())
    {
      currentPageIndex = pagesStack.size() - 1;
    }

    webView.loadUrl(pagesStack.get(currentPageIndex));

    setArrowsStatus();
  }

  public void refresh()
  {
    if( (currentPageIndex > -1) && (currentPageIndex < pagesStack.size()) )
    {
      webView.loadUrl(pagesStack.get(currentPageIndex));
    }
  }

  private void setArrowsStatus()
  {
    arrowBackButton.setEnabled(currentPageIndex > 0);
    arrowForwardButton.setEnabled(currentPageIndex < (pagesStack.size() - 1));
    if(arrowBackButton.isEnabled())
    {
      arrowBackButton.setImageAlpha(255);
    }
    else
    {
      arrowBackButton.setImageAlpha(100);
    }
    if(arrowForwardButton.isEnabled())
    {
      arrowForwardButton.setImageAlpha(255);
    }
    else
    {
      arrowForwardButton.setImageAlpha(100);
    }
  }

  private void createBrowserMenu()
  {
    browserMenu = new PopupMenu(activity, menuButton);
    MenuInflater inflater = browserMenu.getMenuInflater();
    inflater.inflate(MyResources.getMenu("browser_menu", activity), browserMenu.getMenu());
    browserMenu.setOnMenuItemClickListener(this);

    boolean existProfile = !Miscellaneous.isEmpty(profileName);
    browserMenu.getMenu().findItem(MyResources.getId("subscribedPosts", activity)).setVisible(existProfile);
    browserMenu.getMenu().findItem(MyResources.getId("moderatedPosts", activity)).setVisible(existProfile);
    browserMenu.getMenu().findItem(MyResources.getId("moderatedMagazines", activity)).setVisible(existProfile);
  }

  private void keepMenuOpenOnSelection()
  {
    MenuItem smallerFont = browserMenu.getMenu().findItem(MyResources.getId("smallerFont", activity));
    MenuItem biggerFont = browserMenu.getMenu().findItem(MyResources.getId("biggerFont", activity));
    keepMenuOpenOnSelection(smallerFont);
    keepMenuOpenOnSelection(biggerFont);
  }

  private void keepMenuOpenOnSelection(MenuItem item)
  {
    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
    item.setActionView(new View(activity));
    item.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
    {
      @Override
      public boolean onMenuItemActionExpand(MenuItem item)
      {
        return false;
      }

      @Override
      public boolean onMenuItemActionCollapse(MenuItem item)
      {
        return false;
      }
    });
  }

  private void showBrowserMenu()
  {
    browserMenu.show();
  }

  @Override
  public boolean onMenuItemClick(MenuItem item)
  {
    if(item.getItemId() == MyResources.getId("moderatedPosts", activity))
    {
      webView.loadUrl("https://kbin.social/mod");
    }
    else if(item.getItemId() == MyResources.getId("subscribedPosts", activity))
    {
      webView.loadUrl("https://kbin.social/sub");
    }
    else if(item.getItemId() == MyResources.getId("topPosts", activity))
    {
      webView.loadUrl("https://kbin.social/top");
    }
    else if(item.getItemId() == MyResources.getId("moderatedMagazines", activity))
    {
      webView.loadUrl("https://kbin.social/u/" + profileName + "/moderated");
    }
    else if(item.getItemId() == MyResources.getId("settingInjectJS", activity))
    {
      Intent injectJSActivityIntent = new Intent(activity.getApplicationContext(), InjectJSActivity.class);
      injectJSActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      activity.getApplicationContext().startActivity(injectJSActivityIntent);
    }
    else if(item.getItemId() == MyResources.getId("smallerFont", activity))
    {
      keepMenuOpenOnSelection(item);
      WebSettings webSettings = webView.getSettings();
      int fontSize = webSettings.getDefaultFontSize() - 1;
      setFontSize(fontSize);
      return false;
    }
    else if(item.getItemId() == MyResources.getId("biggerFont", activity))
    {
      keepMenuOpenOnSelection(item);
      WebSettings webSettings = webView.getSettings();
      int fontSize = webSettings.getDefaultFontSize() + 1;
      setFontSize(fontSize);
      return false;
    }
    else
    {
      return false;
    }

    return true;
  }

  private void setFontSize(int fontSize)
  {
    WebSettings webSettings = webView.getSettings();
    webSettings.setDefaultFontSize(fontSize);
    VariousUtils.savePreference(Constants.fontSizeKey, "" + fontSize, activity);
  }

  public boolean backButtonPressed(int keyCode, KeyEvent event)
  {
    if(currentPageIndex > 0)
    {
      if ((event != null) && (event.isLongPress()))
      {
        if( (pagesStack.get(currentPageIndex).startsWith("https://kbin.social"))
          && !((pagesStack.size() > currentPageIndex) && (!pagesStack.get(pagesStack.size() - 1).startsWith("https://kbin.social"))) )
        {
          activity.finish();
        }
        else
        {
          for(int i = currentPageIndex; i>0; i--)
          {
            if(pagesStack.get(i).startsWith("https://kbin.social"))
            {
              if(i != currentPageIndex)
              {
                currentPageIndex = i;
                refresh();
              }
              break;
            }
          }
        }
      }
      else
      {
        showPreviousPage();
      }

      return true;
    }
    else
    {
      if ((event != null) && (event.isLongPress()))
      {
        activity.finish();
        return true;
      }
      return false;
    }
  }

  public void elaborateHtml()
  {
    String currentUrl = webView.getUrl();

    if (Miscellaneous.isEmpty(profileName))
    {
      if ((currentUrl != null) && (currentUrl.startsWith("https://kbin.social/")))
      {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
          webView.evaluateJavascript("(function(){return document.body.outerHTML})();",
            new ValueCallback<String>()
            {
              @Override
              public void onReceiveValue(String html)
              {
                try
                {
                  int index = html.indexOf(profileContainer);
                  if (index != -1)
                  {
                    int index2 = html.indexOf("\\\"", index + profileContainer.length());
                    profileName = html.substring(index + profileContainer.length(), index2);

                    if (!Miscellaneous.isEmpty(profileName))
                    {
                      VariousUtils.savePreference(Constants.profileNameKey, profileName, activity);

                      createBrowserMenu();
                    }
                  }
                }
                catch (Throwable t)
                {
                  MyLog.e(className, "elaborateHtml", t);
                }
              }
            });
        }
      }
    }
    else if ((currentUrl != null) && (currentUrl.startsWith("https://kbin.social/login")))
    {
      VariousUtils.deletePreference(Constants.profileNameKey, activity);
    }
  }

  public void inject()
  {
    if(!Miscellaneous.isEmpty(VariousUtils.getJS2Inject()))
    {
      injectJS();
    }
  }

  private void injectJS()
  {
    InputStream inputStream = null;
    try
    {
      webView.loadUrl("javascript:(function() {" +
        "var parent = document.getElementsByTagName('head').item(0);" +
        "var script = document.createElement('script');" +
        "script.type = 'text/javascript';" +
        "script.innerHTML = decodeURIComponent(window.atob('" + VariousUtils.getJS2Inject() + "'));" +
        "parent.appendChild(script)" +
        "})()");
    }
    catch (Throwable t)
    {
      MyLog.e(className, "injectJS", t);
    }
    finally
    {
      if(inputStream != null)
      {
        try { inputStream.close(); } catch (Throwable t) {}
      }
    }
  }
}

