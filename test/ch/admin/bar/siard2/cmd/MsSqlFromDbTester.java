package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;

import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.mssql.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class MsSqlFromDbTester extends BaseFromDbTester
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
  private static final String _sMSSQL_SIARD_FILE = "tmp/sfdbmssql.siard";
  private static final String _sMSSQL_METADATA_FILE = "tmp/sfdbmssql.xml";
  private static final File _fileMSSQL_SIARD_FINAL = new File("testfiles/sfdbmssql.siard");

  @Test
  public void testMsSqlFromDb()
  {
    System.out.println("testMsSqlFromDb");
    try
    {
      MsSqlDataSource dsMsSql = new MsSqlDataSource();
      dsMsSql.setUrl(_sMSSQL_DB_URL);
      dsMsSql.setUser(_sMSSQL_DB_USER);
      dsMsSql.setPassword(_sMSSQL_DB_PASSWORD);
      MsSqlConnection connMsSql = (MsSqlConnection)dsMsSql.getConnection();
      /* drop and create the test database */
      clearDatabase(connMsSql,
        "dbo",
        TestMsSqlDatabase._sTEST_SCHEMA, 
        ch.admin.bar.siard2.mssql.TestSqlDatabase._sTEST_SCHEMA,
        null);
      System.out.println("Create TestSqlDatabase");
      new ch.admin.bar.siard2.mssql.TestSqlDatabase(connMsSql);
      System.out.println("Create TestMsSqlDatabase");
      new TestMsSqlDatabase(connMsSql);
      connMsSql.close();
      String[] args = new String[]{
        "-o",
        "-j:"+_sMSSQL_DB_URL,
        "-u:"+_sMSSQL_DB_USER,
        "-p:"+_sMSSQL_DB_PASSWORD,
        "-e:"+_sMSSQL_METADATA_FILE,
        "-s:"+_sMSSQL_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_fileMSSQL_SIARD_FINAL.exists())
        FU.copy(new File(_sMSSQL_SIARD_FILE),_fileMSSQL_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testMsSqlFromDb */

}
