package ch.admin.bar.siard2.cmd;

import static org.junit.Assert.*;
import java.io.*;
import java.sql.*;
import org.junit.*;
import ch.admin.bar.siard2.jdbc.*;
import ch.admin.bar.siard2.jdbcx.*;
import ch.admin.bar.siard2.oracle.*;
import ch.enterag.utils.*;
import ch.enterag.utils.base.*;

public class OracleBugTester extends BaseFromDbTester
{
  private static final String _sORACLE_DB_URL;
  private static final String _sORACLE_DB_USER;
  private static final String _sORACLE_DB_PASSWORD;
  private static final String _sORACLE_DBA_USER;
  private static final String _sORACLE_DBA_PASSWORD;
  static
  {
    ConnectionProperties cp = new ConnectionProperties("oracle");
    _sORACLE_DB_URL = OracleDriver.getUrl(cp.getHost() + ":" + cp.getPort() + ":" + cp.getInstance());
    _sORACLE_DB_USER = cp.getUser();
    _sORACLE_DB_PASSWORD = cp.getPassword();
    _sORACLE_DBA_USER = cp.getDbaUser();
    _sORACLE_DBA_PASSWORD = cp.getDbaPassword();
  }
  private static final String _sORACLE_SIARD_FILE = "tmp/sfdboracle.siard";
  private static final String _sORACLE_METADATA_FILE = "tmp/sfdboracle.xml";
  private static final File _fileORACLE_SIARD_FINAL = new File("testfiles/sfdboracle.siard");

  @Test
  public void testOracleBugFrom()
  {
    System.out.println("testOracleBugFrom");
    try
    {
      OracleDataSource dsOracle = new OracleDataSource();
      dsOracle.setUrl(_sORACLE_DB_URL);
      dsOracle.setUser(_sORACLE_DBA_USER);
      dsOracle.setPassword(_sORACLE_DBA_PASSWORD);
      OracleConnection connOracle = (OracleConnection)dsOracle.getConnection();
      clearDatabase(connOracle,
        _sORACLE_DB_USER,
        TestOracleDatabase._sTEST_SCHEMA, 
        ch.admin.bar.siard2.oracle.TestSqlDatabase._sTEST_SCHEMA,
        null);
      /* drop and create the test database */
      System.out.println("Create TestSqlDatabase");
      new ch.admin.bar.siard2.oracle.TestSqlDatabase(connOracle);
      TestOracleDatabase.grantReadViews(connOracle, ch.admin.bar.siard2.oracle.TestSqlDatabase._sTEST_SCHEMA, _sORACLE_DB_USER);
      System.out.println("Create TestOracleDatabase");
      new TestOracleDatabase(connOracle);
      /* grant select only on views */
      TestOracleDatabase.grantReadViews(connOracle, TestOracleDatabase._sTEST_SCHEMA, _sORACLE_DB_USER);
      connOracle.close();
      String[] args = new String[]{
        "-o",
        "-v",
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
