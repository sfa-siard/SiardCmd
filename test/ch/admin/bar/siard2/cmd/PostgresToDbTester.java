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

public class PostgresToDbTester extends BaseFromDbTester
{
  private static final String _sPOSTGRES_DB_URL;
  private static final String _sPOSTGRES_DB_USER;
  private static final String _sPOSTGRES_DB_PASSWORD;
  private static final String _sPOSTGRES_DBA_USER;
  private static final String _sPOSTGRES_DBA_PASSWORD;
  private static final String _sPOSTGRES_TEST_SCHEMA = "testschema";
  static
  {
    ConnectionProperties cp = new ConnectionProperties("postgres");
    _sPOSTGRES_DB_URL = PostgresDriver.getUrl(cp.getHost() + ":" + cp.getPort() + "/" + cp.getCatalog());
    _sPOSTGRES_DB_USER = cp.getUser();
    _sPOSTGRES_DB_PASSWORD = cp.getPassword();
    _sPOSTGRES_DBA_USER = cp.getDbaUser();
    _sPOSTGRES_DBA_PASSWORD = cp.getDbaPassword();
  }


  private static final String TESTFILES_SAMPLE_2_2_SIARD = "testfiles/sample-2.2.siard";

  private static final String SAMPLE_2_1_SIARD = "testfiles/sample.siard"; // TODO: rename file
  private static final String _sPOSTGRES_SIARD_FILE = "testfiles/sfdbpostgres.siard";

  @Test
  public void testPostgresToPostgres() throws SQLException, IOException {
    System.out.println("testPostgresToPostgres");
    // now upload sample
    String[] args = new String[]{
            "-o",
            "-j:" + _sPOSTGRES_DB_URL,
            "-u:" + _sPOSTGRES_DBA_USER,
            "-p:" + _sPOSTGRES_DBA_PASSWORD,
            "-s:" + _sPOSTGRES_SIARD_FILE,
            "pg_catalog", "testschema",
            "testpgschema", "testschema"
    };
    SiardToDb stdb = new SiardToDb(args);
    assertEquals("SiardToDb failed!", 0, stdb.getReturn());
    System.out.println("---------------------------------------");
  }

  @Test
  public void testSample22ToPostgres() throws SQLException, IOException {
    archiveToPostgres(TESTFILES_SAMPLE_2_2_SIARD);
  }

  @Test
  public void testSample21ToPostgres() throws SQLException, IOException {
    archiveToPostgres(SAMPLE_2_1_SIARD);
  }

  private void archiveToPostgres(String siardFile) throws SQLException, IOException {
    PostgresDataSource dsPostgres = new PostgresDataSource();
    dsPostgres.setUrl(_sPOSTGRES_DB_URL);
    dsPostgres.setUser(_sPOSTGRES_DBA_USER);
    dsPostgres.setPassword(_sPOSTGRES_DBA_PASSWORD);
    PostgresConnection connPostgres = (PostgresConnection) dsPostgres.getConnection();
    connPostgres.setAutoCommit(false);
    try {
      Statement stmt = connPostgres.createStatement();
      stmt.executeUpdate("DROP SCHEMA " + _sPOSTGRES_TEST_SCHEMA + " CASCADE");
      stmt.close();
      connPostgres.commit();
    } catch (SQLException se) {
      System.out.println(EU.getExceptionMessage(se));
      /* terminate transaction */
      try {
        connPostgres.rollback();
      } catch (SQLException seRollback) {
        System.out.println("Rollback failed with " + EU.getExceptionMessage(seRollback));
      }
    }
    Statement stmt = connPostgres.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
    String sSql = "CREATE SCHEMA " + _sPOSTGRES_TEST_SCHEMA + " AUTHORIZATION " + _sPOSTGRES_DB_USER;
    int iResult = stmt.executeUpdate(sSql);
    stmt.close();
    if (iResult == 0)
      connPostgres.commit();
    else {
      connPostgres.rollback();
      fail(sSql + " failed!");
    }
    TestPostgresDatabase.grantSchemaUser(connPostgres, _sPOSTGRES_TEST_SCHEMA, _sPOSTGRES_DB_USER);
    connPostgres.close();
    /* now upload sample */

    String[] args = new String[]{
            "-o",
            "-j:" + _sPOSTGRES_DB_URL,
            "-u:" + _sPOSTGRES_DB_USER,
            "-p:" + _sPOSTGRES_DB_PASSWORD,
            "-s:" + siardFile,
            "pg_catalog", "testschema",
            "SampleSchema", "testschema"
    };
    SiardToDb stdb = new SiardToDb(args);
    assertEquals("SiardToDb failed!", 0, stdb.getReturn());
    System.out.println("---------------------------------------");
  }

}
