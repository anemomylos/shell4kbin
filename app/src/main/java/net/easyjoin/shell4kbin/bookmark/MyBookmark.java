package net.easyjoin.shell4kbin.bookmark;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public final class MyBookmark implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String url;
  private String title;
  private String magazine;

  public String getUrl()
  {
    return url;
  }

  public void setUrl(String url)
  {
    this.url = url;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public String getMagazine()
  {
    return magazine;
  }

  public void setMagazine(String magazine)
  {
    this.magazine = magazine;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    sb.append("url: ");
    sb.append(getUrl());
    sb.append(" | ");
    sb.append("title: ");
    sb.append(getTitle());
    sb.append(" | ");
    sb.append("magazine: ");
    sb.append(getMagazine());
    sb.append("}");

    return sb.toString();
  }
}
