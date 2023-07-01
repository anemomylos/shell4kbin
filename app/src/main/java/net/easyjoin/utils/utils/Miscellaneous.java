package net.easyjoin.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;


public final class Miscellaneous
{
  public final static ArrayList stackTrace(int remove) throws Exception
  {    
    String stackTraceString = stackTrace(new Exception());
    
    StringTokenizer stringTokenizer = new StringTokenizer(stackTraceString, "\r\n", false);
    ArrayList stringList = new ArrayList(stringTokenizer.countTokens());
    while(stringTokenizer.hasMoreTokens())
    {      
      String token = stringTokenizer.nextToken().trim();
      stringList.add(token.substring(3)); //remove prefix string "at "
    }//while
    
    //delete the first x lines
    for(int i=0; i<remove+2; i++)
    {
      try
      {
        stringList.remove(0);
      }
      catch(Throwable t)
      {
        break;
      }
    }
    
    return stringList;
  }
  

  public final static String stackTrace(Exception exception)
  { 
    String stack = null;
    ByteArrayOutputStream buf = null;
    PrintWriter pw = null;
    
    try
    {
      buf = new ByteArrayOutputStream(1024);
      pw = new PrintWriter(buf);
      exception.printStackTrace(pw);
      pw.flush();
    
      stack = buf.toString();
    }
    catch(Throwable t)
    {
      stack = "error on reading stack trace: " + t.toString();
      
      if(pw != null)
        pw.close();
      
      try
      {
        if(buf != null)
          buf.close();
      }
      catch(Throwable th){}
    }
    
    return stack;    
  }

  public final static String stackTrace(Throwable throwable)
  {
    String stack = null;
    ByteArrayOutputStream buf = null;
    PrintWriter pw = null;

    try
    {
      buf = new ByteArrayOutputStream(1024);
      pw = new PrintWriter(buf);
      throwable.printStackTrace(pw);
      pw.flush();

      stack = buf.toString();
    }
    catch(Throwable t)
    {
      stack = "error on reading stack trace: " + t.toString();

      if(pw != null)
        pw.close();

      try
      {
        if(buf != null)
          buf.close();
      }
      catch(Throwable th){}
    }

    return stack;
  }


  public final static String vector2String(Vector vector)
  {
    StringBuilder string2Return = new StringBuilder();
    
    if(vector != null)
    {
      int length = vector.size()-1;

      for(int i=0; i <= length; i++)
      {
        string2Return.append(vector.elementAt(i));
        if( i != length )
          string2Return.append("|");
      }
    }
    
    return string2Return.toString();
  }

  public final static String arrayList2String(ArrayList arrayList)
  {
    StringBuilder string2Return = new StringBuilder();
    
    if(arrayList != null)
    {
      int length = arrayList.size()-1;

      for(int i=0; i <= length; i++)
      {
        string2Return.append(arrayList.get(i));
        if( i != length )
          string2Return.append("|");
      }
    }
    
    return string2Return.toString();
  }
  

  public final static String arrayString2String(String[] values)
  {
    StringBuilder string2Return = new StringBuilder();
    
    if(values != null)
    {
      int length = values.length-1;

      for(int i=0; i <= length; i++)
      {
        string2Return.append(values[i]);
        if( i != length )
          string2Return.append("|");
      }
    }
    
    return string2Return.toString();
  }

  public static Vector getVectorFromString(String toElaborate, String separator)
  {
    Vector vector = new Vector();
    
    StringTokenizer stringTokenizer = new StringTokenizer(toElaborate, separator);
    while(stringTokenizer.hasMoreTokens())
    {
      String token = stringTokenizer.nextToken();
      vector.addElement(token);
    }
    
    return vector;
  }

  public static ArrayList getArrayListFromString(String toElaborate, String separator)
  {
    ArrayList arrayList = new ArrayList();
    
    StringTokenizer stringTokenizer = new StringTokenizer(toElaborate, separator);
    while(stringTokenizer.hasMoreTokens())
    {
      String token = stringTokenizer.nextToken();
      arrayList.add(token);
    }
    
    return arrayList;
  }

  public static ArrayList getArrayListFromStringArray(String[] toElaborate)
  {
    ArrayList arrayList = new ArrayList();
    
    if(toElaborate != null)
    {
      int toElaborateLength = toElaborate.length;
      
      for(int i=0; i<toElaborateLength; i++)
        arrayList.add(toElaborate[i]);
    }
    
    return arrayList;
  }


  public static String[] getStringArrayFromArrayList(ArrayList toElaborate)
  {
    String[] stringArray = null;

    if(toElaborate != null)
    {
      int toElaborateSize = toElaborate.size();
      stringArray = new String[toElaborateSize];

      for(int i=0; i<toElaborateSize; i++)
        stringArray[i] = (String)toElaborate.get(i);
    }

    return stringArray;
  }


  public static Vector getVectorFromStringArray(String[] toElaborate)
  {
    Vector vector = new Vector();
    
    if(toElaborate != null)
    {
      int length = toElaborate.length;
      
      for(int i=0; i<length; i++)
        vector.addElement(toElaborate[i]);
    }
    
    return vector;
  }

  public static boolean isEmpty(String toCheck)
  {
    boolean result = false;

    if( (toCheck == null) || ("".equals(toCheck.trim())) )
      result = true;

    return result;
  }

  public static String doubleSingleQuot(String toChange)
  {
    if (toChange != null)
      toChange = ReplaceText.replace(toChange, "'", "''");

    return toChange;
  }

  public static Object getNotNull(Object toCheck, Object defaultValue)
  {
    if ((toCheck == null) || (toCheck instanceof NullObject))
      return defaultValue;
    else
      return toCheck;
  }


  public static Object getNotNull(Object toCheck)
  {
    return getNotNull(toCheck, "");
  }


  public static String getNotNullString(Object toCheck, String defaultValue)
  {
    if ((toCheck == null) || (toCheck instanceof NullObject))
      return defaultValue;
    else
      return toCheck.toString();
  }


  public static Object getNotNullString(Object toCheck)
  {
    return getNotNull(toCheck, "");
  }


  public static String getNotNullStringUC(Object toCheck, String defaultValue)
  {
    return new String(getNotNullString(toCheck, defaultValue)).toUpperCase();
  }


  public static Object getNotNullStringUC(Object toCheck)
  {
    return getNotNullStringUC(toCheck, "");
  }


  public static Object getNotEmpty(Object toCheck, Object defaultValue)
  {
    if ((toCheck == null) || (toCheck instanceof NullObject) || "".equals(toCheck))
      return defaultValue;
    else
      return toCheck;
  }


  public static String null2Blank(String toCheck)
  {
    if (toCheck == null)
      toCheck = "";

    return toCheck;
  }
}