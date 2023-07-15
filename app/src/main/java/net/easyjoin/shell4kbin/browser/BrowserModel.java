package net.easyjoin.shell4kbin.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ShareCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.easyjoin.shell4kbin.activity.InjectCSSActivity;
import net.easyjoin.shell4kbin.activity.InjectJSActivity;
import net.easyjoin.shell4kbin.activity.SettingsActivity;
import net.easyjoin.shell4kbin.browser.MyJavascript;
import net.easyjoin.shell4kbin.browser.MyWebChromeClient;
import net.easyjoin.shell4kbin.browser.MyWebViewClient;
import net.easyjoin.shell4kbin.utils.CachedValues;
import net.easyjoin.shell4kbin.utils.Constants;
import net.easyjoin.utils.Miscellaneous;
import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.ThemeUtils;
import net.easyjoin.utils.VariousUtils;
import net.easyjoin.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public final class BrowserModel implements PopupMenu.OnMenuItemClickListener
{
  private final String className = getClass().getName();
  private List<String> pagesStack;
  private int currentPageIndex = -1;
  private Activity activity;
  public String initialUrl;
  private String webViewName;
  private String loadingProgressName;
  private boolean isNormalView;
  private boolean isDesktopView;
  private WebView webView;
  private ProgressBar progressBar;;
  private TextView pageTitle;
  private AppCompatEditText pageURL;
  private ImageButton arrowBackButton;
  private ImageButton arrowForwardButton;
  private ImageButton refreshButton;
  private ImageButton shareButton;
  private ImageButton menuButton;
  private ImageButton goURLButton;
  private PopupMenu browserMenu;
  private View titleContainer;
  private View urlContainer;
  private MyJavascript myJavascript;
  private MyWebChromeClient myWebChromeClient;
  private String profileName;
  private final String profileContainer = "span class=\\\"user-name\\\">";
  private String magazineNameInView;

  public BrowserModel(String webViewName, Activity activity, String loadingProgressName, boolean isNormalView, boolean isDesktopView)
  {
    this.activity = activity;
    this.webViewName = webViewName;
    this.loadingProgressName = loadingProgressName;
    this.isNormalView = isNormalView;
    this.isDesktopView = isDesktopView;
    pagesStack = new ArrayList<>();

    initLayout();
    initWebView();
    initInject();
    initSettings();

    profileName = VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.profileNameKey, "", activity);

    String initialPage = "https://kbin.social/hot";
    if(!Miscellaneous.isEmpty(profileName))
    {
      initialPage = "https://kbin.social/sub";
    }

    if(ThemeUtils.useBlackTheme(activity))
    {
      webView.setBackgroundColor(Color.BLACK);
    }

    //loadEmptyPage();
    loadUrl(initialPage);
  }

  public BrowserModel(String webViewName, Activity activity)
  {
    this(webViewName, activity, null, false, false);
  }

  @SuppressLint("ClickableViewAccessibility")
  private void initLayout()
  {
    webView = activity.findViewById(MyResources.getId(webViewName, activity));
    pageTitle = activity.findViewById(MyResources.getId("pageTitle", activity));
    pageURL = activity.findViewById(MyResources.getId("pageURL", activity));
    arrowBackButton = activity.findViewById(MyResources.getId("arrowBackButton", activity));
    arrowForwardButton = activity.findViewById(MyResources.getId("arrowForwardButton", activity));
    refreshButton = activity.findViewById(MyResources.getId("refreshButton", activity));
    shareButton = activity.findViewById(MyResources.getId("shareButton", activity));
    menuButton = activity.findViewById(MyResources.getId("menuButton", activity));
    titleContainer = activity.findViewById(MyResources.getId("titleContainer", activity));
    urlContainer = activity.findViewById(MyResources.getId("urlContainer", activity));
    goURLButton = activity.findViewById(MyResources.getId("goURLButton", activity));
    progressBar = activity.findViewById(MyResources.getId("progressLoad", activity));

    createBrowserMenu();
    //keepMenuOpenOnSelection();

    menuButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        showBrowserMenu();
      }
    });

    refreshButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        refresh();
      }
    });

    shareButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        shareURL();
      }
    });

    arrowBackButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        showPreviousPage();
      }
    });

    arrowForwardButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        showNextPage();
      }
    });

    /*GestureDetector pageTitleGestureDetector = new GestureDetector(activity, new GestureDetector.SimpleOnGestureListener()
    {
      @Override
      public boolean onDoubleTap(MotionEvent e)
      {
        titleContainer.setVisibility(View.GONE);
        urlContainer.setVisibility(View.VISIBLE);
        return true;
      }

      @Override
      public void onLongPress(MotionEvent e)
      {
        if (currentPageIndex > -1)
        {
          String currentUrl = pagesStack.get(currentPageIndex);
          ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
          ClipData clip = ClipData.newPlainText(MyResources.getString("app_name", activity), currentUrl);
          clipboard.setPrimaryClip(clip);
          Toast.makeText(activity, MyResources.getString("urls_saved_clipboard", activity) + "\n\n" + currentUrl, Toast.LENGTH_SHORT).show();
        }
      }

      @Override
      public boolean onDoubleTapEvent(MotionEvent e)
      {
        return true;
      }

      @Override
      public boolean onDown(MotionEvent e)
      {
        return true;
      }
    });

    pageTitle.setOnTouchListener(new View.OnTouchListener()
    {
      @Override
      public boolean onTouch(View v, MotionEvent event)
      {
        return pageTitleGestureDetector.onTouchEvent(event);
      }
    });*/

    pageTitle.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v)
      {
        arrowBackButton.setVisibility(View.GONE);
        arrowForwardButton.setVisibility(View.GONE);
        titleContainer.setVisibility(View.GONE);
        menuButton.setVisibility(View.GONE);
        urlContainer.setVisibility(View.VISIBLE);
        return true;
      }
    });

    pageURL.setOnKeyListener(new View.OnKeyListener() {
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ( (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) )
        {
          onGoURL();
          return true;
        }
        return false;
      }
    });

    goURLButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        onGoURL();
      }
    });

    goURLButton.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v)
      {
        viewsInBar(true);
        return true;
      }
    });

    final SwipeRefreshLayout webViewPullToRefresh = activity.findViewById(MyResources.getId("webViewPullToRefresh", activity));
    webViewPullToRefresh.setDistanceToTriggerSync(312);
    webViewPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
    {
      @Override
      public void onRefresh()
      {
        webViewPullToRefresh.setRefreshing(true);
        refresh();

        new Thread(new Runnable()
        {
          @Override
          public void run()
          {
            try {Thread.sleep(1500);}catch (Throwable t) {}

            activity.runOnUiThread(new Runnable()
            {
              @Override
              public void run()
              {
                webViewPullToRefresh.setRefreshing(false);
              }
            });
          }
        }).start();
      }
    });
  }

  private void initWebView()
  {
    webView.setWebViewClient(new MyWebViewClient(this, webViewName));
    myJavascript = new MyJavascript();
    webView.addJavascriptInterface(myJavascript, "HTMLOUT");
    webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    webView.setScrollbarFadingEnabled(false);
    myWebChromeClient = new MyWebChromeClient(progressBar);
    webView.setWebChromeClient(myWebChromeClient);

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

    String fontSize = VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.fontSizeKey, "", activity);
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

  private void initInject()
  {
    String injectJSSwitch = VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.injectJSKey, "0", activity);
    if("1".equals(injectJSSwitch))
    {
      String js2Inject = VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.injectJSTextKey, "", activity);
      if(Miscellaneous.isEmpty(js2Inject))
      {
        js2Inject = Constants.jsSource;
      }
      CachedValues.setJS2Inject(js2Inject);
    }

    String injectCSSSwitch = VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.injectCSSKey, "0", activity);
    if("1".equals(injectCSSSwitch))
    {
      String css2Inject = VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.injectCSSTextKey, "", activity);
      if(Miscellaneous.isEmpty(css2Inject))
      {
        css2Inject = Constants.cssSource;
      }
      CachedValues.setCSS2Inject(css2Inject);
    }
  }

  private void initSettings()
  {
    CachedValues.setExternalLinksDefaultBrowser(VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.externalLinksDefaultBrowserKey, "0", activity).equals("1"));

    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://easyjoin.net"));
    CachedValues.setBrowserIntentCanBeHandled(VariousUtils.intentCanBeHandled(browserIntent, activity));
  }

  private void loadEmptyPage()
  {
    String html = "<html><body style=\"background-color: black\"></body></html>";
    String encodedHtml = Base64.encodeToString(html.getBytes(), Base64.NO_PADDING);
    webView.loadData(encodedHtml, "text/html", "base64");
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

  private void onGoURL()
  {
    Editable text = pageURL.getText();
    if(text != null)
    {
      String textValue = String.valueOf(text);

      if(!Miscellaneous.isEmpty(textValue))
      {
        if( (!textValue.startsWith("https://")) && (!textValue.startsWith("http://")) )
        {
          textValue = "https://" + textValue;
        }
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, 0);
        loadUrl(textValue);
        viewsInBar(true);
      }
    }
  }

  private void viewsInBar(boolean isTitle)
  {
    if( (isTitle) && (urlContainer.getVisibility() == View.VISIBLE) )
    {
      urlContainer.setVisibility(View.GONE);
      arrowBackButton.setVisibility(View.VISIBLE);
      arrowForwardButton.setVisibility(View.VISIBLE);
      titleContainer.setVisibility(View.VISIBLE);
      menuButton.setVisibility(View.VISIBLE);
    }
    else if( (!isTitle) && (urlContainer.getVisibility() == View.GONE) )
    {
      arrowBackButton.setVisibility(View.GONE);
      arrowForwardButton.setVisibility(View.GONE);
      titleContainer.setVisibility(View.GONE);
      menuButton.setVisibility(View.GONE);
      urlContainer.setVisibility(View.VISIBLE);
    }
  }

  public void setPageTitle()
  {
    if(!webView.getTitle().startsWith("data:text/html"))
    {
      pageTitle.setText(webView.getTitle());
    }
    if(webView.getUrl().startsWith("http"))
    {
      pageURL.setText(webView.getUrl());
    }

    viewsInBar(true);
  }

  public void showShareButton()
  {
    boolean showShare = ( (!Miscellaneous.isEmpty(webView.getUrl())) && (webView.getUrl().indexOf("/t/") != -1) );
    if(showShare)
    {
      refreshButton.setVisibility(View.GONE);
      shareButton.setVisibility(View.VISIBLE);
    }
    else
    {
      shareButton.setVisibility(View.GONE);
      refreshButton.setVisibility(View.VISIBLE);
    }
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

  private void showPreviousPage()
  {
    currentPageIndex--;
    if(currentPageIndex < 0)
    {
      currentPageIndex = 0;
    }

    //webView.loadUrl(pagesStack.get(currentPageIndex));
    webView.goBack();

    setArrowsStatus();
  }

  private void showNextPage()
  {
    currentPageIndex++;
    if(currentPageIndex >= pagesStack.size())
    {
      currentPageIndex = pagesStack.size() - 1;
    }

    //webView.loadUrl(pagesStack.get(currentPageIndex));
    webView.goForward();

    setArrowsStatus();
  }

  private void refresh()
  {
    if( (currentPageIndex > -1) && (currentPageIndex < pagesStack.size()) )
    {
      webView.loadUrl(pagesStack.get(currentPageIndex));
    }
  }

  private void shareURL()
  {
    new ShareCompat
      .IntentBuilder(activity)
      .setType("text/plain")
      .setChooserTitle(MyResources.getString("share_thread", activity))
      .setText(webView.getUrl())
      .startChooser();
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
    browserMenu.getMenu().findItem(MyResources.getId("myContent", activity)).setVisible(!Miscellaneous.isEmpty(profileName));

    boolean showMagazineContent = (webView != null) && (webView.getUrl() != null) && (webView.getUrl().startsWith("https://kbin.social/m/"));
    MenuItem magazineContentMenuItem = browserMenu.getMenu().findItem(MyResources.getId("magazineContent", activity));
    magazineContentMenuItem.setTitle(MyResources.getString("magazine", activity));
    magazineContentMenuItem.setVisible(showMagazineContent);
    magazineNameInView = null;
    if(showMagazineContent)
    {
      String[] splitsUrl = webView.getUrl().split("/m/");
      if(splitsUrl.length == 2)
      {
        if(!Miscellaneous.isEmpty(splitsUrl[1]))
        {
          String[] splitsMagazineUrl = splitsUrl[1].split("/");
          if(!Miscellaneous.isEmpty(splitsMagazineUrl[0]))
          {
            magazineNameInView = splitsMagazineUrl[0];
            magazineContentMenuItem.setTitle("m/" + magazineNameInView);
          }
        }
      }
    }

    browserMenu.show();
  }

  @Override
  public boolean onMenuItemClick(MenuItem item)
  {
    if(item.getItemId() == MyResources.getId("topThreadsKbin", activity))
    {
      webView.loadUrl("https://kbin.social/all/top");
    }
    else if(item.getItemId() == MyResources.getId("hotThreadsKbin", activity))
    {
      webView.loadUrl("https://kbin.social/all");
    }
    else if(item.getItemId() == MyResources.getId("newThreadsKbin", activity))
    {
      webView.loadUrl("https://kbin.social/all/newest");
    }
    else if(item.getItemId() == MyResources.getId("mblogKbin", activity))
    {
      webView.loadUrl("https://kbin.social/microblog");
    }
    else if(item.getItemId() == MyResources.getId("subscribedThreads", activity))
    {
      webView.loadUrl("https://kbin.social/sub");
    }
    else if(item.getItemId() == MyResources.getId("moderatedThreads", activity))
    {
      webView.loadUrl("https://kbin.social/mod");
    }
    else if(item.getItemId() == MyResources.getId("moderatedMagazines", activity))
    {
      webView.loadUrl("https://kbin.social/u/" + profileName + "/moderated");
    }
    else if(item.getItemId() == MyResources.getId("topThreadsMagazine", activity))
    {
      if(!Miscellaneous.isEmpty(magazineNameInView))
      {
        webView.loadUrl("https://kbin.social/m/" + magazineNameInView + "/top");
      }
    }
    else if(item.getItemId() == MyResources.getId("hotThreadsMagazine", activity))
    {
      if(!Miscellaneous.isEmpty(magazineNameInView))
      {
        webView.loadUrl("https://kbin.social/m/" + magazineNameInView);
      }
    }
    else if(item.getItemId() == MyResources.getId("newThreadsMagazine", activity))
    {
      if(!Miscellaneous.isEmpty(magazineNameInView))
      {
        webView.loadUrl("https://kbin.social/m/" + magazineNameInView + "/newest");
      }
    }
    else if(item.getItemId() == MyResources.getId("mblogMagazine", activity))
    {
      if(!Miscellaneous.isEmpty(magazineNameInView))
      {
        webView.loadUrl("https://kbin.social/m/" + magazineNameInView + "/microblog");
      }
    }
    else if(item.getItemId() == MyResources.getId("settingInjectJS", activity))
    {
      VariousUtils.openDialogActivity(InjectJSActivity.class, activity);
    }
    else if(item.getItemId() == MyResources.getId("settingInjectCSS", activity))
    {
      VariousUtils.openDialogActivity(InjectCSSActivity.class, activity);
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
    else if(item.getItemId() == MyResources.getId("moreSettings", activity))
    {
      VariousUtils.openDialogActivity(SettingsActivity.class, activity);
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
    VariousUtils.savePreference(Constants.sharedPreferencesName, Constants.fontSizeKey, "" + fontSize, activity);
  }

  public boolean backButtonPressed(int keyCode, KeyEvent event)
  {
    if(urlContainer.getVisibility() == View.VISIBLE)
    {
      viewsInBar(true);
      return true;
    }
    else if(currentPageIndex > 0)
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
                    //span class=\"user-name\">anemomylos\u003C/span>
                    int index2 = html.indexOf("\\u003C", index + profileContainer.length());
                    if(index2 != -1)
                    {
                      profileName = html.substring(index + profileContainer.length(), index2);

                      if (!Miscellaneous.isEmpty(profileName))
                      {
                        VariousUtils.savePreference(Constants.sharedPreferencesName, Constants.profileNameKey, profileName, activity);
                      }
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
      VariousUtils.deletePreference(Constants.sharedPreferencesName, Constants.profileNameKey, activity);
    }
  }

  public void inject(String url)
  {
    if(url.startsWith("https://kbin.social"))
    {
      if (!Miscellaneous.isEmpty(CachedValues.getJS2Inject()))
      {
        injectJS();
      }
      if (!Miscellaneous.isEmpty(CachedValues.getCSS2Inject()))
      {
        //MyLog.showDialog(new String(Base64.decode(CachedValues.getCSS2Inject(), Base64.NO_WRAP)), false, activity);
        injectCSS();
      }
    }
  }

  private void injectJS()
  {
    webView.loadUrl("javascript:(function() {" +
      "var parent = document.getElementsByTagName('head').item(0);" +
      "var script = document.createElement('script');" +
      "script.type = 'text/javascript';" +
      "script.innerHTML = decodeURIComponent(window.atob('" + CachedValues.getJS2Inject() + "'));" +
      "parent.appendChild(script)" +
      "})()");
  }

  private void injectCSS()
  {
    webView.loadUrl("javascript:(function() {" +
      "var parent = document.getElementsByTagName('head').item(0);" +
      "var style = document.createElement('style');" +
      "style.type = 'text/css';" +
      "style.innerHTML = window.atob('" + CachedValues.getCSS2Inject() + "');" +
      "parent.appendChild(style)" +
      "})()");
  }
}

