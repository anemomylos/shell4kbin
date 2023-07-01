package net.easyjoin.utils;


public final class NullObject
{
  /**The only instance of that class.*/
  public final static NullObject INSTANCE = new NullObject();
  
  
  /**
   *No other instance can be create from other classes.
   */
  private NullObject() { }
  
  
  /**
   *@return the value returnet is <b>null</b>.
   */
  public String toString()
  {
    return null;
  }
  
}
