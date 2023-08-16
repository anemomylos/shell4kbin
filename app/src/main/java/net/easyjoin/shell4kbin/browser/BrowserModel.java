package net.easyjoin.shell4kbin.browser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import net.easyjoin.shell4kbin.activity.BookmarksActivity;
import net.easyjoin.shell4kbin.activity.InjectCSSActivity;
import net.easyjoin.shell4kbin.activity.InjectJSActivity;
import net.easyjoin.shell4kbin.activity.SettingsActivity;
import net.easyjoin.shell4kbin.bookmark.BookmarkManager;
import net.easyjoin.shell4kbin.bookmark.MyBookmark;
import net.easyjoin.shell4kbin.utils.CachedValues;
import net.easyjoin.shell4kbin.utils.Constants;
import net.easyjoin.utils.Miscellaneous;
import net.easyjoin.utils.MyLog;
import net.easyjoin.utils.MyResources;
import net.easyjoin.utils.ThemeUtils;
import net.easyjoin.utils.VariousUtils;
import net.easyjoin.utils.WebViewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public final class BrowserModel implements PopupMenu.OnMenuItemClickListener
{
  private final String className = getClass().getName();
  private List<String> pagesStack;
  private int currentPageIndex = -1;
  private Activity activity;
  public String initialUrl;
  private boolean isNormalView;
  private boolean isDesktopView;
  private WebView webViewMain;
  private WebView webViewPost;
  private SwipeRefreshLayout webViewMainPullToRefresh;
  private SwipeRefreshLayout webViewPostPullToRefresh;
  private ProgressBar progressBar;
  private TextView pageTitle;
  private AppCompatEditText pageURL;
  private ImageButton arrowBackButton;
  private ImageButton arrowForwardButton;
  private ImageButton moreOptionsButton;
  private ImageButton menuButton;
  private ImageButton goURLButton;
  private PopupMenu browserMenu;
  private PopupMenu moreOptionsMenu;
  private MenuPopupHelper moreOptionsMenuHelper;
  private View navigationToolbar;
  private View urlToolbar;
  private String profileName;
  private final String profileContainer = "span class=\\\"user-name\\\">";
  private String magazineNameInView;
  private TextView magazineNameToolbar;

  public BrowserModel(Activity activity, boolean isNormalView, boolean isDesktopView)
  {
    this.activity = activity;
    this.isNormalView = isNormalView;
    this.isDesktopView = isDesktopView;
    pagesStack = new ArrayList<>();

    initLayout();
    initWebView(webViewMain);
    initWebView(webViewPost);
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
      webViewMain.setBackgroundColor(Color.BLACK);
      webViewPost.setBackgroundColor(Color.BLACK);
    }

    loadInWebViewMain(initialPage);
  }

  @SuppressLint("ClickableViewAccessibility")
  private void initLayout()
  {
    webViewMain = activity.findViewById(MyResources.getId("webViewMain", activity));
    webViewPost = activity.findViewById(MyResources.getId("webViewPost", activity));
    navigationToolbar = activity.findViewById(MyResources.getId("navigationToolbar", activity));
    urlToolbar = activity.findViewById(MyResources.getId("urlToolbar", activity));
    pageTitle = activity.findViewById(MyResources.getId("pageTitle", activity));
    pageURL = activity.findViewById(MyResources.getId("pageURL", activity));
    arrowBackButton = activity.findViewById(MyResources.getId("arrowBackButton", activity));
    arrowForwardButton = activity.findViewById(MyResources.getId("arrowForwardButton", activity));
    moreOptionsButton = activity.findViewById(MyResources.getId("moreOptionsButton", activity));
    menuButton = activity.findViewById(MyResources.getId("menuButton", activity));
    goURLButton = activity.findViewById(MyResources.getId("goURLButton", activity));
    progressBar = activity.findViewById(MyResources.getId("progressLoad", activity));
    magazineNameToolbar = activity.findViewById(MyResources.getId("magazineNameToolbar", activity));

    setArrowsStatus();
    createBrowserMenu();
    createMoreOptionsMenu();

    menuButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        showBrowserMenu();
      }
    });

    moreOptionsButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        showMoreOptionsMenu();
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

    pageTitle.setOnLongClickListener(new View.OnLongClickListener()
    {
      @Override
      public boolean onLongClick(View v)
      {
        navigationToolbar.setVisibility(View.GONE);
        urlToolbar.setVisibility(View.VISIBLE);

        pageURL.requestFocus();
        pageURL.setSelection(pageURL.getText().length());
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(pageURL, InputMethodManager.SHOW_IMPLICIT);

        return true;
      }
    });

    pageURL.setOnKeyListener(new View.OnKeyListener()
    {
      public boolean onKey(View v, int keyCode, KeyEvent event)
      {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
        {
          onGoURL();
          return true;
        }
        return false;
      }
    });

    goURLButton.setOnClickListener(new View.OnClickListener()
    {
      @Override
      public void onClick(View v)
      {
        onGoURL();
      }
    });

    goURLButton.setOnLongClickListener(new View.OnLongClickListener()
    {
      @Override
      public boolean onLongClick(View v)
      {
        viewsInBar(true);
        return true;
      }
    });

    webViewMainPullToRefresh = activity.findViewById(MyResources.getId("webViewMainPullToRefresh", activity));
    webViewMainPullToRefresh.setDistanceToTriggerSync(312);
    webViewMainPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
    {
      @Override
      public void onRefresh()
      {
        webViewMainPullToRefresh.setRefreshing(true);
        refreshMain();

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
                webViewMainPullToRefresh.setRefreshing(false);
              }
            });
          }
        }).start();
      }
    });

    webViewPostPullToRefresh = activity.findViewById(MyResources.getId("webViewPostPullToRefresh", activity));
    webViewPostPullToRefresh.setDistanceToTriggerSync(312);
    webViewPostPullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
    {
      @Override
      public void onRefresh()
      {
        webViewPostPullToRefresh.setRefreshing(true);
        refreshPost();

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
                webViewPostPullToRefresh.setRefreshing(false);
              }
            });
          }
        }).start();
      }
    });
  }

  private void initWebView(WebView webView)
  {
    webView.setWebViewClient(new MyWebViewClient(this));
    webView.addJavascriptInterface( new MyJavascript(), "HTMLOUT");
    webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    webView.setScrollbarFadingEnabled(false);
    webView.setWebChromeClient(new MyWebChromeClient(progressBar));

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
      String newUA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/116.0";
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

    CachedValues.setShowRedditLinks(VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.showRedditLinksKey, "0", activity).equals("1"));

    CachedValues.setRedditLinks(VariousUtils.readPreference(Constants.sharedPreferencesName, Constants.redditLinksKey, "", activity));
  }

  public void loadInWebViewMain(String url)
  {
    WebViewUtils.cleanWebViewContent(webViewMain);
    loadUrl(url, null, webViewMain);
    closeWebViewPost();
  }

  public void loadInWebViewPost(String url)
  {
    WebViewUtils.cleanWebViewContent(webViewPost);
    webViewPost.loadUrl(url);
    showWebViewPost();

    if (isShowUrlInExternalBrowser(url))
    {
      closeWebViewPost();
    }
  }

  private void showWebViewPost()
  {
    if(webViewPostPullToRefresh.getVisibility() == View.GONE)
    {
      webViewMainPullToRefresh.setVisibility(View.GONE);
      webViewPostPullToRefresh.setVisibility(View.VISIBLE);
    }
  }

  public void closeWebViewPost()
  {
    if(webViewPostPullToRefresh.getVisibility() == View.VISIBLE)
    {
      webViewPostPullToRefresh.setVisibility(View.GONE);
      webViewMainPullToRefresh.setVisibility(View.VISIBLE);
    }
  }

  private boolean isWebViewPostVisible()
  {
    return (webViewPostPullToRefresh.getVisibility() == View.VISIBLE);
  }

  public void loadUrl(String url, Map<String, String> extraHeaders, WebView webView)
  {
    initialUrl = url;
    WebViewUtils.cleanWebViewContent(webView);

    if(extraHeaders != null)
    {
      webView.loadUrl(url, extraHeaders);
    }
    else
    {
      webView.loadUrl(url);
    }
  }

  public void loadUrl(String url, WebView webView)
  {
    loadUrl(url, null, webView);
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
        loadUrl(textValue, getVisibleWebView());
        viewsInBar(true);
      }
    }
  }

  private void viewsInBar(boolean isTitle)
  {
    if( (isTitle) && (urlToolbar.getVisibility() == View.VISIBLE) )
    {
      urlToolbar.setVisibility(View.GONE);
      navigationToolbar.setVisibility(View.VISIBLE);
    }
    else if( (!isTitle) && (urlToolbar.getVisibility() == View.GONE) )
    {
      navigationToolbar.setVisibility(View.GONE);
      urlToolbar.setVisibility(View.VISIBLE);
    }
  }

  public void setPageTitle()
  {
    WebView webView = getVisibleWebView();

    if( (webView.getTitle() != null) && (!webView.getTitle().startsWith("data:text/html")) )
    {
      pageTitle.setText(webView.getTitle());
    }
    if( (webView.getUrl() != null) &&  (webView.getUrl().startsWith("http")) )
    {
      pageURL.setText(webView.getUrl());
    }

    if(retrieveMagazineInView())
    {
      magazineNameToolbar.setText(magazineNameInView);
    }
    else
    {
      magazineNameToolbar.setText("");
    }

    viewsInBar(true);
  }

  public void addNextPage(String url)
  {
    if(!"about:blank".equals(url))
    {
      if (currentPageIndex > -1)
      {
        if (!pagesStack.get(currentPageIndex).equalsIgnoreCase(url))
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

  private synchronized void showPreviousPage()
  {
    currentPageIndex--;
    if(currentPageIndex < 0)
    {
      currentPageIndex = 0;
    }

    String previousUrl = pagesStack.get(currentPageIndex);

    if(isUrl4WebViewPost(previousUrl))
    {
      webViewMain.stopLoading();
      webViewPost.stopLoading();
      showWebViewPost();
      webViewPost.goBack();
    }
    else
    {
      webViewMain.stopLoading();
      webViewPost.stopLoading();
      if(isWebViewPostVisible())
      {
        closeWebViewPost();
      }
      else
      {
        webViewMain.goBack();
      }
    }

    setPageTitle();
    setArrowsStatus();
  }

  private synchronized void showNextPage()
  {
    currentPageIndex++;
    if(currentPageIndex >= pagesStack.size())
    {
      currentPageIndex = pagesStack.size() - 1;
    }

    if(isUrl4WebViewPost(pagesStack.get(currentPageIndex)))
    {
      webViewMain.stopLoading();
      webViewPost.stopLoading();
      showWebViewPost();
      webViewPost.goForward();
    }
    else
    {
      webViewMain.stopLoading();
      webViewPost.stopLoading();
      if(isWebViewPostVisible())
      {
        closeWebViewPost();
      }
      else
      {
        webViewMain.goForward();
      }
    }

    setPageTitle();
    setArrowsStatus();
  }

  private void refresh()
  {
    WebView webView = getVisibleWebView();
    webView.reload();
  }

  private void refreshMain()
  {
    webViewMain.reload();
  }

  private void refreshPost()
  {
    webViewPost.reload();
  }

  private void shareURL()
  {
    VariousUtils.shareText(getVisibleWebView().getUrl(), activity);
  }

  private void savePage()
  {
    MyBookmark myBookmark = new MyBookmark();
    myBookmark.setUrl(getVisibleWebView().getUrl());
    myBookmark.setTitle(getVisibleWebView().getTitle());
    if(retrieveMagazineInView())
    {
      myBookmark.setMagazine(magazineNameInView);
    }

    BookmarkManager.getInstance().add(myBookmark);
  }

  public boolean isShowUrlInExternalBrowser(String url)
  {
    return ( (CachedValues.isExternalLinksDefaultBrowser()) && (CachedValues.isBrowserIntentCanBeHandled()) && (!url.startsWith("https://kbin.")) );
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

  @SuppressLint("RestrictedApi")
  private void createMoreOptionsMenu()
  {
    moreOptionsMenu = new PopupMenu(activity, moreOptionsButton);
    MenuInflater inflater = moreOptionsMenu.getMenuInflater();
    inflater.inflate(MyResources.getMenu("more_options_menu", activity), moreOptionsMenu.getMenu());
    moreOptionsMenu.setOnMenuItemClickListener(this);

    moreOptionsMenuHelper = new MenuPopupHelper(activity, (MenuBuilder) moreOptionsMenu.getMenu(), moreOptionsButton);
    moreOptionsMenuHelper.setForceShowIcon(true);
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
    boolean existProfileName = !Miscellaneous.isEmpty(profileName);
    browserMenu.getMenu().findItem(MyResources.getId("subscribedThreads", activity)).setVisible(existProfileName);
    browserMenu.getMenu().findItem(MyResources.getId("moderatedThreads", activity)).setVisible(existProfileName);
    browserMenu.getMenu().findItem(MyResources.getId("moderatedMagazines", activity)).setVisible(existProfileName);

    boolean existRedditLinks = (CachedValues.isShowRedditLinks() && !CachedValues.getRedditLinksList().isEmpty());
    browserMenu.getMenu().findItem(MyResources.getId("redditMenu", activity)).setVisible(existRedditLinks);
    if(existRedditLinks)
    {
      Menu redditMenu = browserMenu.getMenu().findItem(MyResources.getId("redditMenu", activity)).getSubMenu();
      redditMenu.clear();
      List<String> links = CachedValues.getRedditLinksList();
      for(int i = 0; i < links.size(); i++)
      {
        redditMenu.add(Constants.redditMenuId, i, Menu.NONE, links.get(i));
      }
    }

    boolean isMagazineInView = retrieveMagazineInView();
    MenuItem magazineContentMenuItem = browserMenu.getMenu().findItem(MyResources.getId("magazineContent", activity));
    magazineContentMenuItem.setTitle(MyResources.getString("magazine", activity));
    magazineContentMenuItem.setVisible(isMagazineInView);
    if(isMagazineInView)
    {
      magazineContentMenuItem.setTitle("m/" + magazineNameInView);
    }

    browserMenu.show();
  }

  private boolean retrieveMagazineInView()
  {
    magazineNameInView = null;

    WebView webView = getVisibleWebView();

    boolean isMagazineInView = (webView != null) && (webView.getUrl() != null) && (webView.getUrl().startsWith("https://kbin.social/m/"));

    if(isMagazineInView)
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
          }
        }
      }
    }

    return isMagazineInView;
  }

  private WebView getVisibleWebView()
  {
    if(isWebViewPostVisible())
    {
      return webViewPost;
    }

    return webViewMain;
  }

  public boolean isUrl4WebViewPost(String url)
  {
    return ( (!url.startsWith("https://kbin.social"))
            || ((url.startsWith("https://kbin.social/m/")) && (url.contains("/t/"))) );
  }

  @SuppressLint("RestrictedApi")
  private void showMoreOptionsMenu()
  {
    moreOptionsMenu.show();
    moreOptionsMenuHelper.show();
  }

  @Override
  public boolean onMenuItemClick(MenuItem item)
  {
    if(item.getItemId() == MyResources.getId("topThreadsKbin", activity))
    {
      loadInWebViewMain("https://kbin.social/all/top");
    }
    else if(item.getItemId() == MyResources.getId("hotThreadsKbin", activity))
    {
      loadInWebViewMain("https://kbin.social/all");
    }
    else if(item.getItemId() == MyResources.getId("newThreadsKbin", activity))
    {
      loadInWebViewMain("https://kbin.social/all/newest");
    }
    else if(item.getItemId() == MyResources.getId("mblogKbin", activity))
    {
      loadInWebViewMain("https://kbin.social/microblog");
    }
    else if(item.getItemId() == MyResources.getId("subscribedThreads", activity))
    {
      loadInWebViewMain("https://kbin.social/sub");
    }
    else if(item.getItemId() == MyResources.getId("moderatedThreads", activity))
    {
      loadInWebViewMain("https://kbin.social/mod");
    }
    else if(item.getItemId() == MyResources.getId("moderatedMagazines", activity))
    {
      loadInWebViewMain("https://kbin.social/u/" + profileName + "/moderated");
    }
    else if(item.getItemId() == MyResources.getId("savedBookmarks", activity))
    {
      Intent intent = new Intent(activity, BookmarksActivity.class);
      activity.startActivityForResult(intent, Constants.bookmarkUrlRequestCode);
    }
    else if(item.getItemId() == MyResources.getId("topThreadsMagazine", activity))
    {
      if(!Miscellaneous.isEmpty(magazineNameInView))
      {
        loadInWebViewMain("https://kbin.social/m/" + magazineNameInView + "/top");
      }
    }
    else if(item.getItemId() == MyResources.getId("hotThreadsMagazine", activity))
    {
      if(!Miscellaneous.isEmpty(magazineNameInView))
      {
        loadInWebViewMain("https://kbin.social/m/" + magazineNameInView);
      }
    }
    else if(item.getItemId() == MyResources.getId("newThreadsMagazine", activity))
    {
      if(!Miscellaneous.isEmpty(magazineNameInView))
      {
        loadInWebViewMain("https://kbin.social/m/" + magazineNameInView + "/newest");
      }
    }
    else if(item.getItemId() == MyResources.getId("mblogMagazine", activity))
    {
      if(!Miscellaneous.isEmpty(magazineNameInView))
      {
        loadInWebViewMain("https://kbin.social/m/" + magazineNameInView + "/microblog");
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
      setFontSize(-1, webViewMain);
      setFontSize(-1, webViewPost);
      return false;
    }
    else if(item.getItemId() == MyResources.getId("biggerFont", activity))
    {
      keepMenuOpenOnSelection(item);
      setFontSize(1, webViewMain);
      setFontSize(1, webViewPost);
      return false;
    }
    else if(item.getItemId() == MyResources.getId("moreSettings", activity))
    {
      VariousUtils.openDialogActivity(SettingsActivity.class, activity);
    }
    else if(item.getItemId() == MyResources.getId("shareURL", activity))
    {
      shareURL();
    }
    else if(item.getItemId() == MyResources.getId("savePage", activity))
    {
      savePage();
    }
    else if(item.getItemId() == MyResources.getId("refreshPage", activity))
    {
      refresh();
    }
    else if(item.getGroupId() == Constants.redditMenuId)
    {
      int index = item.getItemId();
      MyLog.w(className, "", "index: " + index);
      loadInWebViewPost("https://www.reddit.com/r/" + CachedValues.getRedditLinksList().get(index) + "/");
    }
    else
    {
      return false;
    }

    return true;
  }

  private void setFontSize(int step, WebView webView)
  {
    int fontSize = webView.getSettings().getDefaultFontSize() + step;
    webView.getSettings().setDefaultFontSize(fontSize);
    VariousUtils.savePreference(Constants.sharedPreferencesName, Constants.fontSizeKey, "" + fontSize, activity);
  }

  public boolean backButtonPressed(int keyCode, KeyEvent event)
  {
    if(urlToolbar.getVisibility() == View.VISIBLE)
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
    WebView webView = getVisibleWebView();
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
    else
    {
      removeProfileName(currentUrl);
    }
  }

  public void removeProfileName(String url)
  {
    if ( (url != null) &&
      ( (url.startsWith("https://kbin.social/login")) || (url.startsWith("https://kbin.social/logout")) ) )
    {
      VariousUtils.deletePreference(Constants.sharedPreferencesName, Constants.profileNameKey, activity);
      profileName = null;
    }
  }

  public void inject(String url)
  {
    WebView webView = getVisibleWebView();

    if (!Miscellaneous.isEmpty(CachedValues.getJS2Inject()))
    {
      injectJS(webView);
    }
    if (!Miscellaneous.isEmpty(CachedValues.getCSS2Inject()))
    {
      //MyLog.showDialog(new String(android.util.Base64.decode(CachedValues.getCSS2Inject(), android.util.Base64.NO_WRAP)), false, activity); //for test
      injectCSS(webView);
    }
  }

  private void injectJS(WebView webView)
  {
    webView.loadUrl("javascript:(function() {" +
      "var parent = document.getElementsByTagName('head').item(0);" +
      "var script = document.createElement('script');" +
      "script.type = 'text/javascript';" +
      "script.innerHTML = decodeURIComponent(window.atob('" + CachedValues.getJS2Inject() + "'));" +
      "parent.appendChild(script)" +
      "})()");
  }

  private void injectCSS(WebView webView)
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

