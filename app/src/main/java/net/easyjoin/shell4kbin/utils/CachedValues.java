package net.easyjoin.shell4kbin.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Base64;

import net.easyjoin.utils.Miscellaneous;
import net.easyjoin.utils.MyLog;

import java.util.ArrayList;
import java.util.List;

public final class CachedValues
{
  private static final String className = CachedValues.class.getName();
  private static String js2Inject;
  private static String css2Inject;
  private static boolean externalLinksDefaultBrowser;
  private static boolean browserIntentCanBeHandled;
  private static boolean showRedditLinks;
  private static String redditLinks;
  private static List<String> redditLinksList = new ArrayList<>();


  public static String getJS2Inject()
  {
    return js2Inject;
  }

  public static void setJS2Inject(String value)
  {
    if(value != null)
    {
      js2Inject = Base64.encodeToString(value.getBytes(), Base64.NO_WRAP);
    }
  }

  public static String getCSS2Inject()
  {
    return css2Inject;
  }

  public static void setCSS2Inject(String value)
  {
    if(value != null)
    {
      css2Inject = Base64.encodeToString(value.getBytes(), Base64.NO_WRAP);
    }
  }

  public static boolean isExternalLinksDefaultBrowser()
  {
    return externalLinksDefaultBrowser;
  }

  public static void setExternalLinksDefaultBrowser(boolean value)
  {
    externalLinksDefaultBrowser = value;
  }

  public static boolean isBrowserIntentCanBeHandled()
  {
    return browserIntentCanBeHandled;
  }

  public static void setBrowserIntentCanBeHandled(boolean value)
  {
    browserIntentCanBeHandled = value;
  }

  public static boolean isShowRedditLinks()
  {
    return showRedditLinks;
  }

  public static void setShowRedditLinks(boolean showRedditLinks)
  {
    CachedValues.showRedditLinks = showRedditLinks;
  }

  public static String getRedditLinks()
  {
    return redditLinks;
  }

  public static List<String> getRedditLinksList()
  {
    return redditLinksList;
  }

  public static void setRedditLinks(String redditLinks)
  {
    CachedValues.redditLinks = redditLinks;
    redditLinksList.clear();

    if(!Miscellaneous.isEmpty(redditLinks))
    {
      String[] links = redditLinks.split(",");
      for (String link : links)
      {
        link = link.trim();
        if(!Miscellaneous.isEmpty(link))
        {
          redditLinksList.add(link);
        }
      }
    }
  }
}
