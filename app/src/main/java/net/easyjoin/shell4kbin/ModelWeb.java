package net.easyjoin.shell4kbin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;

import net.droidopoulos.utils.MyLog;
import net.droidopoulos.various.MyResources;

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
    pageTitle = activity.findViewById(MyResources.getId("pageTitle", activity));
    arrowBackButton = activity.findViewById(MyResources.getId("arrowBackButton", activity));
    arrowForwardButton = activity.findViewById(MyResources.getId("arrowForwardButton", activity));
    refreshButton = activity.findViewById(MyResources.getId("refreshButton", activity));
    menuButton = activity.findViewById(MyResources.getId("menuButton", activity));

    createBrowserMenu();
    menuButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v)
      {
        showBrowserMenu();
      }
    });
  }

  private void initWebView()
  {
    webView.setWebViewClient(new MyBrowser(this, webViewName));
    //webView.addJavascriptInterface(new MyJavascript(), "HTMLOUT");
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

  public void setPageTitle(WebView webView)
  {
    pageTitle.setText(webView.getTitle());
  }

  public void addNextPage(String url)
  {
    MyLog.w(className, "addNextPage", "currentPageIndex: " + currentPageIndex);
    if(!"about:blank".equals(url))
    {
      if (currentPageIndex > -1)
      {
        if (!pagesStack.get(currentPageIndex).equals(url))
        {
          currentPageIndex++;
          pagesStack.add(currentPageIndex, url);

          MyLog.w(className, "addNextPage", "currentPageIndex: " + currentPageIndex + ", pagesStack.size(): " + pagesStack.size());
          if (pagesStack.size() > (currentPageIndex + 1))
          {
            MyLog.w(className, "addNextPage", " removing elements from pagesStack after index: " + currentPageIndex);
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
      MyLog.w(className, "refresh", "reloading: " + pagesStack.get(currentPageIndex));
      webView.loadUrl(pagesStack.get(currentPageIndex));
    }
  }

  private void setArrowsStatus()
  {
    MyLog.w(className, "refresh", "currentPageIndex: " + currentPageIndex + ", pagesStack.size(): " + pagesStack.size());
    arrowBackButton.setEnabled(currentPageIndex != 0);
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

  private void showBrowserMenu()
  {
    browserMenu.show();
  }

  @Override
  public boolean onMenuItemClick(MenuItem item)
  {
    if(item.getItemId() == MyResources.getId("menuModeratedMagazines", activity))
    {
      webView.loadUrl("https://kbin.social/mod");
    }
    else if(item.getItemId() == MyResources.getId("menuSubscribedMagazines", activity))
    {
      webView.loadUrl("https://kbin.social/sub");
    }
    else if(item.getItemId() == MyResources.getId("allPosts", activity))
    {
      webView.loadUrl("https://kbin.social/");
    }
    else
    {
      return false;
    }

    return true;
  }

  public boolean backButtonPressed(int keyCode, KeyEvent event)
  {
    if(currentPageIndex > 0)
    {
      showPreviousPage();
      return true;
    }
    else
    {
      return false;
    }
  }
}

