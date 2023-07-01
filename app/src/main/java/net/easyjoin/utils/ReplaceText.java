package net.easyjoin.utils;



public final class ReplaceText
{

  public static String replace(String stringToConvert, String changeThis, String inThis, String fromHere, String toThere)
  {
    stringToConvert = Miscellaneous.null2Blank(stringToConvert);
    changeThis = Miscellaneous.null2Blank(changeThis);
    inThis = Miscellaneous.null2Blank(inThis);
    fromHere = Miscellaneous.null2Blank(fromHere);
    toThere = Miscellaneous.null2Blank(toThere);
    int toThereLenght = toThere.length();
    
    StringBuilder sentinel = null;
    StringBuilder sentinel4 = null;
    String sentinel2 = null;
    String sentinel3 = null;
    
    //posizione del primo fromHere
    int posfromHere = 0;
    //posizione del primo toThere
    int postoThere = 0;
    //posizione del primo changeThis
    int poschangeThis = 0;
    
    
    //se vogliamo sostituire una qualsiasi stringa che si trova tra fromHere e toThere
    if((changeThis == null) || (changeThis.equalsIgnoreCase("")) )
    {
      int inThisLength = inThis.length();
      sentinel = new StringBuilder("");
      int fromHereLength = fromHere.length();
      
      while ((posfromHere = stringToConvert.indexOf(fromHere, postoThere)) != -1)
      {
        if ((postoThere = stringToConvert.indexOf(toThere, posfromHere)) != -1)
        {
          if(postoThere != posfromHere)
          {
            sentinel.setLength(0);
            sentinel.append(stringToConvert.substring(0, posfromHere + fromHereLength));
            sentinel.append(inThis);
            sentinel.append(stringToConvert.substring(postoThere));
            stringToConvert = sentinel.toString();
            postoThere = posfromHere + fromHereLength + inThisLength + toThereLenght;
          }
          else
            break;
        }
        else
          break;
      }
    }
    else
    {
      int changeThisLength = changeThis.length();
      
      while ((posfromHere = stringToConvert.indexOf(fromHere, postoThere)) != -1)
      {
        if ((postoThere = stringToConvert.indexOf(toThere, posfromHere)) != -1)
        {
          if (postoThere != posfromHere)
          {
            sentinel2 = stringToConvert.substring(posfromHere, postoThere);
            sentinel3 = stringToConvert.substring(postoThere);
            
            while ((poschangeThis = sentinel2.indexOf(changeThis, poschangeThis)) != -1)
            {
              sentinel4 = new StringBuilder("");
              sentinel4.append(sentinel2.substring(0, poschangeThis));
              sentinel4.append(inThis);
              sentinel4.append(sentinel2.substring(poschangeThis + changeThisLength));
              sentinel2 = sentinel4.toString();
              sentinel4 = null;
              poschangeThis ++;
            }
            sentinel = new StringBuilder("");
            sentinel.append(stringToConvert.substring(0, posfromHere));
            sentinel.append(sentinel2);
            sentinel.append(sentinel3);
            stringToConvert = sentinel.toString();
          }
          else
            break;
        }
        else
          break;
      }
    }
    
    return stringToConvert;
  }
  

  public static String replace(String stringToConvert, String changeThis, String inThis)
  {
    stringToConvert = Miscellaneous.null2Blank(stringToConvert);
    changeThis = Miscellaneous.null2Blank(changeThis);
    inThis = Miscellaneous.null2Blank(inThis);
    
    StringBuilder sentinel = new StringBuilder("");
    
    int pos_attuale = stringToConvert.indexOf(changeThis);
    int changeThisLength = changeThis.length();
    int startPos = 0;
    
    while (pos_attuale != -1)
    {
      sentinel.append(stringToConvert.substring(startPos, pos_attuale));
      sentinel.append(inThis);
      startPos = pos_attuale + changeThisLength;
      pos_attuale = stringToConvert.indexOf(changeThis, startPos);
    }
    sentinel.append(stringToConvert.substring(startPos));
    
    return sentinel.toString();
  }
  
}