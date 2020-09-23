package ch.admin.bar.siard2.cmd;

import java.io.*;
import java.sql.*;

import static org.junit.Assert.*;
import org.junit.Test;

import ch.enterag.utils.*;
import ch.admin.bar.siard2.jdbc.*;

public class AccessBugTester extends BaseFromDbTester
{
  private static final File _fileTEST_ACCESS_SOURCE;
  private static final File _fileTEST_ACCESS_DATABASE;
  private static final String _sACCESS_DB_URL;
  private static final String _sACCESS_DB_USER;
  private static final String _sACCESS_DB_PASSWORD;
  static
  {
    _fileTEST_ACCESS_SOURCE = new File("../Bugs/Issue17/NTArchiv/NTArchiv.accdb");
    _fileTEST_ACCESS_DATABASE = new File("tmp/NTArchiv.accdb");
    _sACCESS_DB_URL = AccessDriver.getUrl(_fileTEST_ACCESS_DATABASE.getAbsolutePath()); 
    _sACCESS_DB_USER = "Admin";
    _sACCESS_DB_PASSWORD = "";
  }
  private static final String _sACCESS_SIARD_FILE = "tmp/sfdbaccess.siard";
  private static final String _sACCESS_METADATA_FILE = "tmp/sfdbaccess.xml";

  @Test
  public void testAccessBugFromDb()
  {
    System.out.println("testAccessBugFromDb");
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
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testAccessFromDb */

}
