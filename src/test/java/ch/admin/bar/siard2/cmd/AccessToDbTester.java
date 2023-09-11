package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.*;

public class AccessToDbTester 
{
  private static final File _fileTEST_EMPTY_DATABASE;
  private static final File _fileTEST_ACCESS_DATABASE;
  private static final String _sACCESS_DB_URL;
  private static final String _sACCESS_DB_USER;
  private static final String _sACCESS_DB_PASSWORD;
  static
  {
    _fileTEST_EMPTY_DATABASE = new File("testfiles/testempty.accdb");
    _fileTEST_ACCESS_DATABASE = new File("tmp/testaccess.accdb");
    _sACCESS_DB_URL = AccessDriver.getUrl(_fileTEST_ACCESS_DATABASE.getPath());
    _sACCESS_DB_USER = "Admin";
    _sACCESS_DB_PASSWORD = "";
  }
  private static final String _sACCESS_SAMPLE_FILE = "testfiles/sample.siard";
  private static final String _sACCESS_SIARD_FILE = "testfiles/sfdbaccess.siard";
  
  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  @Test
  public void testAccessToAccess()
  {
    System.out.println("testAccessToAccess");
    try
    {
      FU.copy(_fileTEST_EMPTY_DATABASE,_fileTEST_ACCESS_DATABASE);
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sACCESS_DB_URL,
        "-u:"+_sACCESS_DB_USER,
        "-p:"+_sACCESS_DB_PASSWORD,
        "-s:"+_sACCESS_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testAccessToAccess */

  @Test
  public void testSampleToAccess()
  {
    System.out.println("testSampleToAccess");
    try
    {
      if (_fileTEST_ACCESS_DATABASE.exists())
        _fileTEST_ACCESS_DATABASE.delete();
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sACCESS_DB_URL,
        "-u:"+_sACCESS_DB_USER,
        "-p:"+_sACCESS_DB_PASSWORD,
        "-s:"+_sACCESS_SAMPLE_FILE,
        "SampleSchema", _sACCESS_DB_USER
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSampleToAccess */

}
