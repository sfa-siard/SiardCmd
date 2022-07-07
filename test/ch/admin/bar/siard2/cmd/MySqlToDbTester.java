package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;

import static org.junit.Assert.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class MySqlToDbTester 
{
  private static final String _sMYSQL_DB_CATALOG;
  private static final String _sMYSQL_DB_URL;
  private static final String _sMYSQL_DB_USER;
  private static final String _sMYSQL_DB_PASSWORD;
  private static final String _sMYSQL_DBA_USER;
  private static final String _sMYSQL_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("mysql");
    _sMYSQL_DB_CATALOG = cp.getCatalog();
    _sMYSQL_DB_URL = MySqlDriver.getUrl(cp.getHost()+":"+cp.getPort()+"/"+cp.getCatalog(),true);
    _sMYSQL_DB_USER = cp.getUser();
    _sMYSQL_DB_PASSWORD = cp.getPassword();
    _sMYSQL_DBA_USER = cp.getDbaUser();
    _sMYSQL_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sMYSQL_SAMPLE_FILE = "testfiles/test.siard";
  private static final String _sMYSQL_SIARD_FILE = "testfiles/sfdbmysql.siard";
  private static final File _fileMYSQL_EXT_LOBS = new File("testfiles/lobs");
  private static final File _fileMYSQL_TMP_LOBS = new File("tmp/lobs");

  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }

  @Test
  public void testMySqlToMySql()
  {
    System.out.println("testMySqlToMySql");
    try
    {
      FU.copyFiles(_fileMYSQL_EXT_LOBS, _fileMYSQL_TMP_LOBS, true);
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DBA_USER,
        "-p:"+_sMYSQL_DBA_PASSWORD,
        "-s:"+_sMYSQL_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMySqlToMySql */

  @Test
  public void testSampleToMySql()
  {
    System.out.println("testSampleToMySql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMYSQL_DB_URL,
        "-u:"+_sMYSQL_DB_USER,
        "-p:"+_sMYSQL_DB_PASSWORD,
        "-s:"+_sMYSQL_SAMPLE_FILE,
        "SampleSchema", _sMYSQL_DB_CATALOG
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSampleToMySql */

}
