package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import ch.admin.bar.siard2.cmd.SiardToDb;
import ch.admin.bar.siard2.jdbc.Db2Driver;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;

public class Db2ToDbTester 
{
  private static final String _sDB2_DB_URL;
  private static final String _sDB2_DB_USER;
  private static final String _sDB2_DB_PASSWORD;
  private static final String _sDB2_DBA_USER;
  private static final String _sDB2_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("db2");
    _sDB2_DB_URL = Db2Driver.getUrl(cp.getHost()+":"+cp.getPort()+"/"+cp.getCatalog());
    _sDB2_DB_USER = cp.getUser();
    _sDB2_DB_PASSWORD = cp.getPassword();
    _sDB2_DBA_USER = cp.getDbaUser();
    _sDB2_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sDB2_SAMPLE_FILE = "testfiles/sample.siard";
  private static final String _sDB2_SIARD_FILE = "testfiles/sfdbdb2.siard";
  

  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  @Test
  public void testDb2ToDb2()
  {
    System.out.println("testDb2ToDb2");
    try
    {
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sDB2_DB_URL,
        "-u:"+_sDB2_DBA_USER,
        "-p:"+_sDB2_DBA_PASSWORD,
        "-s:"+_sDB2_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testDb2ToDb2 */

  @Test
  public void testSampleToDb2()
  {
    System.out.println("testSampleToDb2");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sDB2_DB_URL,
        "-u:"+_sDB2_DB_USER,
        "-p:"+_sDB2_DB_PASSWORD,
        "-s:"+_sDB2_SAMPLE_FILE,
        "SampleSchema", _sDB2_DB_USER
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSampleToDb2 */
  
}
