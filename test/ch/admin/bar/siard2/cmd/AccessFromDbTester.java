package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;

import static org.junit.Assert.*;
import org.junit.Test;

import ch.enterag.utils.*;
import ch.admin.bar.siard2.jdbc.*;

public class AccessFromDbTester extends BaseFromDbTester
{
  private static final File _fileTEST_ACCESS_SOURCE;
  private static final File _fileTEST_ACCESS_DATABASE;
  private static final String _sACCESS_DB_URL;
  private static final String _sACCESS_DB_USER;
  private static final String _sACCESS_DB_PASSWORD;
  static
  {
    _fileTEST_ACCESS_SOURCE = new File("testfiles/testaccess.accdb");
    _fileTEST_ACCESS_DATABASE = new File("tmp/testaccess.accdb");
    _sACCESS_DB_URL = AccessDriver.getUrl(_fileTEST_ACCESS_DATABASE.getAbsolutePath()); 
    _sACCESS_DB_USER = "Admin";
    _sACCESS_DB_PASSWORD = "";
  }
  private static final String _sACCESS_SIARD_FILE = "tmp/sfdbaccess.siard";
  private static final String _sACCESS_METADATA_FILE = "tmp/sfdbaccess.xml";
  private static final File _fileACCESS_SIARD_FINAL = new File("testfiles/sfdbaccess.siard");

  @Test
  public void testAccessFromDb()
  {
    System.out.println("testAccessFromDb");
    try
    {
      FU.copy(_fileTEST_ACCESS_SOURCE, _fileTEST_ACCESS_DATABASE);
      String[] args = new String[]{
        "-o",
        "-j:"+_sACCESS_DB_URL,
        "-u:"+_sACCESS_DB_USER,
        "-p:"+_sACCESS_DB_PASSWORD,
        "-e:"+_sACCESS_METADATA_FILE,
        "-s:"+_sACCESS_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileACCESS_SIARD_FINAL.exists())
        FU.copy(new File(_sACCESS_SIARD_FILE), _fileACCESS_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testAccessFromDb */

//  @Test
//  public void testBug456()
//  {
//    System.out.println("testBug456");
//    try
//    {
//      FU.copy(new File("D:\\Projekte\\SIARD2\\Bugs\\456\\spatz\\spatz.accdb"),_fileTEST_ACCESS_DATABASE);
//      String[] args = new String[]{
//        "-o",
//        "-j:"+_sACCESS_DB_URL,
//        "-u:"+_sACCESS_DB_USER,
//        "-p:"+_sACCESS_DB_PASSWORD,
//        "-e:"+_sACCESS_METADATA_FILE,
//        "-s:"+_sACCESS_SIARD_FILE
//      };
//      SiardFromDb sfdb = new SiardFromDb(args);
//      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
//      System.out.println("---------------------------------------");
//    }
//    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
//    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
//    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
//  }
  
//@Test
//public void testBug20190123()
//{
//  System.out.println("testBug20190123");
//  try
//  {
//    FU.copy(new File("D:\\Projekte\\SIARD2\\Bugs\\20190123\\simpleDB\\simpleDB.accdb"),_fileTEST_ACCESS_DATABASE);
//    String[] args = new String[]{
//      "-o",
//      "-j:"+_sACCESS_DB_URL,
//      "-u:"+_sACCESS_DB_USER,
//      "-p:"+_sACCESS_DB_PASSWORD,
//      "-e:"+_sACCESS_METADATA_FILE,
//      "-s:"+_sACCESS_SIARD_FILE
//    };
//    SiardFromDb sfdb = new SiardFromDb(args);
//    assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
//    System.out.println("---------------------------------------");
//  }
//  catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
//  catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
//  catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
//}
  
  
}
