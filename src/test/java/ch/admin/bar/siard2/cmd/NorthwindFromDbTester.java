package ch.admin.bar.siard2.cmd;
import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;

import ch.enterag.utils.EU;
import ch.enterag.utils.FU;
import org.junit.*;

import ch.admin.bar.siard2.jdbc.*;

public class NorthwindFromDbTester extends BaseFromDbTester 
{
  private static final File _fileTEST_NORTHWIND_SOURCE;
  private static final File _fileTEST_NORTHWIND_DATABASE;
  private static final String _sNORTHWIND_DB_URL;
  private static final String _sNORTHWIND_DB_USER;
  private static final String _sNORTHWIND_DB_PASSWORD;
  static
  {
    _fileTEST_NORTHWIND_SOURCE = new File("testfiles/Northwind.accdb");
    _fileTEST_NORTHWIND_DATABASE = new File("tmp/Northwind.accdb");
    _sNORTHWIND_DB_URL = AccessDriver.getUrl(_fileTEST_NORTHWIND_DATABASE.getAbsolutePath()); 
    _sNORTHWIND_DB_USER = "Admin";
    _sNORTHWIND_DB_PASSWORD = "";
  }
  private static final String _sNORTHWIND_SIARD_FILE = "tmp/Northwind.siard";
  private static final String _sNORTHWIND_METADATA_FILE = "tmp/Northwind.xml";
  private static final File _fileNORTHWIND_SIARD_FINAL = new File("testfiles/Northwind.siard");

  @Test
  public void testAccessFromDb()
  {
    System.out.println("testAccessFromDb");
    try
    {
      FU.copy(_fileTEST_NORTHWIND_SOURCE, _fileTEST_NORTHWIND_DATABASE);
      String[] args = new String[]{
        "-o",
        "-j:"+_sNORTHWIND_DB_URL,
        "-u:"+_sNORTHWIND_DB_USER,
        "-p:"+_sNORTHWIND_DB_PASSWORD,
        "-e:"+_sNORTHWIND_METADATA_FILE,
        "-s:"+_sNORTHWIND_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileNORTHWIND_SIARD_FINAL.exists())
        FU.copy(new File(_sNORTHWIND_SIARD_FILE), _fileNORTHWIND_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testAccessFromDb */

}
