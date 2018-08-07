package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;

import static org.junit.Assert.*;
import org.junit.Test;

import ch.enterag.utils.*;
import ch.enterag.utils.lang.*;
import ch.admin.bar.siard2.access.*;
import ch.admin.bar.siard2.jdbc.*;

public class AccessFromDbTester extends BaseFromDbTester
{
  private static final File _fileTEST_EMPTY_DATABASE;
  private static final File _fileTEST_ACCESS_SOURCE;
  private static final File _fileTEST_ACCESS_DATABASE;
  private static final String _sACCESS_DB_URL;
  private static final String _sACCESS_DB_USER;
  private static final String _sACCESS_DB_PASSWORD;
  static
  {
    _fileTEST_EMPTY_DATABASE = new File("testfiles/testempty.accdb");
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
      FU.copy(_fileTEST_EMPTY_DATABASE,_fileTEST_ACCESS_DATABASE);
      /* The JDBC-ODBC bridge could still be used until JAVA 8 using
       * an extract from the JAVA 7 run-time library and the JdbcOdbc.dll.
       * Now that is blocked by the split packages prohibition.
       * So we use the test database originally created under JAVA 8.
       * If we ever want more controlled features in the test database 
       * we shall be in trouble ... (have to use JAVA 7 or 8!)
       */
      if (Execute.isOsWindows() && Execute.isJavaVersionLessThan("9"))
        new TestAccessDatabase(_fileTEST_ACCESS_DATABASE);
      else
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

  @Test
  public void testBug456()
  {
    System.out.println("testBug456");
    try
    {
      FU.copy(new File("D:\\Projekte\\SIARD2\\Bugs\\456\\spatz\\spatz.accdb"),_fileTEST_ACCESS_DATABASE);
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
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } 
}
