package ch.admin.bar.siard2.cmd;
import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;

import ch.enterag.utils.EU;
import ch.enterag.utils.FU;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.base.*;

public class AwFromDbTester extends BaseFromDbTester 
{
  private static final String _sAW_DB_URL;
  private static final String _sAW_DB_USER;
  private static final String _sAW_DB_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("aw");
    _sAW_DB_URL = MsSqlDriver.getUrl(cp.getHost()+"\\"+cp.getCatalog()+":"+cp.getPort());
    _sAW_DB_USER = cp.getUser();
    _sAW_DB_PASSWORD = cp.getPassword();
  }
  private static final String _sAW_SIARD_FILE = "tmp/sfdbaw.siard";
  private static final String _sAW_METADATA_FILE = "tmp/sfdbaw.xml";
  private static final File _fileAW_SIARD_FINAL = new File("testfiles/sfdbaw.siard");

  @Test
  public void testAdventureWorksFromDb()
  {
    System.out.println("testAdventureWorksFromDb");
    try
    {
      String[] args = new String[]{
        "-o",
        "-j:"+_sAW_DB_URL,
        "-u:"+_sAW_DB_USER,
        "-p:"+_sAW_DB_PASSWORD,
        "-e:"+_sAW_METADATA_FILE,
        "-s:"+_sAW_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileAW_SIARD_FINAL.exists())
        FU.copy(new File(_sAW_SIARD_FILE),_fileAW_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testAdventureWorksFromDb */

}
