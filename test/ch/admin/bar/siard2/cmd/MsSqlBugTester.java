package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class MsSqlBugTester 
{
  private static final String _sMSSQL_DB_URL;
  private static final String _sMSSQL_DB_USER;
  private static final String _sMSSQL_DB_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("mssql");
    _sMSSQL_DB_URL = MsSqlDriver.getUrl(cp.getHost()+":"+cp.getPort()+";databaseName=bugdb");
    _sMSSQL_DB_USER = "buglogin";
    _sMSSQL_DB_PASSWORD = "bugloginpwd";
  }
  private static final String _sMSSQL_SIARD_FILE = "../Bugs/456/spatz1.siard";
  private static final String _sMSSQL_LARGE_METADATA_FILE = "tmp/sfdblarge.xml";
	private static final String _sMSSQL_LARGE_SIARD_FILE = "tmp/sfdblarge.siard";
	private static final File _fileMSSQL_LARGE_SIARD_FINAL = new File("testfiles/sfdblarge.siard");
  	  
  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  @Test
  public void testBugToMsSql()
  {
    System.out.println("testBugToMsSql");
    try
    {
      /* now upload bug */
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-s:"+_sMSSQL_SIARD_FILE,
        "Admin", "dbo"
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMsSqlToMsSql */

  @Test
  public void testBugFromMsSql()
  {
    System.out.println("testBugFromMsSql");
    try
    {
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-e:"+_sMSSQL_LARGE_METADATA_FILE,
        "-s:"+_sMSSQL_LARGE_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileMSSQL_LARGE_SIARD_FINAL.exists())
        FU.copy(new File(_sMSSQL_LARGE_SIARD_FILE),_fileMSSQL_LARGE_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testMsSqlFromDb */

}
