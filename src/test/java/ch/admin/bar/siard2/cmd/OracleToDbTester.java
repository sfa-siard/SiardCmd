package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import ch.admin.bar.siard2.jdbc.OracleDriver;
import ch.enterag.utils.EU;
import ch.enterag.utils.base.ConnectionProperties;

public class OracleToDbTester 
{
  private static final String _sORACLE_DB_URL;
  private static final String _sORACLE_DB_USER;
  private static final String _sORACLE_DB_PASSWORD;
  private static final String _sORACLE_DBA_USER;
  private static final String _sORACLE_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("oracle");
    _sORACLE_DB_URL = OracleDriver.getUrl(cp.getHost() + ":" + cp.getPort() + "/" + cp.getInstance());
    _sORACLE_DB_USER = cp.getUser();
    _sORACLE_DB_PASSWORD = cp.getPassword();
    _sORACLE_DBA_USER = cp.getDbaUser();
    _sORACLE_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sORACLE_SAMPLE_FILE = "testfiles/sample.siard";
  private static final String _sORACLE_SIARD_FILE = "testfiles/sfdboracle.siard";

  /* In JUnit testing getMainJar-relative addressing is not useful */
  static
  {
    System.setProperty("ch.admin.bar.siard2.cmd.drivers","etc/jdbcdrivers.properties");
  }
  
  @Test
  public void testOracleToOracle()
  {
    System.out.println("testOracleToOracle");
    try
    {
      // now upload sample
      String[] args = new String[]{
        "-o",
        "-j:"+_sORACLE_DB_URL,
        "-u:"+_sORACLE_DBA_USER,
        "-p:"+_sORACLE_DBA_PASSWORD,
        "-s:"+_sORACLE_SIARD_FILE
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testOracleToOracle */

  @Test
  public void testSampleToOracle()
  {
    System.out.println("testSampleToOracle");
    try
    {
      /* now upload sample */
      String[] args = new String[]{
        "-o",
        "-j:"+_sORACLE_DB_URL,
        "-u:"+_sORACLE_DB_USER,
        "-p:"+_sORACLE_DB_PASSWORD,
        "-s:"+_sORACLE_SAMPLE_FILE,
        "SampleSchema", _sORACLE_DB_USER
      };
      SiardToDb stdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, stdb.getReturn());
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testSampleToOracle */

}
