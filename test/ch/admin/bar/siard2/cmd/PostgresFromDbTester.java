package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.postgres.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class PostgresFromDbTester extends BaseFromDbTester
{
  private static final String _sPOSTGRES_DB_URL;
  private static final String _sPOSTGRES_DB_USER;
  private static final String _sPOSTGRES_DB_PASSWORD;
  private static final String _sPOSTGRES_DBA_USER;
  private static final String _sPOSTGRES_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("postgres");
    _sPOSTGRES_DB_URL = PostgresDriver.getUrl(cp.getHost() + ":" + cp.getPort() + "/" + cp.getCatalog());
    _sPOSTGRES_DB_USER = cp.getUser();
    _sPOSTGRES_DB_PASSWORD = cp.getPassword();
    _sPOSTGRES_DBA_USER = cp.getDbaUser();
    _sPOSTGRES_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sPOSTGRES_SIARD_FILE = "tmp/sfdbpostgres.siard";
  private static final String _sPOSTGRES_METADATA_FILE = "tmp/sfdbpostgres.xml";
  private static final File _filePOSTGRES_SIARD_FINAL = new File("testfiles/sfdbpostgres.siard");

  @Test
  public void testPostgresFromDb()
  {
    System.out.println("testPostgresFromDb");
    try
    {
      PostgresDataSource dsPostgres = new PostgresDataSource();
      dsPostgres.setUrl(_sPOSTGRES_DB_URL);
      dsPostgres.setUser(_sPOSTGRES_DBA_USER);
      dsPostgres.setPassword(_sPOSTGRES_DBA_PASSWORD);
      PostgresConnection connPostgres = (PostgresConnection)dsPostgres.getConnection();
      clearDatabase(connPostgres,
        _sPOSTGRES_DB_USER,
        TestPostgresDatabase._sTEST_SCHEMA, 
        ch.admin.bar.siard2.postgres.TestSqlDatabase._sTEST_SCHEMA,
        null);
      /* drop and create the test database */
      System.out.println("Create TestSqlDatabase");
      new ch.admin.bar.siard2.postgres.TestSqlDatabase(connPostgres,_sPOSTGRES_DB_USER);
      TestPostgresDatabase.grantSchemaUser(connPostgres, TestSqlDatabase._sTEST_SCHEMA, _sPOSTGRES_DB_USER);
      System.out.println("Create TestPostgresDatabase");
      new TestPostgresDatabase(connPostgres,_sPOSTGRES_DB_USER);
      TestPostgresDatabase.grantSchemaUser(connPostgres, TestPostgresDatabase._sTEST_SCHEMA, _sPOSTGRES_DB_USER);
      connPostgres.close();
      String[] args = new String[]{
        "-o",
        "-j:"+_sPOSTGRES_DB_URL,
        "-u:"+_sPOSTGRES_DB_USER,
        "-p:"+_sPOSTGRES_DB_PASSWORD,
        "-e:"+_sPOSTGRES_METADATA_FILE,
        "-s:"+_sPOSTGRES_SIARD_FILE
      };
      SiardFromDb sfdb = new SiardFromDb(args);
      assertEquals("SiardFromDb failed!",0, sfdb.getReturn());
      if (!_filePOSTGRES_SIARD_FINAL.exists())
        FU.copy(new File(_sPOSTGRES_SIARD_FILE),_filePOSTGRES_SIARD_FINAL);
      System.out.println("---------------------------------------");
    }
    catch(IOException ie) { fail(EU.getExceptionMessage(ie)); }
    catch(SQLException se) { fail(EU.getExceptionMessage(se)); }
    catch(ClassNotFoundException cnfe) { fail(EU.getExceptionMessage(cnfe)); }
  } /* testPostgresFromDb */
  
}
