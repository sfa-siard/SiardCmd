package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class OracleBugTester extends BaseFromDbTester
{
  private static final String _sORACLE_DB_URL;
  private static final String _sORACLE_DB_USER;
  private static final String _sORACLE_DB_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("oracle");
    _sORACLE_DB_URL = OracleDriver.getUrl(cp.getHost() + ":" + cp.getPort() + ":" + cp.getInstance());
    _sORACLE_DB_USER = "DIGIT_B2";
    _sORACLE_DB_PASSWORD = "DIGIT_B2";
  }
  private static final String _sORACLE_SIARD_FILE = "tmp/sfdbsybil.siard";
  private static final String _sORACLE_METADATA_FILE = "tmp/sfdbsybil.xml";
  private static final File _fileORACLE_SIARD_FINAL = new File("testfiles/sfdbsybil.siard");

  @Test
  public void testOracleBugFrom()
  {
    System.out.println("testOracleBugFrom");
    try
    {
      String[] args = new String[]{
        "-o",
        "-j:"+_sORACLE_DB_URL,
        "-u:"+_sORACLE_DB_USER,
        "-p:"+_sORACLE_DB_PASSWORD,
        "-e:"+_sORACLE_METADATA_FILE,
        "-s:"+_sORACLE_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileORACLE_SIARD_FINAL.exists())
        FU.copy(new File(_sORACLE_SIARD_FILE),_fileORACLE_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testOracleFromDb */
  
}
