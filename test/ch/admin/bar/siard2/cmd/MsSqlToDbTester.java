package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import ch.admin.bar.siard2.jdbc.MsSqlDriver;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;

public class MsSqlToDbTester 
{
  private static final String _sMSSQL_DB_URL;
  private static final String _sMSSQL_DB_USER;
  private static final String _sMSSQL_DB_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("mssql");
    _sMSSQL_DB_URL = MsSqlDriver.getUrl(cp.getHost()+"\\"+cp.getCatalog()+":"+cp.getPort());
    _sMSSQL_DB_USER = cp.getUser();
    _sMSSQL_DB_PASSWORD = cp.getPassword();
  }
  // private static final String _sMSSQL_SIARD_FILE = "testfiles/sfdbmssql.siard";
  private static final String _sMSSQL_SAMPLE_FILE = "testfiles/sample.siard";
  private static final String _sMSSQL_SIARD_FILE = "testfiles/sfdbmssql.siard";
  
  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  @Test
  public void testMsSqlToMsSql()
  {
    System.out.println("testMsSqlToMsSql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-s:"+_sMSSQL_SIARD_FILE,
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMsSqlToMsSql */

  @Test
  public void testSampleToMsSql()
  {
    System.out.println("testSampleToMsSql");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-s:"+_sMSSQL_SAMPLE_FILE,
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSampleToMsSql */

}
