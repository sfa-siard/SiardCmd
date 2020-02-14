package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;
import org.junit.*;

import ch.admin.bar.siard2.jdbc.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class MySqlBugTester extends BaseFromDbTester
{
  private static final String _sCATALOG = "bugdb";
  private static final String _sMYSQL_DB_URL;
  private static final String _sMYSQL_DB_USER;
  private static final String _sMYSQL_DB_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("mysql");
    _sMYSQL_DB_URL = MySqlDriver.getUrl(cp.getHost()+":"+cp.getPort()+"/"+_sCATALOG,true);
    _sMYSQL_DB_USER = "buguser";
    _sMYSQL_DB_PASSWORD = "bugpwd";
  }
  private static final String _sMYSQL_SIARD_FILE = "..\\Bugs\\479\\dvd_rental.siard";
  static
  {
    System.setProperty("java.util.logging.config.file", "etc\\debug.properties");
  }
  
  @Test
  public void testMySqlBugTo()
  {
    System.out.println("testMySqlBugToDb");
    try
    {
      System.out.println("Run SiardToDb");
      String[] args = new String[]{
          "-o",
          "-j:"+_sMYSQL_DB_URL,
          "-u:"+_sMYSQL_DB_USER,
          "-p:"+_sMYSQL_DB_PASSWORD,
          "-s:"+_sMYSQL_SIARD_FILE,
          "public", _sCATALOG
      };
      SiardToDb sfdb = new SiardToDb(args);
      assertEquals("SiardToDb failed!",0, sfdb.getReturn());
      System.out.println("-------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
  } /* testMySqlBugToDb */
  
}
