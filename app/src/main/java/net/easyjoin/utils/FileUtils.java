package net.easyjoin.utils;


import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class FileUtils
{
  private static final String className = FileUtils.class.getName();
  public static final String VIDEO_EXT = ",mkv,mpeg,mpg,mp4,avi,asf,mov,qt,flv,swf,afl,avs,dl,fli,gl,m1v,m2v,mpa,mpe,moov,vdo,viv,vivo,rv,viv,vivo,vos,xdr,xsr,fmf,dl,dif,dv,fli,gl,isu,mjpg,asx,asx,avi,qtc,scm,movie,mv,";
  public static final String IMAGE_EXT = ",jpg,jpeg,gif,bmp,tif,tiff,png,jpe,bm,ras,rast,fif,flo,turbot,g3,ief,iefs,jfif,jfif-tbnl,jpe,jut,nap,naplps,pic,pict,jfif,x-png,mcf,dwg,dxf,svf,fpx,fpx,rf,rp,wbmp,xif,ras,dwg,dxf,svf,ico,art,jps,nif,niff,pcx,pct,pnm,pbm,pgm,pgm,ppm,qif,qti,qtif,rgb,tif,tiff,bmp,xbm,xbm,pm,xpm,xwd,xwd,xbm,xpm,";
  public static final String AUDIO_EXT = ",mp3,wav,aiff,aif,aifc,ogg,au,snd,it,funk,my,pfunk,pfunk,rmi,kar,mid,midi,mod,m2a,mp2,mpa,mpg,mpga,la,lma,s3m,tsi,tsp,qcp,voc,vox,snd,aif,aifc,aiff,au,gsd,gsm,jam,lam,mid,midi,mid,midi,mod,mp2,m3u,la,lma,ra,ram,rm,rmm,rmp,ra,rmp,rpm,sid,ra,vqf,vqe,vql,mjf,voc,xm,";
  public static final String DOCUMENT_EXT = ",pdf,odp,pps,ppt,pptx,ods,xls,xlsx,doc,docx,odt,rtf,txt,srt,";
  private static final double SPACE_KB = 1024;
  private static final double SPACE_MB = 1024 * SPACE_KB;
  private static final double SPACE_GB = 1024 * SPACE_MB;
  private static final double SPACE_TB = 1024 * SPACE_GB;
  private static List<File> internalDirs;
  private static List<File> externalDirs;
  public static final int ORDER_NAME_ASC = 1;
  public static final int ORDER_NAME_DESC = 2;
  public static final int ORDER_DATE_ASC = 3;
  public static final int ORDER_DATE_DESC = 4;


  public static String getRaw(Context context, String id)
  {
    String result = null;
    InputStream inputStream = null;

    try
    {
      Resources resources = context.getResources();
      inputStream = resources.openRawResource(MyResources.getRaw(id, context));

      byte[] b = new byte[inputStream.available()];
      inputStream.read(b);
      result = new String(b);
    }
    catch (Throwable t)
    {
      MyLog.e(className, "getRaw", t);
    }
    finally
    {
      if(inputStream != null)
      {
        try
        {
          inputStream.close();
        }
        catch (Throwable t){}
      }
    }

    return result;
  }

  public static Collection<File> getAllFiles(File dir, Date startTime, long maxTime) throws TimeoutException
  {
    if( (maxTime > -1) && ((startTime.getTime() + maxTime) < new Date().getTime()) )
    {
      throw new TimeoutException("timeout on retrieving files");
    }

    Set<File> files = new HashSet<File>();
    if (dir == null || dir.listFiles() == null)
    {
      return files;
    }
    for (File entry : dir.listFiles())
    {
      if (entry.isFile())
      {
        files.add(entry);
      }
      else
      {
        files.addAll(getAllFiles(entry, startTime, maxTime));
      }
    }
    return files;
  }

  public static double getTotalSize(Collection<File> files)
  {
    double totalSize = 0;

    Iterator<File> fileTreeIterator = files.iterator();
    while(fileTreeIterator.hasNext())
    {
      File file = fileTreeIterator.next();
      if(!file.isDirectory())
      {
        totalSize += file.length();
      }
    }

    return totalSize;
  }

  public static double getTotalSizePaths(Collection<String> files)
  {
    double totalSize = 0;

    Iterator<String> fileTreeIterator = files.iterator();
    while(fileTreeIterator.hasNext())
    {
      File file = new File(fileTreeIterator.next());
      if(!file.isDirectory())
      {
        totalSize += file.length();
      }
    }

    return totalSize;
  }

  public static File getInternalPath()
  {
    File internalPath = Environment.getDataDirectory();
    if ((internalPath == null) || (!internalPath.canRead()))
    {
      internalPath = Environment.getExternalStorageDirectory();
    }
    return internalPath;
  }

  public static List<File> getFolderTree(File node, StringBuilder stopRetrievingAllFiles, boolean includeNomedia, boolean excludeDot)
  {
    List<File> directories = new ArrayList();

    try
    {
      boolean retrieveFiles = true;

      if (!includeNomedia)
      {
        File nomediaFile = new File(node, ".nomedia");
        retrieveFiles = !nomediaFile.exists();
      }

      if (retrieveFiles)
      {
        if (node.isDirectory())
        {
          directories.add(node);
        }

        for (int i = 0; i < directories.size(); i++)
        {
          if (stopRetrievingAllFiles.toString().equals("1"))
          {
            break;
          }
          else
          {
            File dir = directories.get(i);

            retrieveFiles = true;
            if (!includeNomedia)
            {
              File nomediaFile = new File(dir, ".nomedia");
              retrieveFiles = !nomediaFile.exists();
            }

            if (retrieveFiles)
            {
              retrieveFiles = !dir.getName().startsWith(".");
            }

            if (retrieveFiles)
            {
              if (dir.canRead())
              {
                String[] subNote = dir.list();
                if (subNote != null)
                {
                  int insertIndex = i;
                  for (String filename : subNote)
                  {
                    if (stopRetrievingAllFiles.toString().equals("1"))
                    {
                      break;
                    }
                    else
                    {
                      File subNode = new File(dir, filename);
                      if ((subNode.isDirectory()) && ((!excludeDot) || (!subNode.getName().startsWith("."))))
                      {
                        directories.add(++insertIndex, subNode);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "getFolderTree", t);
    }

    return directories;
  }

  public static HashMap<String, Object> getFilesByType(File path, String typeExtensions, boolean includeNomedia, boolean excludeDot)
  {
    HashMap<String, Object> fileHash = new HashMap<>();
    List<File> fileList = new ArrayList<>();
    fileHash.put("files", fileList);

    try
    {
      boolean retrieveFiles = (path.exists() && path.canRead());

      if(retrieveFiles)
      {
        if (!includeNomedia)
        {
          File nomediaFile = new File(path, ".nomedia");
          retrieveFiles = !nomediaFile.exists();
        }

        if (retrieveFiles)
        {
          double totalSize = 0;
          fileHash.put("size", totalSize);
          File[] listFile = path.listFiles();
          if (listFile != null && listFile.length > 0)
          {
            for (int i = 0; i < listFile.length; i++)
            {
              if( (listFile[i].canRead()) && (!listFile[i].isDirectory())
                &&( (!excludeDot) || (!listFile[i].getName().startsWith(".")) ) )
              {
                String extension = getFileExtension(listFile[i].getName());
                if(extension != null)
                {
                  extension = "," + extension.toLowerCase() + ",";
                }
                //myList.toString().matches("\\[.*\\b" + word + "\\b.*]")
                if( (extension != null) &&  ( (typeExtensions == null) || (typeExtensions.contains(extension)) ) )
                {
                  fileList.add(listFile[i]);
                  totalSize += listFile[i].length();
                }
              }
            }
          }
          fileHash.put("size", totalSize);
        }
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "getFilesByType", t);
    }

    return fileHash;
  }

  public static HashMap<String, Object> getFoldersByType(File path, String typeExtensions, boolean includeNomedia, boolean excludeDot)
  {
    HashMap<String, Object> folderHash = new HashMap<>();

    try
    {
      boolean retrieveFiles = (path.exists() && path.canRead());

      if(retrieveFiles)
      {
        if (!includeNomedia)
        {
          File nomediaFile = new File(path, ".nomedia");
          retrieveFiles = !nomediaFile.exists();
        }

        if (retrieveFiles)
        {
          double totalSize = 0;
          folderHash.put("size", totalSize);
          int filesInPath = 0;
          folderHash.put("elements", filesInPath);
          File[] listFile = path.listFiles();
          if (listFile != null && listFile.length > 0)
          {
            for (int i = 0; i < listFile.length; i++)
            {
              if( (listFile[i].canRead()) && (!listFile[i].isDirectory())
                &&( (!excludeDot) || (!listFile[i].getName().startsWith(".")) ) )
              {
                String extension = getFileExtension(listFile[i].getName());
                if(extension != null)
                {
                  extension = "," + extension.toLowerCase() + ",";
                }

                if( (extension != null) &&  ( (typeExtensions == null) || (typeExtensions.contains(extension)) ) )
                {
                  filesInPath++;
                  totalSize += listFile[i].length();
                }
              }
            }
          }
          folderHash.put("size", totalSize);
          folderHash.put("elements", filesInPath);
        }
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "getFoldersByType", t);
    }

    return folderHash;
  }

  public static File uri2File(Context context, Uri uri)
  {
    String path = null;
    Cursor cursor = null;

    try
    {
      String uriString = uri.toString();
      if(uriString.startsWith("file://"))
      {
        path = uriString.substring(7);
      }
      else
      {
        cursor = context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex("_data");
        if(nameIndex != -1)
        {
          path = cursor.getString(nameIndex);
        }
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "uri2File", t);
    }
    finally
    {
      if( (cursor != null) && (!cursor.isClosed()) )
      {
        cursor.close();
      }
    }

    if(path == null)
    {
      return null;
    }
    else
    {
      try
      {
        String pathDecoded = URLDecoder.decode(path, "UTF-8");
        if(pathDecoded.length() != path.length())
        {
          path = pathDecoded;
        }
      }
      catch (Throwable t){}
      return new File(path);
    }
  }

  public static String getFileNameFromUri(Context context, Uri uri)
  {
    String fileName = null;
    Cursor cursor = null;

    try
    {
      cursor = context.getContentResolver().query(uri, null, null, null, null);
      cursor.moveToFirst();
      int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
      if(nameIndex != -1)
      {
        fileName = cursor.getString(nameIndex);
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "getFileNameFromUri", t);
    }
    finally
    {
      if( (cursor != null) && (!cursor.isClosed()) )
      {
        cursor.close();
      }
    }

    return fileName;
  }

  public static String getFileSizeFromUri(Context context, Uri uri)
  {
    String fileSize = null;
    Cursor cursor = null;

    try
    {
      cursor = context.getContentResolver().query(uri, null, null, null, null);
      cursor.moveToFirst();
      int nameIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
      if(nameIndex != -1)
      {
        fileSize = cursor.getString(nameIndex);
      }
    }
    catch (Throwable t)
    {
      MyLog.e(className, "getFileSizeFromUri", t);
    }
    finally
    {
      if( (cursor != null) && (!cursor.isClosed()) )
      {
        cursor.close();
      }
    }

    return fileSize;
  }

  public static String getFileExtension(String path)
  {
    String extension = null;
    try
    {
      int index1 = path.lastIndexOf(".");
      if (index1 != -1)
      {
        extension = path.substring(index1 + 1);
      }
    }
    catch (Throwable t){}
    return extension;
  }

  public static String getMiMeType(String filePath)
  {
    String type = null;
    try
    {
      int spaceIndex = -1;
      if ((spaceIndex = filePath.lastIndexOf(" ")) != -1)
      {
        filePath = filePath.substring(spaceIndex + 1);
      }

      String extension = getFileExtension(filePath);
      if (!Miscellaneous.isEmpty(extension))
      {
        type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
      }
    }
    catch (Throwable t){}
    return type;
  }

  public static boolean isImage(String fileName)
  {
    return isFileType(fileName, IMAGE_EXT);
  }

  public static boolean isAudio(String fileName)
  {
    return isFileType(fileName, AUDIO_EXT);
  }

  public static boolean isVideo(String fileName)
  {
    return isFileType(fileName, VIDEO_EXT);
  }

  public static boolean isDocument(String fileName)
  {
    return isFileType(fileName, DOCUMENT_EXT);
  }

  private static boolean isFileType(String fileName, String type)
  {
    String extension = getFileExtension(fileName);
    if(extension != null)
    {
      extension = "," + extension.toLowerCase() + ",";
      return (type.contains(extension));
    }
    else
    {
      return false;
    }
  }

  public static String getReadableSize(double sizeInBytes)
  {
    NumberFormat nf = new DecimalFormat();
    nf.setMaximumFractionDigits(2);

    try
    {
      if (sizeInBytes < SPACE_KB)
      {
        return nf.format(sizeInBytes) + " bytes";
      }
      else if (sizeInBytes < SPACE_MB)
      {
        return nf.format(sizeInBytes / SPACE_KB) + " KB";
      }
      else if (sizeInBytes < SPACE_GB)
      {
        return nf.format(sizeInBytes / SPACE_MB) + " MB";
      }
      else if (sizeInBytes < SPACE_TB)
      {
        return nf.format(sizeInBytes / SPACE_GB) + " GB";
      }
      else
      {
        return nf.format(sizeInBytes / SPACE_TB) + " TB";
      }
    }
    catch (Exception e)
    {
      return sizeInBytes + " bytes";
    }
  }

  public static String normalizePath(String path)
  {
    if(path != null)
    {
      path = path.trim();
      char lastChar = path.charAt(path.length() - 1);
      if( (lastChar != '\\') && (lastChar != '/') )
        path += File.separatorChar;
    }

    return path;
  }

  public static void getFiles(String path, List<File> listOfFiles, List<File> listOfFolders)
  {
    File filePath = new File(path);
    if(filePath != null)
    {
      String[] currentFiles = filePath.list();
      if(currentFiles != null)
      {
        for (int i = 0; i < currentFiles.length; i++)
        {
          File file = new File(path, currentFiles[i]);
          if(file != null)
          {
            if (file.isDirectory())
            {
              listOfFolders.add(file);
            }
            else
            {
              listOfFiles.add(file);
            }
          }
        }
      }
    }
  }

  public static String getParentFolder(String path, String fileSeparator)
  {
    String parentFolder = path;

    try
    {
      int index1 = path.lastIndexOf(fileSeparator);
      if ( ((index1 != -1)) && (index1 == (path.length() - 1)) )
      {
        path = path.substring(0, index1);
        index1 = path.lastIndexOf(fileSeparator);
      }

      if (index1 != -1)
      {
        parentFolder = path.substring(0, index1);
      }

      if( (parentFolder.endsWith(":")) && ("\\".equals(fileSeparator)) )
      {
        parentFolder += fileSeparator;
      }

      if(Miscellaneous.isEmpty(parentFolder))
      {
        parentFolder = fileSeparator;
      }
    }
    catch (Exception e)
    {
    }

    return parentFolder;
  }

  public static String removeSpecialChars4Windows(String fileName)
  {
    return fileName.replaceAll("[\\/?:*\"\"><|]+", "");
  }

  public static String removeSpecialChars4Android(String fileName)
  {
    return fileName.replaceAll("|\\\\\\\\\\?\\*<\\\\\":>\\+\\[]/'", "");
  }

  public static FileOutputStream getFileOutputStream(String filename, boolean append, Context context) throws Exception
  {
    FileOutputStream fileOutputStream = null;

    if (append)
    {
      fileOutputStream = context.openFileOutput(filename, Context.MODE_APPEND);
    }
    else
    {
      fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
    }

    return fileOutputStream;
  }
}
