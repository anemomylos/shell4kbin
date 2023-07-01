package net.easyjoin.utils;

public class TimeoutException extends Exception
{
  public TimeoutException()
  {
    super();
  }

  public TimeoutException(String message)
  {
    super(message);
  }
}